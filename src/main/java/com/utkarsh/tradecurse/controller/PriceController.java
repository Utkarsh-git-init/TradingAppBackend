package com.utkarsh.tradecurse.controller;

import com.utkarsh.tradecurse.service.PriceStreamService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/stream")
@CrossOrigin("*")
public class PriceController {
    private final PriceStreamService priceStreamService;

    public PriceController(PriceStreamService priceStreamService) {
        this.priceStreamService = priceStreamService;
    }

    @GetMapping("/prices")
    public SseEmitter getStockPrices(){
        return priceStreamService.subscribe();
    }
}
