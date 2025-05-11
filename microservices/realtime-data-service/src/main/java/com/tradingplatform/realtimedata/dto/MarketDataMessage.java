package com.tradingplatform.realtimedata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketDataMessage {
    private String symbol;
    private BigDecimal price;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal volume;
    private String exchange;
    private Instant timestamp;
    private String messageType; // TRADE, QUOTE, etc.
}
