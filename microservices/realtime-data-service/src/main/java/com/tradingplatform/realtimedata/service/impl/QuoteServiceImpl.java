package com.tradingplatform.realtimedata.service.impl;

import com.tradingplatform.common.exception.ServiceException;
import com.tradingplatform.realtimedata.entity.Quote;
import com.tradingplatform.realtimedata.repository.QuoteRepository;
import com.tradingplatform.realtimedata.service.QuoteService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QuoteServiceImpl implements QuoteService {
    private static final Logger logger = LoggerFactory.getLogger(QuoteServiceImpl.class);

    private final QuoteRepository quoteRepository;

    @Autowired
    public QuoteServiceImpl(QuoteRepository quoteRepository) {
        this.quoteRepository = quoteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Quote> getQuoteHistory(String symbol, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        logger.debug("Fetching quote history for symbol: {} from {} to {}", symbol, from, to);
        return quoteRepository.findBySymbolAndTimestampBetween(symbol, from, to, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quote> getLatestQuotes(String symbol, int limit) {
        logger.debug("Fetching latest {} quotes for symbol: {}", limit, symbol);
        return quoteRepository.findTopNBySymbolOrderByTimestampDesc(symbol, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Quote> getLatestQuote(String symbol) {
        logger.debug("Fetching latest quote for symbol: {}", symbol);
        return quoteRepository.findFirstBySymbolOrderByTimestampDesc(symbol);
    }

    @Override
    @Transactional
    public Quote saveQuote(Quote quote) {
        logger.debug("Saving quote for symbol: {}", quote.getSymbol());
        
        if (quote.getTimestamp() == null) {
            quote.setTimestamp(LocalDateTime.now());
        }
        
        try {
            Quote savedQuote = quoteRepository.save(quote);
            logger.info("Quote saved successfully for symbol: {}", quote.getSymbol());
            return savedQuote;
        } catch (Exception e) {
            logger.error("Failed to save quote", e);
            throw new ServiceException("Failed to save quote", e);
        }
    }

    @Override
    @Transactional
    public List<Quote> saveQuotes(List<Quote> quotes) {
        logger.debug("Saving batch of {} quotes", quotes.size());
        
        LocalDateTime now = LocalDateTime.now();
        quotes.forEach(quote -> {
            if (quote.getTimestamp() == null) {
                quote.setTimestamp(now);
            }
        });
        
        try {
            List<Quote> savedQuotes = quoteRepository.saveAll(quotes);
            logger.info("Successfully saved {} quotes", savedQuotes.size());
            return savedQuotes;
        } catch (Exception e) {
            logger.error("Failed to save quotes batch", e);
            throw new ServiceException("Failed to save quotes batch", e);
        }
    }

    @Override
    @Transactional
    public void deleteOldQuotes(String symbol, LocalDateTime before) {
        logger.debug("Deleting quotes for symbol: {} before: {}", symbol, before);
        
        try {
            int deleted = quoteRepository.deleteBySymbolAndTimestampBefore(symbol, before);
            logger.info("Deleted {} old quotes for symbol: {}", deleted, symbol);
        } catch (Exception e) {
            logger.error("Failed to delete old quotes", e);
            throw new ServiceException("Failed to delete old quotes", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quote> getQuotesBySymbols(List<String> symbols, LocalDateTime from, LocalDateTime to) {
        logger.debug("Fetching quotes for {} symbols from {} to {}", symbols.size(), from, to);
        return quoteRepository.findBySymbolInAndTimestampBetweenOrderByTimestampDesc(symbols, from, to);
    }
}
