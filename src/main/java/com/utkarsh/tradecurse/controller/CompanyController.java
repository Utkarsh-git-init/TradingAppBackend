package com.utkarsh.tradecurse.controller;

import com.utkarsh.tradecurse.entity.Company;
import com.utkarsh.tradecurse.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/admin/company")
    public Company addCompany(@RequestBody Company company){
        return companyService.addCompany(company);
    }

    @GetMapping("/company")
    public List<Company> getAllCompanies(){
        return companyService.getCompanies().values().stream().toList();
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getCompanyById(@PathVariable Integer companyId){
        return ResponseEntity.ok().body(
                companyService.getCompanies().get(companyId)
        );
    }
}
