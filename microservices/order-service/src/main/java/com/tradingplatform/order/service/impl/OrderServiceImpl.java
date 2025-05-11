package com.tradingplatform.order.service.impl;

import com.tradingplatform.common.exception.ServiceException;
import com.tradingplatform.order.entity.Order;
import com.tradingplatform.order.entity.OrderAudit;
import com.tradingplatform.order.entity.OrderStatus;
import com.tradingplatform.order.event.OrderCreatedEvent;
import com.tradingplatform.order.event.OrderEvent;
import com.tradingplatform.order.messaging.KafkaEventPublisher;
import com.tradingplatform.order.repository.OrderAuditRepository;
import com.tradingplatform.order.repository.OrderRepository;
import com.tradingplatform.order.service.OrderService;
import com.tradingplatform.order.service.OrderValidationService;

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
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderAuditRepository auditRepository;
    private final OrderValidationService validationService;
    private final KafkaEventPublisher eventPublisher;

    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderAuditRepository auditRepository,
            OrderValidationService validationService,
            KafkaEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.auditRepository = auditRepository;
        this.validationService = validationService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        logger.debug("Creating order: {}", order);
        
        // Validate order
        validationService.validateOrder(order);
        
        // Set initial order status
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        // Create audit entry
        OrderAudit audit = new OrderAudit();
        audit.setOrderId(savedOrder.getId());
        audit.setStatus(OrderStatus.PENDING);
        audit.setTimestamp(LocalDateTime.now());
        audit.setDescription("Order created");
        auditRepository.save(audit);
        
        // Publish order created event
        OrderEvent event = new OrderCreatedEvent(savedOrder);
        eventPublisher.publishOrderEvent(event);
        
        logger.info("Order created with ID: {}", savedOrder.getId());
        return savedOrder;
    }

    @Override
    @Cacheable(value = "orders", key = "#orderId")
    public Optional<Order> getOrder(Long orderId) {
        logger.debug("Fetching order with ID: {}", orderId);
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        logger.debug("Fetching orders for user ID: {}", userId);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "orders", key = "#orderId")
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        logger.debug("Updating order {} status to {}", orderId, newStatus);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException("Order not found: " + orderId));
        
        // Validate status transition
        validateStatusTransition(order.getStatus(), newStatus);
        
        // Update order status
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        Order updatedOrder = orderRepository.save(order);
        
        // Create audit entry
        OrderAudit audit = new OrderAudit();
        audit.setOrderId(orderId);
        audit.setStatus(newStatus);
        audit.setTimestamp(LocalDateTime.now());
        audit.setDescription("Order status updated to: " + newStatus);
        auditRepository.save(audit);
        
        // Publish status update event
        OrderEvent event = new OrderEvent(updatedOrder);
        eventPublisher.publishOrderEvent(event);
        
        logger.info("Order {} status updated to {}", orderId, newStatus);
        return updatedOrder;
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        logger.debug("Cancelling order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException("Order not found: " + orderId));
        
        // Validate if order can be cancelled
        if (!canCancel(order)) {
            throw new ServiceException("Order cannot be cancelled in current state: " + order.getStatus());
        }
        
        // Update order status to CANCELLED
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        
        // Create audit entry
        OrderAudit audit = new OrderAudit();
        audit.setOrderId(orderId);
        audit.setStatus(OrderStatus.CANCELLED);
        audit.setTimestamp(LocalDateTime.now());
        audit.setDescription("Order cancelled");
        auditRepository.save(audit);
        
        // Publish cancellation event
        OrderEvent event = new OrderEvent(order);
        eventPublisher.publishOrderEvent(event);
        
        logger.info("Order {} cancelled", orderId);
    }

    private boolean canCancel(Order order) {
        return order.getStatus() == OrderStatus.PENDING || 
               order.getStatus() == OrderStatus.PARTIAL_FILLED;
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Implement status transition validation rules
        if (currentStatus == OrderStatus.CANCELLED || currentStatus == OrderStatus.FILLED) {
            throw new ServiceException("Cannot update order in terminal state: " + currentStatus);
        }
        
        // Add more specific transition rules as needed
        if (currentStatus == OrderStatus.PENDING && newStatus == OrderStatus.FILLED) {
            if (!isValidFillTransition(currentStatus)) {
                throw new ServiceException("Invalid status transition from " + currentStatus + " to " + newStatus);
            }
        }
    }

    private boolean isValidFillTransition(OrderStatus currentStatus) {
        // Implement fill transition validation logic
        return currentStatus == OrderStatus.PENDING || currentStatus == OrderStatus.PARTIAL_FILLED;
    }
}
