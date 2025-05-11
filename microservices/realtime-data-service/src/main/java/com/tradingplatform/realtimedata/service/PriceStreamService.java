package com.tradingplatform.realtimedata.service;

import com.tradingplatform.common.exception.ServiceException;
import com.tradingplatform.realtimedata.dto.PriceUpdate;
import com.tradingplatform.realtimedata.service.MarketDataService;
import com.tradingplatform.realtimedata.service.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PriceStreamService {
    private static final Logger logger = LoggerFactory.getLogger(PriceStreamService.class);
    private static final String PRICE_UPDATES_TOPIC = "market.price.updates";

    private final MarketDataService marketDataService;
    private final QuoteService quoteService;
    private final Map<String, Many<PriceUpdate>> priceStreams;

    @Autowired
    public PriceStreamService(MarketDataService marketDataService, QuoteService quoteService) {
        this.marketDataService = marketDataService;
        this.quoteService = quoteService;
        this.priceStreams = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void init() {
        logger.info("Initializing price stream service");
    }

    public Flux<PriceUpdate> subscribeToPriceUpdates(String symbol) {
        logger.debug("New subscription request for symbol: {}", symbol);
        
        return priceStreams.computeIfAbsent(symbol, this::createPriceStream)
                .asFlux()
                .publishOn(Schedulers.boundedElastic())
                .doOnSubscribe(sub -> logger.info("New subscriber for symbol: {}", symbol))
                .doOnCancel(() -> handleUnsubscribe(symbol))
                .doOnError(error -> logger.error("Error in price stream for symbol: {}", symbol, error));
    }

    @KafkaListener(topics = PRICE_UPDATES_TOPIC, groupId = "price-stream-service")
    public void handlePriceUpdate(PriceUpdate update) {
        logger.debug("Received price update for symbol: {}", update.getSymbol());
        
        try {
            // Update market data
            marketDataService.processMarketDataUpdate(update);
            
            // Publish to stream if there are subscribers
            Many<PriceUpdate> sink = priceStreams.get(update.getSymbol());
            if (sink != null) {
                sink.tryEmitNext(update);
            }
            
        } catch (Exception e) {
            logger.error("Failed to process price update", e);
            throw new ServiceException("Failed to process price update", e);
        }
    }

    public void unsubscribe(String symbol) {
        logger.debug("Unsubscribe request for symbol: {}", symbol);
        handleUnsubscribe(symbol);
    }

    public boolean hasSubscribers(String symbol) {
        Many<PriceUpdate> sink = priceStreams.get(symbol);
        return sink != null && sink.currentSubscriberCount() > 0;
    }

    private Many<PriceUpdate> createPriceStream(String symbol) {
        logger.debug("Creating new price stream for symbol: {}", symbol);
        return Sinks.many().multicast().onBackpressureBuffer();
    }

    private void handleUnsubscribe(String symbol) {
        logger.debug("Handling unsubscribe for symbol: {}", symbol);
        
        Many<PriceUpdate> sink = priceStreams.get(symbol);
        if (sink != null && sink.currentSubscriberCount() == 0) {
            priceStreams.remove(symbol);
            logger.info("Removed price stream for symbol: {} (no subscribers)", symbol);
        }
    }

    public void closeAllStreams() {
        logger.info("Closing all price streams");
        
        priceStreams.forEach((symbol, sink) -> {
            sink.tryEmitComplete();
            priceStreams.remove(symbol);
        });
    }
}
