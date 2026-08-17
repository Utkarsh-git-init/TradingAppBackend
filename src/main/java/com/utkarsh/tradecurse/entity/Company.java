package com.utkarsh.tradecurse.entity;

import com.utkarsh.tradecurse.enums.Sector;
import com.utkarsh.tradecurse.enums.Volatility;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Sector sector;
    private String symbol;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Integer employees;
    private Integer foundedYear;
    private String exchange;
    private String currency;

    // Fundamental information
    private BigDecimal peRatio;
    private BigDecimal eps;
    private BigDecimal dividendYield;
    private Long sharesOutstanding;

    @Column(precision = 38, scale = 2)
    private BigDecimal currentPrice;
    @Column(precision = 38, scale = 2)
    private BigDecimal fairPrice;

    //simulation parameters
    private Volatility volatility;
    private double growthRate;
    private double meanReversion;
    private double trend;

}
