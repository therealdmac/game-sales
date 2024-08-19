package com.dmac.game_sales.controller;

import com.dmac.game_sales.model.GameSales;
import com.dmac.game_sales.model.TotalSales;
import com.dmac.game_sales.service.GameSalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("api/game_sales")
public class GameSalesController {

    @Autowired
    GameSalesService gameSalesService;

    @PostMapping
    public Mono<GameSales> createGameSales(@RequestBody GameSales gameSales){
        return gameSalesService.saveGameSale(gameSales);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Flux<GameSales> importFile(@RequestPart("files") Flux<FilePart> filePart) {

        return gameSalesService.saveGameSalesBulk(filePart);
    }

    /*
    Design and develop an endpoint called ‘/getGameSales’ that returns the following result:
            1.	A list of game sales
            2.	A list of game sales during a given period (From date and To date).
            3.	A list of game sales where sale_price is less than or more than a given parameter.
    The endpoint should only return 100 records per request and must support pagination.
    The endpoint should return results in less than 50ms.
    */
    @GetMapping(value = "/getGameSales")
    public Flux<GameSales> getGameSales(
            @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) LocalDate toDate,
            @RequestParam(value = "salesPriceParam", required = false) Double salesPriceParam,
            @RequestParam(value = "salePriceComparator", required = false) String salePriceComparator,
            @RequestParam(value = "page", defaultValue = "0") int page){
        return gameSalesService.getGameSales(fromDate, toDate, salePriceComparator, salesPriceParam, page);
    }

    /*
    Design and develop an endpoint called ‘/getTotalSales’ that returns the possible result:
            1.	The total number of games sold during a given period. (eg. daily counts)
            2.	The total sales generated (total sale_price) during a given period. (eg. daily sales)
            3.	The total sales generated (total sale_price) during a given period with a given game_no. (eg. daily sales of a particular game_no)
    */
    @GetMapping(value = "/getTotalGamesSales")
    public Mono<TotalSales> getTotalGamesSales(
            @RequestParam(value = "fromDate", required = false) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) LocalDate toDate,
            @RequestParam(value = "gameNo", required = false) Integer gameNo){
        return gameSalesService.getGameTotalSales(fromDate, toDate, gameNo);
    }
}
