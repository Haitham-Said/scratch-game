package com.scratchgame.model;

import java.util.List;

public class Probabilities {
private List<StandardSymbol> standard_symbols;
private BonusSymbol bonus_symbols;

    public List<StandardSymbol> getStandard_symbols() {
        return standard_symbols;
    }

    public void setStandard_symbols(List<StandardSymbol> standard_symbols) {
        this.standard_symbols = standard_symbols;
    }

    public BonusSymbol getBonus_symbols() {
        return bonus_symbols;
    }

    public void setBonus_symbols(BonusSymbol bonus_symbols) {
        this.bonus_symbols = bonus_symbols;
    }
}
