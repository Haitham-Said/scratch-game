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


    public Double calculateOutcome(Configuration config, Map<String, Integer> countSymbolMap, Integer betAmount,String bonusSymbol) {
        Double finalReward = 0.0;
        Map<String,WinCombination> winCombinationMap=new HashMap<>();
        for(Map.Entry<String,Integer> entry:countSymbolMap.entrySet()){
            if(entry.getValue()>=3){
                for (Map.Entry<String, WinCombination> winCombinationEntry:config.getWin_combinations().entrySet()){
                    if(winCombinationEntry.getValue().getCount()!=null&&winCombinationEntry.getValue().getCount().equals(entry.getValue())){
                        winCombinationMap.put(entry.getKey(),winCombinationEntry.getValue());
                    }
                }
            }

        }
        for (Map.Entry<String,WinCombination> winCombinationEntry:winCombinationMap.entrySet()){
            Double reward_multiplier=config.getSymbols().get(winCombinationEntry.getKey()).getReward_multiplier();
            finalReward +=reward_multiplier*winCombinationEntry.getValue().getReward_multiplier();

        }
       finalReward =finalReward* betAmount;
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
}
