package com.dmac.game_sales.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("game_sales") @Data @Builder
public class GameSales {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private long id;

    @Column(name = "game_no")
    private long gameNo;

    @Column(name = "game_name")
    private String gameName;

    @Column(name = "game_code")
    private String gameCode;

    @Column(name = "type")
    private int type;

    @Column(name = "cost_price")
    private double costPrice;

    @Column(name = "tax")
    private double tax;

    @Column(name = "sale_price")
    private double salePrice;

    @Column(name = "date_of_sale")
    private Instant dateOfSale;
}


/*
1.	id (a running number starts with 1)
2.	game_no (an integer value between 1 to 100)
3.	game_name (a string value not more than 20 characters)
4.	game_code (a string value not more than 5 characters)
5.	type (an integer, 1 = Online | 2 = Offline)
6.	cost_price (decimal value not more than 100)
7.	tax (9%)
8.	sale_price (decimal value, cost_price inclusive of tax)
9.	date_of_sale (a timestamp of the sale)
 */