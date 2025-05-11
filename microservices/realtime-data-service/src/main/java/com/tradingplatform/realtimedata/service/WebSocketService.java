package com.tradingplatform.realtimedata.service;

import com.tradingplatform.common.exception.ServiceException;
import com.tradingplatform.realtimedata.dto.MarketDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class WebSocketService {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    private static final String TOPIC_PREFIX = "/topic/market-data/";

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, Set<String>> symbolSubscriptions;
    private final Map<String, String> sessionSymbols;

    @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.symbolSubscriptions = new ConcurrentHashMap<>();
        this.sessionSymbols = new ConcurrentHashMap<>();
    }

    public void subscribe(String sessionId, String symbol) {
        logger.debug("Session {} subscribing to symbol {}", sessionId, symbol);
        
        symbolSubscriptions.computeIfAbsent(symbol, k -> new CopyOnWriteArraySet<>())
                .add(sessionId);
        sessionSymbols.put(sessionId, symbol);
        
        logger.info("Session {} subscribed to {}", sessionId, symbol);
    }

    public void unsubscribe(String sessionId) {
        logger.debug("Unsubscribing session: {}", sessionId);
        
        String symbol = sessionSymbols.remove(sessionId);
        if (symbol != null) {
            Set<String> subscribers = symbolSubscriptions.get(symbol);
            if (subscribers != null) {
                subscribers.remove(sessionId);
                if (subscribers.isEmpty()) {
                    symbolSubscriptions.remove(symbol);
                }
            }
        }
        
        logger.info("Session {} unsubscribed from {}", sessionId, symbol);
    }

    public void broadcast(String symbol, MarketDataMessage message) {
        logger.debug("Broadcasting market data for symbol: {}", symbol);
        
        Set<String> subscribers = symbolSubscriptions.get(symbol);
        if (subscribers != null && !subscribers.isEmpty()) {
            try {
                String destination = TOPIC_PREFIX + symbol;
                messagingTemplate.convertAndSend(destination, message);
                logger.debug("Broadcast sent to {} subscribers for symbol {}", subscribers.size(), symbol);
            } catch (Exception e) {
                logger.error("Failed to broadcast market data", e);
                throw new ServiceException("Failed to broadcast market data", e);
            }
        }
    }

    public void broadcastToSession(String sessionId, MarketDataMessage message) {
        logger.debug("Sending market data to session: {}", sessionId);
        
        try {
            String destination = TOPIC_PREFIX + "private/" + sessionId;
            messagingTemplate.convertAndSendToUser(sessionId, destination, message);
            logger.debug("Message sent to session {}", sessionId);
        } catch (Exception e) {
            logger.error("Failed to send message to session", e);
            throw new ServiceException("Failed to send message to session", e);
        }
    }

    public Set<String> getSubscribedSessions(String symbol) {
        return symbolSubscriptions.getOrDefault(symbol, new CopyOnWriteArraySet<>());
    }

    public boolean hasSubscribers(String symbol) {
        Set<String> subscribers = symbolSubscriptions.get(symbol);
        return subscribers != null && !subscribers.isEmpty();
    }

    public void handleSessionDisconnect(String sessionId) {
        logger.info("Handling disconnect for session: {}", sessionId);
        unsubscribe(sessionId);
    }

    public int getSubscriberCount(String symbol) {
        Set<String> subscribers = symbolSubscriptions.get(symbol);
        return subscribers != null ? subscribers.size() : 0;
    }
}
