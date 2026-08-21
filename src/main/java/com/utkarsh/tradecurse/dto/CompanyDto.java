package com.utkarsh.tradecurse.dto;

import com.utkarsh.tradecurse.enums.Interval;
import com.utkarsh.tradecurse.enums.Sector;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class CompanyDto {
    private Integer id;
    private String name;
    private BigDecimal currentPrice;
    private Sector sector;
    private String symbol;
    private String description;
    private Integer employees;
    private Integer foundedYear;
    private String exchange;
    private String currency;
    private BigDecimal marketCap;

    private CompanyFundamentals fundamentals;

    private Map<Interval, BigDecimal> changes;
    private Map<Interval, PriceRangeDto> ranges;
}