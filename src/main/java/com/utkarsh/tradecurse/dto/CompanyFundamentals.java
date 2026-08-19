package com.utkarsh.tradecurse.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CompanyFundamentals {
    private BigDecimal peRatio;
    private BigDecimal eps;
    private BigDecimal dividendYield;
    private Long sharesOutstanding;
}
