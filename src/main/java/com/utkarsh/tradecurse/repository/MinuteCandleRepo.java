package com.utkarsh.tradecurse.repository;

import com.utkarsh.tradecurse.model.Company;
import com.utkarsh.tradecurse.model.MinuteCandle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MinuteCandleRepo extends JpaRepository<MinuteCandle,Long> {
    List<MinuteCandle> getAllByCompany(Company company);

    List<MinuteCandle> findByCompanyAndTimestampGreaterThanEqual(Company company, LocalDateTime timestampIsGreaterThan);

    List<MinuteCandle> findByCompanyAndTimestampGreaterThanEqualOrderByTimestampAsc(Company company, LocalDateTime timestampIsGreaterThan);

    List<MinuteCandle> findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(Company company, LocalDateTime timestampIsGreaterThan, LocalDateTime timestampIsLessThan);

    List<MinuteCandle> getByCompanyAndTimestampAfterOrderByTimestampAsc(Company company, LocalDateTime timestampAfter);
}
