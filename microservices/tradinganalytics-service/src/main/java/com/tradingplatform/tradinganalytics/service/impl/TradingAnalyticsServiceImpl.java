package com.tradingplatform.tradinganalytics.service.impl;

import com.tradingplatform.common.exception.ServiceException;
import com.tradingplatform.tradinganalytics.model.AnalysisResult;
import com.tradingplatform.tradinganalytics.model.TradingPattern;
import com.tradingplatform.tradinganalytics.model.MarketTrend;
import com.tradingplatform.tradinganalytics.repository.AnalysisRepository;
import com.tradingplatform.tradinganalytics.service.TradingAnalyticsService;
import com.tradingplatform.realtimedata.service.MarketDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TradingAnalyticsServiceImpl implements TradingAnalyticsService {
    private static final Logger logger = LoggerFactory.getLogger(TradingAnalyticsServiceImpl.class);

    private final AnalysisRepository analysisRepository;
    private final MarketDataService marketDataService;

    @Autowired
    public TradingAnalyticsServiceImpl(
            AnalysisRepository analysisRepository,
            MarketDataService marketDataService) {
        this.analysisRepository = analysisRepository;
        this.marketDataService = marketDataService;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "marketAnalysis", key = "#symbol")
    public AnalysisResult analyzeMarketTrends(String symbol, LocalDateTime from, LocalDateTime to) {
        logger.debug("Analyzing market trends for symbol: {} from {} to {}", symbol, from, to);
        
        try {
            // Get historical market data
            List<com.tradingplatform.realtimedata.entity.Quote> quotes = 
                marketDataService.getHistoricalData(symbol, from, to);
            
            if (quotes.isEmpty()) {
                throw new ServiceException("No market data available for analysis");
            }
            
            // Perform technical analysis
            MarketTrend trend = analyzeTrend(quotes);
            List<TradingPattern> patterns = identifyPatterns(quotes);
            Map<String, BigDecimal> indicators = calculateTechnicalIndicators(quotes);
            
            // Create analysis result
            AnalysisResult result = new AnalysisResult();
            result.setSymbol(symbol);
            result.setTrend(trend);
            result.setPatterns(patterns);
            result.setIndicators(indicators);
            result.setAnalysisTime(LocalDateTime.now());
            
            // Save analysis
            analysisRepository.save(result);
            
            logger.info("Market analysis completed for symbol: {}", symbol);
            return result;
            
        } catch (Exception e) {
            logger.error("Failed to analyze market trends", e);
            throw new ServiceException("Market analysis failed", e);
        }
    }

    @Override
    public List<TradingPattern> identifyTradingPatterns(String symbol, LocalDateTime from, LocalDateTime to) {
        logger.debug("Identifying trading patterns for symbol: {} from {} to {}", symbol, from, to);
        
        List<com.tradingplatform.realtimedata.entity.Quote> quotes = 
            marketDataService.getHistoricalData(symbol, from, to);
        
        return identifyPatterns(quotes);
    }

    @Override
    public Map<String, BigDecimal> calculateTechnicalIndicators(String symbol) {
        logger.debug("Calculating technical indicators for symbol: {}", symbol);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        
        List<com.tradingplatform.realtimedata.entity.Quote> quotes = 
            marketDataService.getHistoricalData(symbol, thirtyDaysAgo, now);
        
        return calculateTechnicalIndicators(quotes);
    }

    private MarketTrend analyzeTrend(List<com.tradingplatform.realtimedata.entity.Quote> quotes) {
        if (quotes.size() < 2) {
            return MarketTrend.NEUTRAL;
        }

        // Simple trend analysis using linear regression
        double[] prices = quotes.stream()
                .mapToDouble(q -> q.getPrice().doubleValue())
                .toArray();
        
        double slope = calculateSlope(prices);
        
        if (slope > 0.01) {
            return MarketTrend.BULLISH;
        } else if (slope < -0.01) {
            return MarketTrend.BEARISH;
        } else {
            return MarketTrend.NEUTRAL;
        }
    }

    private List<TradingPattern> identifyPatterns(List<com.tradingplatform.realtimedata.entity.Quote> quotes) {
        return quotes.stream()
                .collect(Collectors.groupingBy(q -> q.getTimestamp().toLocalDate()))
                .values()
                .stream()
                .map(this::analyzePatternForDay)
                .filter(pattern -> pattern != null)
                .collect(Collectors.toList());
    }

    private TradingPattern analyzePatternForDay(List<com.tradingplatform.realtimedata.entity.Quote> dayQuotes) {
        if (dayQuotes.size() < 2) {
            return null;
        }

        // Implement pattern recognition logic here
        // This is a simplified example
        BigDecimal open = dayQuotes.get(0).getPrice();
        BigDecimal close = dayQuotes.get(dayQuotes.size() - 1).getPrice();
        BigDecimal high = dayQuotes.stream()
                .map(com.tradingplatform.realtimedata.entity.Quote::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        BigDecimal low = dayQuotes.stream()
                .map(com.tradingplatform.realtimedata.entity.Quote::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        return new TradingPattern(
                dayQuotes.get(0).getTimestamp(),
                "CANDLESTICK",
                Map.of(
                    "open", open,
                    "close", close,
                    "high", high,
                    "low", low
                )
        );
    }

    private Map<String, BigDecimal> calculateTechnicalIndicators(List<com.tradingplatform.realtimedata.entity.Quote> quotes) {
        if (quotes.isEmpty()) {
            return Map.of();
        }

        BigDecimal[] prices = quotes.stream()
                .map(com.tradingplatform.realtimedata.entity.Quote::getPrice)
                .toArray(BigDecimal[]::new);

        return Map.of(
            "SMA_20", calculateSMA(prices, 20),
            "EMA_20", calculateEMA(prices, 20),
            "RSI_14", calculateRSI(prices, 14),
            "VOLATILITY", calculateVolatility(prices)
        );
    }

    private double calculateSlope(double[] values) {
        int n = values.length;
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumXX = 0;

        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += values[i];
            sumXY += i * values[i];
            sumXX += i * i;
        }

        return (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
    }

    private BigDecimal calculateSMA(BigDecimal[] prices, int period) {
        if (prices.length < period) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = BigDecimal.ZERO;
        for (int i = prices.length - period; i < prices.length; i++) {
            sum = sum.add(prices[i]);
        }
        
        return sum.divide(BigDecimal.valueOf(period), 4, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal calculateEMA(BigDecimal[] prices, int period) {
        if (prices.length < period) {
            return BigDecimal.ZERO;
        }

        BigDecimal multiplier = BigDecimal.valueOf(2.0 / (period + 1));
        BigDecimal ema = calculateSMA(prices, period);

        for (int i = prices.length - period + 1; i < prices.length; i++) {
            ema = prices[i].multiply(multiplier)
                    .add(ema.multiply(BigDecimal.ONE.subtract(multiplier)));
        }

        return ema;
    }

    private BigDecimal calculateRSI(BigDecimal[] prices, int period) {
        if (prices.length < period + 1) {
            return BigDecimal.ZERO;
        }

        BigDecimal[] gains = new BigDecimal[period];
        BigDecimal[] losses = new BigDecimal[period];

        for (int i = 1; i <= period; i++) {
            BigDecimal diff = prices[prices.length - i].subtract(prices[prices.length - i - 1]);
            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                gains[i - 1] = diff;
                losses[i - 1] = BigDecimal.ZERO;
            } else {
                gains[i - 1] = BigDecimal.ZERO;
                losses[i - 1] = diff.abs();
            }
        }

        BigDecimal avgGain = calculateAverage(gains);
        BigDecimal avgLoss = calculateAverage(losses);

        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }

        BigDecimal rs = avgGain.divide(avgLoss, 4, BigDecimal.ROUND_HALF_UP);
        return BigDecimal.valueOf(100)
                .subtract(BigDecimal.valueOf(100)
                        .divide(BigDecimal.ONE.add(rs), 4, BigDecimal.ROUND_HALF_UP));
    }

    private BigDecimal calculateVolatility(BigDecimal[] prices) {
        if (prices.length < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal mean = calculateAverage(prices);

        for (BigDecimal price : prices) {
            BigDecimal diff = price.subtract(mean);
            sum = sum.add(diff.multiply(diff));
        }

        return BigDecimal.valueOf(Math.sqrt(sum.divide(
                BigDecimal.valueOf(prices.length), 4, BigDecimal.ROUND_HALF_UP)
                .doubleValue()));
    }

    private BigDecimal calculateAverage(BigDecimal[] values) {
        if (values.length == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            sum = sum.add(value);
        }

        return sum.divide(BigDecimal.valueOf(values.length), 4, BigDecimal.ROUND_HALF_UP);
    }
}
