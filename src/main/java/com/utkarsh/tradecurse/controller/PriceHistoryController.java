package com.utkarsh.tradecurse.controller;

import com.utkarsh.tradecurse.service.PriceHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class PriceHistoryController {
    private final PriceHistoryService priceHistoryService;

    public PriceHistoryController(PriceHistoryService priceHistoryService) {
        this.priceHistoryService = priceHistoryService;
    }

    @GetMapping("/company/candles/{companyId}")
    public ResponseEntity<?> get24hrHistory(@PathVariable Integer companyId,@RequestParam String range){
        return priceHistoryService.getCandles(companyId,range);
    }

}
