package com.utkarsh.tradecurse.repository;

import com.utkarsh.tradecurse.entity.Company;
import com.utkarsh.tradecurse.entity.HourCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HourCandleRepo extends JpaRepository<HourCandle, Long> {

    List<HourCandle> findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(Company company, LocalDateTime timestampIsGreaterThan, LocalDateTime timestampIsLessThan);
}
