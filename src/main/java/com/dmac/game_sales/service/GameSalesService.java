package com.dmac.game_sales.service;

import com.dmac.game_sales.model.GameSales;
import com.dmac.game_sales.model.TotalSales;
import com.dmac.game_sales.repository.GameSalesRepository;
import com.dmac.game_sales.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GameSalesService {

    private static final Logger logger = LoggerFactory.getLogger(GameSalesService.class);

    @Autowired
    private GameSalesRepository gameSalesRepository;

    public Mono<GameSales> saveGameSale(GameSales gameSales){
        return gameSalesRepository.save(gameSales);
    }

    public Flux<GameSales> saveGameSalesBulk(Flux<FilePart> file){

        Instant start = Instant.now();
        logger.info("Begin reading file at "+start);
        Flux<String> fileLines = file
        .flatMap(filePart -> {
            logger.info("Filename: "+filePart.filename());
            return filePart.content().map(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);
                return new String(bytes, StandardCharsets.UTF_8);
            });
        })
        .map(s -> {
            Supplier<Stream<String>> streamSupplier = s::lines;
            return streamSupplier.get().collect(Collectors.toList());
        })
        .flatMapIterable(Function.identity());

        Flux<GameSales> gameSales =  fileLines.map(line -> {
            String[] cols = line.split(",");
            return GameSales.builder()
                    .gameNo(Integer.parseInt(cols[1]))
                    .gameName(cols[2])
                    .gameCode(cols[3])
                    .type(Integer.parseInt(cols[4]))
                    .costPrice(Double.parseDouble(cols[5]))
                    .tax(Double.parseDouble(cols[6]))
                    .salePrice(Double.parseDouble(cols[7]))
                    .dateOfSale(Instant.parse(cols[8]))
                    .build();
        })
        .onErrorContinue((error, obj) -> System.out.println(error.getMessage()))
        .doOnComplete(()->{
            Instant completed = Instant.now();
            Duration duration = Duration.between(start, completed);
            logger.info("File completed at "+completed);
            logger.info("Time taken to process:"+ DateTimeUtils.toText(duration));
        });

        return gameSalesRepository.saveAll(gameSales);
    }

    public Flux<GameSales> getGameSales(
            LocalDate fromDate, LocalDate toDate,
            String salePriceComparator, Double salesPriceParam, int page){

        Instant start = Instant.now();
        logger.info("Begin get game sales at "+start);

        int limit = 100;
        boolean getDateRange = null != fromDate && null != toDate;
        boolean moreThanPrice = null != salesPriceParam && "moreThan".equalsIgnoreCase(salePriceComparator);
        boolean lessThanPrice = null != salesPriceParam && "lessThan".equalsIgnoreCase(salePriceComparator);

        Pageable sortedBySalesPrice = PageRequest.of(page, limit, Sort.by("sale_price"));
        Pageable sortedByDateOfSale = PageRequest.of(page, limit, Sort.by("date_of_sale"));

        if(getDateRange && (moreThanPrice || lessThanPrice)){
            if(moreThanPrice){
                return gameSalesRepository.findByDateOfSaleBetweenAndSalePriceGreaterThan(
                        fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                        toDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                        salesPriceParam,
                        sortedByDateOfSale
                ).doOnComplete(()->{
                    Instant completed = Instant.now();
                    Duration duration = Duration.between(start, completed);
                    logger.info("Process completed at "+completed);
                    logger.info("Time taken to process:"+ DateTimeUtils.toText(duration));
                });
            }
            if(lessThanPrice){
                return gameSalesRepository.findByDateOfSaleBetweenAndSalePriceLessThan(
                        fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                        toDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                        salesPriceParam,
                        sortedByDateOfSale
                ).doOnComplete(()->{
                    Instant completed = Instant.now();
                    Duration duration = Duration.between(start, completed);
                    logger.info("Process completed at "+completed);
                    logger.info("Time taken to process:"+ DateTimeUtils.toText(duration));
                });
            }
        }else if(getDateRange){
            return gameSalesRepository.findByDateOfSaleBetween(
                    fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                    toDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                    sortedByDateOfSale
            ).doOnComplete(()->{
                Instant completed = Instant.now();
                Duration duration = Duration.between(start, completed);
                logger.info("Process completed at "+completed);
                logger.info("Time taken to process:"+ DateTimeUtils.toText(duration));
            });
        }else if(moreThanPrice){
            return gameSalesRepository.findBySalePriceGreaterThan(salesPriceParam, sortedBySalesPrice)
                    .doOnComplete(()->{
                        Instant completed = Instant.now();
                        Duration duration = Duration.between(start, completed);
                        logger.info("Process completed at "+completed);
                        logger.info("Time taken to process:"+ DateTimeUtils.toText(duration));
                    });
        }else if(lessThanPrice){
            return gameSalesRepository.findBySalePriceLessThan(salesPriceParam, sortedBySalesPrice)
                    .doOnComplete(()->{
                        Instant completed = Instant.now();
                        Duration duration = Duration.between(start, completed);
                        logger.info("Process completed at "+completed);
                        logger.info("Time taken to process:"+ DateTimeUtils.toText(duration));
                    });
        }

        return null;
    }

    public Mono<TotalSales> getGameTotalSales(
            LocalDate fromDate, LocalDate toDate, Integer gameNo){

        Instant start = Instant.now();
        logger.info("Begin get game total sales at "+start);

        if(null != fromDate && null != toDate && null != gameNo){
            return gameSalesRepository.findTotalSalesInDateRangeWithGameNo(
                    fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                    toDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                    gameNo
            ).doOnSuccess(totalSales->{
                Instant completed = Instant.now();
                Duration duration = Duration.between(start, completed);
                logger.info("Process completed at "+completed);
                logger.info("Time taken to process:"+ DateTimeUtils.toText(duration));
            });
        }

        if(null != fromDate && null != toDate){
            return gameSalesRepository.findTotalSalesInDateRange(
                    fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                    toDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            ).doOnSuccess(totalSales->{
                Instant completed = Instant.now();
                Duration duration = Duration.between(start, completed);
                logger.info("Process completed at "+completed);
                logger.info("Time taken to process:"+ DateTimeUtils.toText(duration));
            });
        }

        return null;
    }
}
