package com.utkarsh.tradecurse.service;

import com.utkarsh.tradecurse.dto.CurrentPriceDto;
import com.utkarsh.tradecurse.entity.Company;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PriceService {

    private final CompanyService companyService;
    private final CandleService candleService;
    private final PriceStreamService priceStreamService;
    private final PriceSimulationService priceSimulationService;

    public PriceService(CompanyService companyService, CandleService candleService, PriceStreamService priceStreamService, PriceSimulationService priceSimulationService) {
        this.companyService = companyService;
        this.candleService = candleService;
        this.priceStreamService = priceStreamService;
        this.priceSimulationService = priceSimulationService;
    }
    @Scheduled(fixedRate = 1000)
    public void generatePrices(){
//        log.info("updating prices");
        List<CurrentPriceDto> prices=new ArrayList<>();
        for(Company company:companyService.getCompanies().values()){
            BigDecimal newPrice=priceSimulationService.nextPrice(company);
            company.setCurrentPrice(newPrice);
            prices.add(new CurrentPriceDto(company.getId(),newPrice));
            candleService.updatePrice(company.getId(),newPrice);
        }
        priceStreamService.sendPrices(prices);
    }
}
