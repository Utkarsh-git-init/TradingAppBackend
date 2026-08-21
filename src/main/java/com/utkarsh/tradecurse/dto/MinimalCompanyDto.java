package com.utkarsh.tradecurse.dto;

import com.utkarsh.tradecurse.enums.Sector;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MinimalCompanyDto {
    private Integer id;
    private String name;
    private String symbol;
    private Sector sector;
    private BigDecimal currentPrice;
    private String exchange;
    private String currency;
    private BigDecimal twentyFourHourChange;
    private BigDecimal twentyFourHourChangePercent;
}
