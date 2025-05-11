package com.tradingplatform.realtimedata.controller;

import com.tradingplatform.realtimedata.dto.MarketDataMessage;
import com.tradingplatform.realtimedata.entity.MarketData;
import com.tradingplatform.realtimedata.service.MarketDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/market-data")
@Tag(name = "Market Data API", description = "APIs for market data operations")
public class MarketDataController {

    private final MarketDataService marketDataService;

    @Autowired
    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    @GetMapping("/{symbol}")
    @Operation(summary = "Get latest market data for a symbol")
    public ResponseEntity<MarketData> getLatestMarketData(@PathVariable String symbol) {
        return marketDataService.getLatestMarketData(symbol)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{symbol}/history")
    @Operation(summary = "Get historical market data for a symbol")
    public ResponseEntity<List<MarketData>> getMarketDataHistory(
            @PathVariable String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime) {
        List<MarketData> history = marketDataService.getMarketDataHistory(symbol, startTime);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/process")
    @Operation(summary = "Process market data update")
    public ResponseEntity<Void> processMarketData(@RequestBody MarketDataMessage message) {
        marketDataService.processMarketDataUpdate(message);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exchange/{exchange}")
    @Operation(summary = "Get market data by exchange")
    public ResponseEntity<List<MarketData>> getMarketDataByExchange(
            @PathVariable String exchange,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime) {
        List<MarketData> data = marketDataService.getMarketDataByExchange(exchange, startTime);
        return ResponseEntity.ok(data);
    }
}
