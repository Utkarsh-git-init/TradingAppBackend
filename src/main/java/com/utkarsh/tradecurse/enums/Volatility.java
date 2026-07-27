package com.utkarsh.tradecurse.enums;

import lombok.Getter;

@Getter
public enum Volatility {
    LOW(1),
    MID(2),
    HIGH(4);

    private final double maxChange;
    Volatility(double maxChange){
        this.maxChange=maxChange;
    }

}
