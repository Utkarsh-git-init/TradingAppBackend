package com.utkarsh.tradecurse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CurrentPriceDto {
    private Integer companyId;
    private BigDecimal currentPrice;
}
