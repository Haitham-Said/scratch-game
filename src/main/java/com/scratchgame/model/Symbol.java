package com.scratchgame.model;

public class Symbol {
    private Double reward_multiplier;
    private String type;
    private String impact;
    private Integer extra;


    public Double getReward_multiplier() {
        return reward_multiplier;
    }

    public void setReward_multiplier(Double reward_multiplier) {
        this.reward_multiplier = reward_multiplier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    public Integer getExtra() {
        return extra;
    }

    public void setExtra(Integer extra) {
        this.extra = extra;
    }
}
