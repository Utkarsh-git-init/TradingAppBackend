package com.utkarsh.tradecurse.controller;

import com.utkarsh.tradecurse.entity.FifteenMinuteCandle;
import com.utkarsh.tradecurse.service.PriceHistoryService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class PriceHistoryController {
    private final PriceHistoryService priceHistoryService;

    public PriceHistoryController(PriceHistoryService priceHistoryService) {
        this.priceHistoryService = priceHistoryService;
    }

    @GetMapping("/company/price_history/{companyId}")
    public List<FifteenMinuteCandle> get24hrHistory(@PathVariable Integer companyId){
        return priceHistoryService.get24hrHistory(companyId);
    }
}
