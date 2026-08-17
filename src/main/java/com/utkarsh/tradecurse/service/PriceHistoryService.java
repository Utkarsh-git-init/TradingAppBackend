package com.utkarsh.tradecurse.service;

import com.utkarsh.tradecurse.entity.Company;
import com.utkarsh.tradecurse.entity.FifteenMinuteCandle;
import com.utkarsh.tradecurse.entity.MinuteCandle;
import com.utkarsh.tradecurse.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class PriceHistoryService {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    private final CompanyRepo companyRepo;
    private final FifteenMinuteCandleRepo fifteenMinuteCandleRepo;
    private final MinuteCandleRepo minuteCandleRepo;
    private final FiveMinuteCandleRepo fiveMinuteCandleRepo;
    private final HourCandleRepo hourCandleRepo;
    private final SixHourCandleRepo sixHourCandleRepo;

    public PriceHistoryService(CompanyRepo companyRepo, FifteenMinuteCandleRepo fifteenMinuteCandleRepo, MinuteCandleRepo minuteCandleRepo, FiveMinuteCandleRepo fiveMinuteCandleRepo, HourCandleRepo hourCandleRepo, SixHourCandleRepo sixHourCandleRepo) {
        this.companyRepo = companyRepo;
        this.fifteenMinuteCandleRepo = fifteenMinuteCandleRepo;
        this.minuteCandleRepo = minuteCandleRepo;
        this.fiveMinuteCandleRepo = fiveMinuteCandleRepo;
        this.hourCandleRepo = hourCandleRepo;
        this.sixHourCandleRepo = sixHourCandleRepo;
    }

    public ResponseEntity<?> getCandles(Integer companyId, String range) {
        Company company=companyRepo.getReferenceById(companyId);
        return switch (range) {
            case "1h"  -> get1hHistory(company);
            case "6h"  -> get6hHistory(company);
            case "24h" -> get24hHistory(company);
            case "1w"  -> get1wHistory(company);
            case "1m"  -> get1mHistory(company);
            case "1y"  -> get1YHistory(company);
            default -> throw new IllegalArgumentException("Invalid range");
        };
    }

    private ResponseEntity<?> get1hHistory(Company company) {
        return ResponseEntity.ok().body(
                minuteCandleRepo.findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(
                        company,
                        LocalDateTime.now(IST).minusHours(1),
                        LocalDateTime.now(IST)
                )
        );
    }

    private ResponseEntity<?> get6hHistory(Company company) {
        return ResponseEntity.ok().body(
                fiveMinuteCandleRepo.
                        findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(
                                company,
                                LocalDateTime.now(IST).minusHours(6),
                                LocalDateTime.now(IST)
                        )
        );
    }

    private ResponseEntity<?> get24hHistory(Company company) {
        return ResponseEntity.ok().body(
                fifteenMinuteCandleRepo
                        .findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(
                                company,
                                LocalDateTime.now(IST).minusDays(1),
                                LocalDateTime.now(IST)
                        )
        );
    }

    private ResponseEntity<?> get1wHistory(Company company) {
        return ResponseEntity.ok().body(
                hourCandleRepo.
                        findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(
                                company,
                                LocalDateTime.now(IST).minusWeeks(1),
                                LocalDateTime.now(IST)
                        )
        );
    }

    private ResponseEntity<?> get1mHistory(Company company) {
        return ResponseEntity.ok().body(
                sixHourCandleRepo.
                        findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(
                                company,
                                LocalDateTime.now(IST).minusMonths(1),
                                LocalDateTime.now(IST)
                        )
        );
    }

    private ResponseEntity<?> get1YHistory(Company company) {
        return ResponseEntity.ok().body("working on it");
    }

}
