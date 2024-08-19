package com.dmac.game_sales.repository;

import com.dmac.game_sales.model.GameSales;
import com.dmac.game_sales.model.TotalSales;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;

@Repository
public interface GameSalesRepository extends ReactiveCrudRepository<GameSales, Long> {

    Flux<GameSales> findByDateOfSaleBetween(Instant fromDate, Instant toDate, Pageable pageable);
    Flux<GameSales> findBySalePriceLessThan(double salePrice, Pageable pageable);
    Flux<GameSales> findBySalePriceGreaterThan(double salePrice, Pageable pageable);
    Flux<GameSales> findByDateOfSaleBetweenAndSalePriceLessThan(Instant fromDate, Instant toDate, double salePrice, Pageable pageable);
    Flux<GameSales> findByDateOfSaleBetweenAndSalePriceGreaterThan(Instant fromDate, Instant toDate, double salePrice, Pageable pageable);

    @Query("select count(1) as quantity, sum(g.sale_price) as total_sales from game_sales g where g.date_of_sale between :fromDate and :toDate")
    Mono<TotalSales> findTotalSalesInDateRange(
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate
    );

    @Query("select count(1) as quantity, sum(g.sale_price) as total_sales from game_sales g where g.date_of_sale between :fromDate and :toDate and g.game_no = :gameNo")
    Mono<TotalSales> findTotalSalesInDateRangeWithGameNo(
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate,
            @Param("gameNo") Integer gameNo
    );
}