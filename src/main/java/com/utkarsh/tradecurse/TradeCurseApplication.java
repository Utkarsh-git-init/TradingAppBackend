package com.utkarsh.tradecurse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TradeCurseApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeCurseApplication.class, args);
    }

}
