package com.utkarsh.tradecurse.service;

import com.utkarsh.tradecurse.model.Company;
import com.utkarsh.tradecurse.model.MinuteCandle;
import com.utkarsh.tradecurse.repository.CompanyRepo;
import com.utkarsh.tradecurse.repository.MinuteCandleRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceHistoryService {
    private final MinuteCandleRepo minuteCandleRepo;
    private final CompanyRepo companyRepo;

    public PriceHistoryService(MinuteCandleRepo minuteCandleRepo, CompanyRepo companyRepo) {
        this.minuteCandleRepo = minuteCandleRepo;
        this.companyRepo = companyRepo;
    }

    public List<MinuteCandle> get24hrHistory(Integer companyId) {
        Company company=companyRepo.getReferenceById(companyId);
        return minuteCandleRepo.getAllByCompany(company);
    }
}
