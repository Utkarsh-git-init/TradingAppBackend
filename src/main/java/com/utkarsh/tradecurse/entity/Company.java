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
    @Column(precision = 38, scale = 2)
    private BigDecimal currentPrice;
    @Column(precision = 38, scale = 2)
    private BigDecimal fairPrice;
    private Volatility volatility;
    private double growthRate;
    private double meanReversion;
    private double trend;
}
