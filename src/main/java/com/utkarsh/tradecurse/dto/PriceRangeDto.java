package com.utkarsh.tradecurse.dto;

import lombok.Data;

import java.math.BigDecimal;

public record PriceRangeDto(
        BigDecimal low,
        BigDecimal high
) {}