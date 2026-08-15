package com.utkarsh.tradecurse.repository;

import com.utkarsh.tradecurse.entity.Company;
import com.utkarsh.tradecurse.entity.FifteenMinuteCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FifteenMinuteCandleRepo extends JpaRepository<FifteenMinuteCandle, Long> {
    List<FifteenMinuteCandle> findByCompanyAndTimestampGreaterThanEqual(Company company, LocalDateTime timestampIsGreaterThan);

    List<FifteenMinuteCandle> findByCompanyAndTimestampGreaterThanEqualOrderByTimestampAsc(Company company, LocalDateTime timestampIsGreaterThan);

    List<FifteenMinuteCandle> findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(Company company, LocalDateTime timestampIsGreaterThan, LocalDateTime timestampIsLessThan);

    List<FifteenMinuteCandle> getByCompanyAndTimestampAfterOrderByTimestampAsc(Company company, LocalDateTime timestampAfter);

    List<FifteenMinuteCandle> getByCompanyOrderByTimestampAsc(Company company);
}
