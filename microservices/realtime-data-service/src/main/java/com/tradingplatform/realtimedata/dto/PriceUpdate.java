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
public class PriceUpdate {
    private String symbol;
    private BigDecimal price;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;
    private Long bidSize;
    private Long askSize;
    private BigDecimal volume;
    private String exchange;
    private Instant timestamp;
    private String updateType; // TRADE, QUOTE, BBO_UPDATE
    private String source;     // Exchange identifier or data source
}
