package com.tradingplatform.portfolio.service.impl;

import com.tradingplatform.common.exception.ServiceException;
import com.tradingplatform.portfolio.entity.Portfolio;
import com.tradingplatform.portfolio.entity.Position;
import com.tradingplatform.portfolio.entity.Transaction;
import com.tradingplatform.portfolio.event.PortfolioEvent;
import com.tradingplatform.portfolio.messaging.PortfolioEventPublisher;
import com.tradingplatform.portfolio.repository.PortfolioRepository;
import com.tradingplatform.portfolio.repository.PositionRepository;
import com.tradingplatform.portfolio.repository.TransactionRepository;
import com.tradingplatform.portfolio.service.PortfolioService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PortfolioServiceImpl implements PortfolioService {
    private static final Logger logger = LoggerFactory.getLogger(PortfolioServiceImpl.class);

    private final PortfolioRepository portfolioRepository;
    private final PositionRepository positionRepository;
    private final TransactionRepository transactionRepository;
    private final PortfolioEventPublisher eventPublisher;

    @Autowired
    public PortfolioServiceImpl(
            PortfolioRepository portfolioRepository,
            PositionRepository positionRepository,
            TransactionRepository transactionRepository,
            PortfolioEventPublisher eventPublisher) {
        this.portfolioRepository = portfolioRepository;
        this.positionRepository = positionRepository;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Portfolio createPortfolio(Long userId, String name) {
        logger.debug("Creating portfolio for user: {} with name: {}", userId, name);
        
        // Check if user already has a portfolio with this name
        if (portfolioRepository.existsByUserIdAndName(userId, name)) {
            throw new ServiceException("Portfolio with name already exists: " + name);
        }
        
        Portfolio portfolio = new Portfolio();
        portfolio.setUserId(userId);
        portfolio.setName(name);
        portfolio.setCreatedAt(LocalDateTime.now());
        portfolio.setTotalValue(BigDecimal.ZERO);
        
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        logger.info("Created portfolio with ID: {}", savedPortfolio.getId());
        return savedPortfolio;
    }

    @Override
    @Cacheable(value = "portfolios", key = "#portfolioId")
    public Optional<Portfolio> getPortfolio(Long portfolioId) {
        logger.debug("Fetching portfolio: {}", portfolioId);
        return portfolioRepository.findById(portfolioId);
    }

    @Override
    public List<Portfolio> getUserPortfolios(Long userId) {
        logger.debug("Fetching portfolios for user: {}", userId);
        return portfolioRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Position addPosition(Long portfolioId, String symbol, BigDecimal quantity, BigDecimal price) {
        logger.debug("Adding position to portfolio {}: {} x {} @ {}", portfolioId, symbol, quantity, price);
        
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ServiceException("Portfolio not found: " + portfolioId));
        
        // Check for existing position
        Optional<Position> existingPosition = positionRepository.findByPortfolioIdAndSymbol(portfolioId, symbol);
        Position position;
        
        if (existingPosition.isPresent()) {
            position = updateExistingPosition(existingPosition.get(), quantity, price);
        } else {
            position = createNewPosition(portfolio, symbol, quantity, price);
        }
        
        // Record transaction
        recordTransaction(position, quantity, price);
        
        // Update portfolio value
        updatePortfolioValue(portfolio);
        
        // Publish event
        publishPortfolioUpdate(portfolio);
        
        return position;
    }

    @Override
    @Transactional
    @CacheEvict(value = "portfolios", key = "#portfolioId")
    public void removePosition(Long portfolioId, String symbol) {
        logger.debug("Removing position from portfolio {}: {}", portfolioId, symbol);
        
        Position position = positionRepository.findByPortfolioIdAndSymbol(portfolioId, symbol)
                .orElseThrow(() -> new ServiceException("Position not found for symbol: " + symbol));
        
        positionRepository.delete(position);
        
        // Update portfolio value
        Portfolio portfolio = position.getPortfolio();
        updatePortfolioValue(portfolio);
        
        // Publish event
        publishPortfolioUpdate(portfolio);
    }

    @Override
    public List<Position> getPortfolioPositions(Long portfolioId) {
        logger.debug("Fetching positions for portfolio: {}", portfolioId);
        return positionRepository.findByPortfolioId(portfolioId);
    }

    @Override
    public List<Transaction> getPortfolioTransactions(Long portfolioId, LocalDateTime from, LocalDateTime to) {
        logger.debug("Fetching transactions for portfolio {} from {} to {}", portfolioId, from, to);
        return transactionRepository.findByPortfolioIdAndTimestampBetweenOrderByTimestampDesc(portfolioId, from, to);
    }

    private Position updateExistingPosition(Position position, BigDecimal quantity, BigDecimal price) {
        BigDecimal newQuantity = position.getQuantity().add(quantity);
        
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("Invalid quantity: Would result in negative position");
        }
        
        // Calculate new average price
        if (newQuantity.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalCost = position.getQuantity().multiply(position.getAveragePrice())
                    .add(quantity.multiply(price));
            position.setAveragePrice(totalCost.divide(newQuantity, 4, BigDecimal.ROUND_HALF_UP));
        }
        
        position.setQuantity(newQuantity);
        position.setLastUpdated(LocalDateTime.now());
        
        return positionRepository.save(position);
    }

    private Position createNewPosition(Portfolio portfolio, String symbol, BigDecimal quantity, BigDecimal price) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("Initial position quantity must be positive");
        }
        
        Position position = new Position();
        position.setPortfolio(portfolio);
        position.setSymbol(symbol);
        position.setQuantity(quantity);
        position.setAveragePrice(price);
        position.setCreatedAt(LocalDateTime.now());
        position.setLastUpdated(LocalDateTime.now());
        
        return positionRepository.save(position);
    }

    private void recordTransaction(Position position, BigDecimal quantity, BigDecimal price) {
        Transaction transaction = new Transaction();
        transaction.setPortfolioId(position.getPortfolio().getId());
        transaction.setSymbol(position.getSymbol());
        transaction.setQuantity(quantity);
        transaction.setPrice(price);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    private void updatePortfolioValue(Portfolio portfolio) {
        BigDecimal totalValue = positionRepository.findByPortfolioId(portfolio.getId()).stream()
                .map(position -> position.getQuantity().multiply(position.getAveragePrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        portfolio.setTotalValue(totalValue);
        portfolio.setLastUpdated(LocalDateTime.now());
        portfolioRepository.save(portfolio);
    }

    private void publishPortfolioUpdate(Portfolio portfolio) {
        PortfolioEvent event = new PortfolioEvent();
        event.setPortfolioId(portfolio.getId());
        event.setUserId(portfolio.getUserId());
        event.setTotalValue(portfolio.getTotalValue());
        event.setTimestamp(LocalDateTime.now());
        
        eventPublisher.publishPortfolioEvent(event);
    }
}
