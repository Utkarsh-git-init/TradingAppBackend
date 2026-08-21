package com.utkarsh.tradecurse.controller;

import com.utkarsh.tradecurse.dto.MarketDto;
import com.utkarsh.tradecurse.service.MarketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/market")
public class MarketController {

    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping
    public ResponseEntity<?> market(){
        return ResponseEntity.ok()
                .body(marketService.getMarketDto());
    }
}
