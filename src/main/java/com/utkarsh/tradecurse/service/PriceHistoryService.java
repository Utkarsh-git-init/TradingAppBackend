package com.utkarsh.tradecurse.service;

import com.utkarsh.tradecurse.model.Company;
import com.utkarsh.tradecurse.model.FifteenMinuteCandle;
import com.utkarsh.tradecurse.model.MinuteCandle;
import com.utkarsh.tradecurse.repository.CompanyRepo;
import com.utkarsh.tradecurse.repository.FifteenMinuteCandleRepo;
import com.utkarsh.tradecurse.repository.MinuteCandleRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PriceHistoryService {
    private final CompanyRepo companyRepo;
    private final FifteenMinuteCandleRepo fifteenMinuteCandleRepo;

    public PriceHistoryService(CompanyRepo companyRepo, FifteenMinuteCandleRepo fifteenMinuteCandleRepo) {
        this.companyRepo = companyRepo;
        this.fifteenMinuteCandleRepo = fifteenMinuteCandleRepo;
    }

    public List<FifteenMinuteCandle> get24hrHistory(Integer companyId) {
        Company company=companyRepo.getReferenceById(companyId);
        return fifteenMinuteCandleRepo
                .getByCompanyAndTimestampAfterOrderByTimestampAsc(company, LocalDateTime.now().minusDays(1));
    }
}
