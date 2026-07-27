package com.utkarsh.tradecurse.service;

import com.utkarsh.tradecurse.model.Company;
import com.utkarsh.tradecurse.repository.CompanyRepo;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CompanyService {
    private final CompanyRepo companyRepo;
    @Getter
    private Map<Integer, Company> companies=new ConcurrentHashMap<>();

    public CompanyService(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    @PostConstruct
    public void loadCompanies(){
        companyRepo.findAll()
                .forEach(company -> {
                    companies.put(company.getId(),company);
                });
    }
    public Company addCompany(Company company) {
        return companyRepo.save(company);
    }
}
