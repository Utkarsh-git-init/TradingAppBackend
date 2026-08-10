package com.utkarsh.tradecurse.enums;

import lombok.Getter;

@Getter
public enum Volatility {
    LOW(0.15),
    MID(0.30),
    HIGH(0.60);
    private final double baseSigma;

    Volatility(double baseSigma) {
        this.baseSigma = baseSigma;
    }

}
