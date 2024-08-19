package com.dmac.game_sales.utils;

import com.dmac.game_sales.model.Game;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameSalesFileGenerator {

    private final List<String> gameNamePrefixes;
    private final List<String> gameNamePostfixes;
    private final List<Game> gameList;

    GameSalesFileGenerator(){
        gameNamePrefixes = new ArrayList<>();
        gameNamePostfixes = new ArrayList<>();
        gameList = new ArrayList<>();
    }

    private void init(){

        String gameNamePostfixesfilePath = "/Users/darylmcintyre/IdeaProjects/game-sales/src/main/resources/game_name_postfixes.txt";
        String gameNamePrefixesfilePath = "/Users/darylmcintyre/IdeaProjects/game-sales/src/main/resources/game_name_prefixes.txt";

        try (BufferedReader prefixesReader = new BufferedReader(new FileReader(gameNamePrefixesfilePath));
             BufferedReader postFixesReader = new BufferedReader(new FileReader(gameNamePostfixesfilePath))) {

            String line;

            while ((line = prefixesReader.readLine()) != null) {
                gameNamePrefixes.add(line);
            }

            while ((line = postFixesReader.readLine()) != null) {
                gameNamePostfixes.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        generateGames(100);
    }

    private void generateGames(int noOfGames){

        Random random = new Random();

        for(int i=1; i<=noOfGames; i++){

            String gameNamePrefix = gameNamePrefixes.get(random.nextInt(gameNamePostfixes.size()));
            String gameNamePostfix = gameNamePostfixes.get(random.nextInt(gameNamePostfixes.size()));

            String game_name = gameNamePrefix + " " + gameNamePostfix;
            String game_code = "" + gameNamePrefix.charAt(0) + gameNamePostfix.charAt(0) + i;

            gameList.add(new Game(i, game_name, game_code));
        }
    }

    private String generateHeader(){
        StringBuilder sb = new StringBuilder();
        sb.append("id"); sb.append(',');
        sb.append("game_no"); sb.append(',');
        sb.append("game_name"); sb.append(',');
        sb.append("game_code"); sb.append(',');
        sb.append("type"); sb.append(',');
        sb.append("cost_price"); sb.append(',');
        sb.append("tax"); sb.append(',');
        sb.append("sale_price"); sb.append(',');
        sb.append("date_of_sale");
        return sb.toString();
    }

    private String generateLine(int id){

        Random random = new Random(id);

        /*String gameNamePrefix = gameNamePrefixes.get(random.nextInt(gameNamePostfixes.size()));
        String gameNamePostfix = gameNamePostfixes.get(random.nextInt(gameNamePostfixes.size()));

        int game_no = random.nextInt(1,101);
        String game_name = gameNamePrefix + " " + gameNamePostfix;
        String game_code = "" + gameNamePrefix.charAt(0) + gameNamePostfix.charAt(0) + game_no;*/
        Game game = gameList.get(random.nextInt(100));
        int type = random.nextInt(1,3);
        double cost_price = (double) random.nextInt(1, 10000) /100;
        double tax = cost_price * 0.09;
        double sale_price = cost_price + tax;
        String date_of_sale = String.format("2024-04-%02dT%02d:%02d:%02d.%02dZ",
                random.nextInt(1,30),
                random.nextInt(24),
                random.nextInt(60),
                random.nextInt(60),
                random.nextInt(1000));

        StringBuilder sb = new StringBuilder();
        sb.append(id); sb.append(',');
        sb.append(game.gameNo()); sb.append(',');
        sb.append(game.gameName()); sb.append(',');
        sb.append(game.gameCode()); sb.append(',');
        sb.append(type); sb.append(',');
        sb.append(String.format("%.2f", cost_price)); sb.append(',');
        sb.append(String.format("%.2f", tax)); sb.append(',');
        sb.append(String.format("%.2f", sale_price)); sb.append(',');
        sb.append(date_of_sale);

        return sb.toString();
    }

    public void generateFile(int noOfLines){

        String PATTERN_FORMAT = "yyyyMMddhhmmssSSS";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
                .withZone(ZoneId.systemDefault());
        String formattedInstant = formatter.format(Instant.now());

        String gameSalesFilePath = "/Users/darylmcintyre/IdeaProjects/game-sales/src/main/resources/";
        String gameSalesFileName = "game_sales-"+noOfLines+"Records-"+formattedInstant+".csv";

        try (BufferedWriter gameSalesFileReader = new BufferedWriter(new FileWriter(gameSalesFilePath+gameSalesFileName))) {

            gameSalesFileReader.write(generateHeader());
            gameSalesFileReader.newLine();

            for(int i=0; i<noOfLines; i++){
                gameSalesFileReader.write(generateLine(i+1));
                gameSalesFileReader.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){

        GameSalesFileGenerator gameSalesFileGenerator = new GameSalesFileGenerator();
        gameSalesFileGenerator.init();
        gameSalesFileGenerator.generateFile(1000000);
    }
}
