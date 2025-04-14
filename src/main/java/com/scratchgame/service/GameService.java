package com.scratchgame.service;

import com.scratchgame.model.*;

import java.util.*;

public class GameService {

    public List<List<String>> generateMatrix(Probabilities probabilities, Integer rows, Integer columns) {

        List<List<String>> matrix = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            List<String> row=new ArrayList<>();
            for (int j = 0; j < columns; j++) {
                String symbol = selectRandomSymbol(probabilities.getStandard_symbols(), i, j);
                row.add(symbol);
            }
            matrix.add(row);

        }

        return matrix;
    }

    public String insertBonusSymbol(Map<String, Integer> symbols, List<List<String>> matrix) {
       int totalPrb= symbols.values().stream().mapToInt(Integer::intValue).sum();
       String bonusSymbol = null;
       int randomVal=new Random().nextInt(totalPrb);
       int currentPrb=0;
       for (Map.Entry<String,Integer> entry:symbols.entrySet()){
           currentPrb+=entry.getValue();
           if(currentPrb>=randomVal){
               bonusSymbol=entry.getKey();
               break;
           }
       }
       int row=new Random().nextInt(matrix.size());
       int column=new Random().nextInt(matrix.get(0).size());
       matrix.get(row).set(column,bonusSymbol);
        return bonusSymbol;
    }

    public Map<String,Integer> countSymbol(List<List<String>> matrix){
        Map<String,Integer> map=new HashMap<>();
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                if(map.containsKey(matrix.get(i).get(j))){
                    map.put(matrix.get(i).get(j),map.get(matrix.get(i).get(j))+1);
                }else{
                    map.put(matrix.get(i).get(j),1);
                }
            }

        }
        return map;
    }


    private static String selectRandomSymbol(List<StandardSymbol> standardSymbols, Integer row, Integer column) {

        Map<String, Integer> symbolProbabilities = standardSymbols.stream()
                .filter(standardSymbol -> standardSymbol.getRow().equals(row) && standardSymbol.getColumn().equals(column))
                .findFirst()
                .orElseGet(() -> standardSymbols.stream()
                        .filter(s -> s.getRow() == 0 && s.getColumn() == 0)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("No default standard symbol found")))
                .getSymbols();

        int totalPrb = symbolProbabilities.values().stream().mapToInt(Integer::intValue).sum();
        int randomValue = new Random().nextInt(totalPrb);
        int prbValue = 0;

        for (Map.Entry<String, Integer> entry : symbolProbabilities.entrySet()) {
            prbValue += entry.getValue();
            if (prbValue > randomValue) {
                return entry.getKey();
            }
        }

        return null;
    }


    public Double calculateOutcome(Configuration config, List<List<String>> matrix, Integer betAmount, String bonusSymbol, Map<String, Integer> countSymbolMap) {
        Double finalReward = 0.0;

        Map<String, Set<String>> winningSymbols = getWinningSymbolsFromPatterns(matrix);
        Map<String, WinCombination> winCombinationMap = new HashMap<>();

        finalReward = calculatePatternReward(config, betAmount, winningSymbols, finalReward);


        finalReward = calculateCountReward(config, betAmount, countSymbolMap, winCombinationMap, finalReward);

        finalReward = applyBonus(config, bonusSymbol, finalReward);

        return finalReward;
    }

    private static Double applyBonus(Configuration config, String bonusSymbol, Double finalReward) {
        if (bonusSymbol != null && config.getSymbols().containsKey(bonusSymbol)) {
            Symbol bonus = config.getSymbols().get(bonusSymbol);
            switch (bonus.getImpact()) {
                case "multiply_reward":
                    finalReward *= bonus.getReward_multiplier();
                    break;
                case "extra_bonus":
                    finalReward += bonus.getExtra();
                    break;
                case "miss":
                    break;
            }
        }
        return finalReward;
    }

    private static Double calculateCountReward(Configuration config, Integer betAmount, Map<String, Integer> countSymbolMap, Map<String, WinCombination> winCombinationMap, Double finalReward) {
        for(Map.Entry<String,Integer> entry: countSymbolMap.entrySet()){
            if(entry.getValue()>=3){
                for (Map.Entry<String, WinCombination> winCombinationEntry: config.getWin_combinations().entrySet()){
                    if(winCombinationEntry.getValue().getCount()!=null&&winCombinationEntry.getValue().getCount().equals(entry.getValue())){
                        winCombinationMap.put(entry.getKey(),winCombinationEntry.getValue());
                    }
                }
            }

        }
        for (Map.Entry<String,WinCombination> winCombinationEntry: winCombinationMap.entrySet()){
            Double reward_multiplier= config.getSymbols().get(winCombinationEntry.getKey()).getReward_multiplier();
            finalReward += betAmount *reward_multiplier*winCombinationEntry.getValue().getReward_multiplier();

        }
        return finalReward;
    }

    private static Double calculatePatternReward(Configuration config, Integer betAmount, Map<String, Set<String>> winningSymbols, Double finalReward) {
        for (Map.Entry<String, Set<String>> entry : winningSymbols.entrySet()) {
            for (String winningPattern : entry.getValue()){
                for (Map.Entry<String, WinCombination> winEntry : config.getWin_combinations().entrySet()) {
                    if (winEntry.getValue().getGroup().equals(winningPattern)) {
                        WinCombination winCombo = winEntry.getValue();
                            finalReward += betAmount * config.getSymbols().get(entry.getKey()).getReward_multiplier()*winCombo.getReward_multiplier();
                    }
                }
        }
        }
        return finalReward;
    }

    public Map<String, Set<String>> getWinningSymbolsFromPatterns(List<List<String>> matrix) {
        Map<String, Set<String>> winningSymbols = new HashMap<>();
        int rows = matrix.size();
        int cols = matrix.get(0).size();


        checkHorizontalPattern(matrix, rows, cols, winningSymbols);


//        checkVerticalPattern(matrix, rows, cols, winningSymbols);



        return winningSymbols;
    }

    private void checkHorizontalPattern(List<List<String>> matrix, int rows, int cols, Map<String, Set<String>> winningSymbols) {
        int matchCount=matrix.get(0).size();
        for (int i = 0; i < rows; i++) {
            int count=1;
            for (int j = 1; j < cols; j++) {
                if(matrix.get(i).get(j).equals(matrix.get(i).get(j-1))){
                    if (count>=matchCount){
                        winningSymbols.computeIfAbsent(matrix.get(i).get(j),k->new HashSet<>())
                                .add("horizontally_linear_symbols");
                    }
                }else {
                    count=1;
                }
            }
        }
    }

    private void checkVerticalPattern(List<List<String>> matrix, int rows, int cols, Map<String, Set<String>> winningSymbols){

    }

}




