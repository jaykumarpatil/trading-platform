package com.trading.tantra.engine.trading.service;

import com.trading.tantra.common.exception.ServiceException;
import com.trading.tantra.engine.trading.model.Trade;
import com.trading.tantra.engine.trading.model.TradeStatus;
import com.trading.tantra.engine.trading.repository.TradeRepository;
import com.trading.tantra.order.entity.Order;
import com.trading.tantra.order.entity.OrderStatus;
import com.trading.tantra.order.service.OrderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TradeService {
    private static final Logger logger = LoggerFactory.getLogger(TradeService.class);
    private static final String TRADE_EVENTS_TOPIC = "trade.events";

    private final TradeRepository tradeRepository;
    private final OrderService orderService;
    private final KafkaTemplate<String, Trade> kafkaTemplate;

    @Autowired
    public TradeService(
            TradeRepository tradeRepository,
            OrderService orderService,
            KafkaTemplate<String, Trade> kafkaTemplate) {
        this.tradeRepository = tradeRepository;
        this.orderService = orderService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public Trade executeTrade(Order buyOrder, Order sellOrder, BigDecimal executionPrice, BigDecimal quantity) {
        logger.debug("Executing trade between buy order {} and sell order {}", buyOrder.getId(), sellOrder.getId());

        validateTradeExecution(buyOrder, sellOrder, executionPrice, quantity);

        try {
            // Create new trade
            Trade trade = createTrade(buyOrder, sellOrder, executionPrice, quantity);
            Trade savedTrade = tradeRepository.save(trade);

            // Update order statuses
            updateOrderStatus(buyOrder, quantity);
            updateOrderStatus(sellOrder, quantity);

            // Publish trade event
            publishTradeEvent(savedTrade);

            logger.info("Trade executed successfully: {}", savedTrade.getId());
            return savedTrade;
        } catch (Exception e) {
            logger.error("Trade execution failed", e);
            throw new ServiceException("Failed to execute trade", e);
        }
    }

    public List<Trade> getTradesByOrderId(Long orderId) {
        return tradeRepository.findByBuyOrderIdOrSellOrderIdOrderByExecutionTimeDesc(orderId, orderId);
    }

    public List<Trade> getTradesBySymbol(String symbol, LocalDateTime from, LocalDateTime to) {
        return tradeRepository.findBySymbolAndExecutionTimeBetweenOrderByExecutionTimeDesc(symbol, from, to);
    }

    private Trade createTrade(Order buyOrder, Order sellOrder, BigDecimal executionPrice, BigDecimal quantity) {
        Trade trade = new Trade();
        trade.setTradeId(generateTradeId());
        trade.setBuyOrderId(buyOrder.getId());
        trade.setSellOrderId(sellOrder.getId());
        trade.setSymbol(buyOrder.getSymbol());
        trade.setExecutionPrice(executionPrice);
        trade.setQuantity(quantity);
        trade.setExecutionTime(LocalDateTime.now());
        trade.setStatus(TradeStatus.EXECUTED);
        return trade;
    }

    private void validateTradeExecution(Order buyOrder, Order sellOrder, BigDecimal executionPrice, BigDecimal quantity) {
        if (buyOrder == null || sellOrder == null) {
            throw new ServiceException("Both buy and sell orders must be provided");
        }

        if (!buyOrder.getSymbol().equals(sellOrder.getSymbol())) {
            throw new ServiceException("Orders must be for the same symbol");
        }

        if (executionPrice.compareTo(BigDecimal.ZERO) <= 0 || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("Execution price and quantity must be positive");
        }

        validateOrderStatus(buyOrder);
        validateOrderStatus(sellOrder);
    }

    private void validateOrderStatus(Order order) {
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PARTIAL_FILLED) {
            throw new ServiceException("Invalid order status for trade execution: " + order.getStatus());
        }
    }

    private void updateOrderStatus(Order order, BigDecimal executedQuantity) {
        BigDecimal remainingQuantity = order.getRemainingQuantity().subtract(executedQuantity);
        OrderStatus newStatus = determineOrderStatus(remainingQuantity);
        orderService.updateOrderStatus(order.getId(), newStatus);
    }

    private OrderStatus determineOrderStatus(BigDecimal remainingQuantity) {
        return remainingQuantity.compareTo(BigDecimal.ZERO) == 0 ? OrderStatus.FILLED : OrderStatus.PARTIAL_FILLED;
    }

    private void publishTradeEvent(Trade trade) {
        kafkaTemplate.send(TRADE_EVENTS_TOPIC, trade.getSymbol(), trade);
    }

    private String generateTradeId() {
        return UUID.randomUUID().toString();
    }
}
