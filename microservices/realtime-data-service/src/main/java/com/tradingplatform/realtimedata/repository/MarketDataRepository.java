package com.tradingplatform.realtimedata.repository;

import com.tradingplatform.realtimedata.entity.MarketData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MarketDataRepository extends JpaRepository<MarketData, Long> {
    
    Optional<MarketData> findBySymbol(String symbol);
    
    @Query("SELECT m FROM MarketData m WHERE m.symbol = :symbol AND m.timestamp >= :startTime")
    List<MarketData> findBySymbolAndTimestampAfter(
        @Param("symbol") String symbol,
        @Param("startTime") Instant startTime
    );
    
    @Query("SELECT m FROM MarketData m WHERE m.exchange = :exchange AND m.timestamp >= :startTime")
    List<MarketData> findByExchangeAndTimestampAfter(
        @Param("exchange") String exchange,
        @Param("startTime") Instant startTime
    );
    
    void deleteByTimestampBefore(Instant timestamp);
}
