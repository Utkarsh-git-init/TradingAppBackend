package com.utkarsh.tradecurse.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrentCandle {
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal open;
    private BigDecimal close;
}
