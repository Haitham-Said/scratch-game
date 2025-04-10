package com.scratchgame;

import com.scratchgame.model.Configuration;
import com.scratchgame.service.GameService;
import com.scratchgame.utils.ParserUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws FileNotFoundException {


        String configFilePath = "";
        Integer betAmount = 0;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--config":
                    configFilePath = args[i + 1];
                    i++;
                    break;
                case "--betting-amount":
                    try {
                        betAmount = Integer.parseInt(args[i + 1]);
                        i++;
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid betting amount");
                        return;
                    }
                    break;
                default:
                    System.err.println("Unknown argument: " + args[i]);
                    return;
            }
        }

        Configuration configuration;
        try (FileInputStream fileInputStream = new FileInputStream(configFilePath)) {
            configuration = ParserUtil.parse(configFilePath, Configuration.class);
        } catch (Exception e) {
            throw new RuntimeException("Error reading config file: " + e.getMessage(), e);
        }
        GameService gameService = new GameService();
        List<List<String>> matrix = gameService.generateMatrix(configuration.getProbabilities(), configuration.getRows(), configuration.getColumns());
        Map<String, Integer> countSymbolMap = gameService.countSymbol(matrix);
        String bonusSymbol = gameService.insertBonusSymbol(configuration.getProbabilities().getBonus_symbols().getSymbols(), matrix);
        Double outCome = gameService.calculateOutcome(configuration, countSymbolMap, betAmount, bonusSymbol);

        System.out.println(outCome);
    }
}