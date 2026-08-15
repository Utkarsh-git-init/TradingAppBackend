package com.utkarsh.tradecurse.repository;

import com.utkarsh.tradecurse.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepo extends JpaRepository<Company,Integer> {
}
