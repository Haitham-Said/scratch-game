package com.scratchgame.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;


public class Configuration {
    private Integer rows;
    private Integer columns;
    private Map<String,Symbol> symbols;
    private Probabilities probabilities;
    private Map<String,WinCombination> win_combinations;

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(Map<String, Symbol> symbols) {
        this.symbols = symbols;
    }

    public Probabilities getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(Probabilities probabilities) {
        this.probabilities = probabilities;
    }

    public Map<String, WinCombination> getWin_combinations() {
        return win_combinations;
    }

    public void setWin_combinations(Map<String, WinCombination> win_combinations) {
        this.win_combinations = win_combinations;
    }
}
