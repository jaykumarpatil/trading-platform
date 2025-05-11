package com.tradingplatform.realtimedata.repository;

import com.tradingplatform.realtimedata.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    
    Optional<Quote> findFirstBySymbolOrderByTimestampDesc(String symbol);
    
    @Query("SELECT q FROM Quote q WHERE q.symbol = :symbol AND q.timestamp >= :startTime ORDER BY q.timestamp DESC")
    List<Quote> findBySymbolAndTimestampAfterOrderByTimestampDesc(
        @Param("symbol") String symbol,
        @Param("startTime") Instant startTime
    );
    
    @Query("SELECT q FROM Quote q WHERE q.exchange = :exchange AND q.timestamp >= :startTime")
    List<Quote> findByExchangeAndTimestampAfter(
        @Param("exchange") String exchange,
        @Param("startTime") Instant startTime
    );
    
    void deleteByTimestampBefore(Instant timestamp);
}
