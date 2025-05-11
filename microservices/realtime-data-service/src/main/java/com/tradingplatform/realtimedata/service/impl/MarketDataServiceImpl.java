package com.tradingplatform.realtimedata.service.impl;

import com.tradingplatform.common.exception.ServiceException;
import com.tradingplatform.realtimedata.dto.MarketDataMessage;
import com.tradingplatform.realtimedata.dto.PriceUpdate;
import com.tradingplatform.realtimedata.entity.MarketData;
import com.tradingplatform.realtimedata.entity.Quote;
import com.tradingplatform.realtimedata.repository.MarketDataRepository;
import com.tradingplatform.realtimedata.repository.QuoteRepository;
import com.tradingplatform.realtimedata.service.MarketDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MarketDataServiceImpl implements MarketDataService {
    private static final Logger logger = LoggerFactory.getLogger(MarketDataServiceImpl.class);
    private static final String MARKET_DATA_TOPIC = "market.data.updates";

    private final MarketDataRepository marketDataRepository;
    private final QuoteRepository quoteRepository;
    private final KafkaTemplate<String, MarketDataMessage> kafkaTemplate;

    @Autowired
    public MarketDataServiceImpl(
            MarketDataRepository marketDataRepository,
            QuoteRepository quoteRepository,
            KafkaTemplate<String, MarketDataMessage> kafkaTemplate) {
        this.marketDataRepository = marketDataRepository;
        this.quoteRepository = quoteRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public void processMarketDataUpdate(PriceUpdate priceUpdate) {
        logger.debug("Processing market data update for symbol: {}", priceUpdate.getSymbol());
        
        try {
            // Update or create market data entry
            MarketData marketData = marketDataRepository
                    .findBySymbol(priceUpdate.getSymbol())
                    .orElse(new MarketData());
            
            updateMarketData(marketData, priceUpdate);
            MarketData savedData = marketDataRepository.save(marketData);
            
            // Create quote history
            Quote quote = createQuote(priceUpdate);
            quoteRepository.save(quote);
            
            // Publish update to Kafka
            publishMarketDataUpdate(savedData);
            
            logger.info("Market data updated for symbol: {}", priceUpdate.getSymbol());
        } catch (Exception e) {
            logger.error("Error processing market data update", e);
            throw new ServiceException("Failed to process market data update", e);
        }
    }

    @Override
    @Cacheable(value = "marketData", key = "#symbol")
    public Optional<MarketData> getLatestMarketData(String symbol) {
        logger.debug("Fetching latest market data for symbol: {}", symbol);
        return marketDataRepository.findBySymbol(symbol);
    }

    @Override
    public List<Quote> getHistoricalData(String symbol, LocalDateTime from, LocalDateTime to) {
        logger.debug("Fetching historical data for symbol: {} from: {} to: {}", symbol, from, to);
        return quoteRepository.findBySymbolAndTimestampBetweenOrderByTimestampDesc(symbol, from, to);
    }

    @Override
    @Transactional
    @CacheEvict(value = "marketData", key = "#symbol")
    public void invalidateMarketData(String symbol) {
        logger.debug("Invalidating market data cache for symbol: {}", symbol);
        marketDataRepository.deleteBySymbol(symbol);
    }

    private void updateMarketData(MarketData marketData, PriceUpdate update) {
        marketData.setSymbol(update.getSymbol());
        marketData.setLastPrice(update.getPrice());
        marketData.setVolume(update.getVolume());
        marketData.setBidPrice(update.getBidPrice());
        marketData.setAskPrice(update.getAskPrice());
        marketData.setHighPrice(calculateHighPrice(marketData.getHighPrice(), update.getPrice()));
        marketData.setLowPrice(calculateLowPrice(marketData.getLowPrice(), update.getPrice()));
        marketData.setLastUpdated(LocalDateTime.now());
    }

    private Quote createQuote(PriceUpdate update) {
        Quote quote = new Quote();
        quote.setSymbol(update.getSymbol());
        quote.setPrice(update.getPrice());
        quote.setVolume(update.getVolume());
        quote.setBidPrice(update.getBidPrice());
        quote.setAskPrice(update.getAskPrice());
        quote.setTimestamp(LocalDateTime.now());
        return quote;
    }

    private void publishMarketDataUpdate(MarketData marketData) {
        MarketDataMessage message = convertToMessage(marketData);
        kafkaTemplate.send(MARKET_DATA_TOPIC, marketData.getSymbol(), message);
    }

    private MarketDataMessage convertToMessage(MarketData data) {
        return MarketDataMessage.builder()
                .symbol(data.getSymbol())
                .price(data.getLastPrice())
                .bidPrice(data.getBidPrice())
                .askPrice(data.getAskPrice())
                .volume(data.getVolume())
                .timestamp(data.getLastUpdated())
                .build();
    }

    private BigDecimal calculateHighPrice(BigDecimal currentHigh, BigDecimal newPrice) {
        if (currentHigh == null) {
            return newPrice;
        }
        return newPrice.compareTo(currentHigh) > 0 ? newPrice : currentHigh;
    }

    private BigDecimal calculateLowPrice(BigDecimal currentLow, BigDecimal newPrice) {
        if (currentLow == null) {
            return newPrice;
        }
        return newPrice.compareTo(currentLow) < 0 ? newPrice : currentLow;
    }
}
