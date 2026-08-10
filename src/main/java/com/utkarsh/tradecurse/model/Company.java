package com.utkarsh.tradecurse.model;

import com.utkarsh.tradecurse.enums.Sector;
import com.utkarsh.tradecurse.enums.Volatility;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private BigDecimal currentPrice;
    private BigDecimal fairPrice;
    private Volatility volatility;
    private double growthRate;
    private double meanReversion;
    private double trend;
}
