package com.tradingplatform.realtimedata.controller;

import com.tradingplatform.realtimedata.dto.PriceUpdate;
import com.tradingplatform.realtimedata.service.PriceStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class WebSocketController {

    private final PriceStreamService priceStreamService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketController(PriceStreamService priceStreamService, SimpMessagingTemplate messagingTemplate) {
        this.priceStreamService = priceStreamService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/subscribe")
    public void subscribeToPrice(@Payload String symbol) {
        Flux<PriceUpdate> priceUpdates = priceStreamService.subscribeToPriceUpdates(symbol);
        
        priceUpdates.subscribe(
            update -> messagingTemplate.convertAndSend("/topic/prices/" + symbol, update),
            error -> messagingTemplate.convertAndSend("/topic/errors/" + symbol, error.getMessage()),
            () -> messagingTemplate.convertAndSend("/topic/complete/" + symbol, "Stream completed")
        );
    }

    @MessageMapping("/unsubscribe")
    public void unsubscribeFromPrice(@Payload String symbol) {
        priceStreamService.unsubscribe(symbol);
        messagingTemplate.convertAndSend("/topic/unsubscribed/" + symbol, "Unsubscribed from " + symbol);
    }
}
