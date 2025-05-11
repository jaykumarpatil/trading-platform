package com.tradingplatform.realtimedata.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "quotes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Column(name = "bid_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal bidPrice;

    @Column(name = "ask_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal askPrice;

    @Column(name = "bid_size", nullable = false)
    private Long bidSize;

    @Column(name = "ask_size", nullable = false)
    private Long askSize;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "exchange", nullable = false)
    private String exchange;

    @Version
    private Long version;
}
