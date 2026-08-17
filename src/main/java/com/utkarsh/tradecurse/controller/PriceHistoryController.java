package com.utkarsh.tradecurse.controller;

import com.utkarsh.tradecurse.service.PriceHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/company/candles/{companyId}")
public class PriceHistoryController {
    private final PriceHistoryService priceHistoryService;

    public PriceHistoryController(PriceHistoryService priceHistoryService) {
        this.priceHistoryService = priceHistoryService;
    }

    @GetMapping
    public ResponseEntity<?> get24hrHistory(@PathVariable Integer companyId,@RequestParam String range){
        return priceHistoryService.getCandles(companyId,range);
    }

}
