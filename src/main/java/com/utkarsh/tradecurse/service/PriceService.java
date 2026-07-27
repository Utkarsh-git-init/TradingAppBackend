package com.utkarsh.tradecurse.service;

import com.utkarsh.tradecurse.dto.CurrentPriceDto;
import com.utkarsh.tradecurse.model.Company;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class PriceService {

    private final CompanyService companyService;
    private final CandleService candleService;
    private final PriceStreamService priceStreamService;

    public PriceService(CompanyService companyService, CandleService candleService, PriceStreamService priceStreamService) {
        this.companyService = companyService;
        this.candleService = candleService;
        this.priceStreamService = priceStreamService;
    }
    @Scheduled(fixedRate = 1000)
    public void generatePrices(){
//        log.info("updating prices");
        List<CurrentPriceDto> prices=new ArrayList<>();
        for(Company company:companyService.getCompanies().values()){
            double max=company.getVolatility().getMaxChange();
            double percentageChange=ThreadLocalRandom.current().nextDouble(-max,max);
            BigDecimal multiplier=BigDecimal.valueOf(1+percentageChange/100);
            BigDecimal newPrice=company.getCurrentPrice()
                    .multiply(multiplier)
                    .setScale(2, RoundingMode.HALF_UP);
            if (newPrice.compareTo(BigDecimal.ONE) < 0) {
                newPrice = BigDecimal.ONE;
            }
            company.setCurrentPrice(newPrice);
            prices.add(new CurrentPriceDto(company.getId(),newPrice));
            candleService.updatePrice(company.getId(),newPrice);
        }
        priceStreamService.sendPrices(prices);
    }
}
