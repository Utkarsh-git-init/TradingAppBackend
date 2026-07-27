package com.utkarsh.tradecurse.repository;

import com.utkarsh.tradecurse.model.Company;
import com.utkarsh.tradecurse.model.FiveMinuteCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FiveMinuteCandleRepo extends JpaRepository<FiveMinuteCandle,Long> {
    List<FiveMinuteCandle> findByCompanyAndTimestampGreaterThanEqual(Company company, LocalDateTime timestampIsGreaterThan);

    List<FiveMinuteCandle> findByCompanyAndTimestampGreaterThanEqualOrderByTimestampAsc(Company company, LocalDateTime timestampIsGreaterThan);

    List<FiveMinuteCandle> findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(Company company, LocalDateTime timestampIsGreaterThan, LocalDateTime timestampIsLessThan);
}
