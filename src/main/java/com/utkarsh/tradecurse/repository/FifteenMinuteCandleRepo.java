package com.utkarsh.tradecurse.repository;

import com.utkarsh.tradecurse.entity.Company;
import com.utkarsh.tradecurse.entity.FifteenMinuteCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FifteenMinuteCandleRepo extends JpaRepository<FifteenMinuteCandle, Long> {
    List<FifteenMinuteCandle> findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(Company company, LocalDateTime timestampIsGreaterThan, LocalDateTime timestampIsLessThan);
}
