package com.utkarsh.tradecurse.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarketOverviewDto {
    private Integer totalCompanies;
    private Integer gainers;
    private Integer losers;
    private Integer unchanged;
    private BigDecimal averageChangePercent;
    private BigDecimal totalMarketCap;
}
