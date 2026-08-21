package com.utkarsh.tradecurse.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MarketDto {
    private MarketOverviewDto market;
    private List<MinimalCompanyDto> companies;
}
