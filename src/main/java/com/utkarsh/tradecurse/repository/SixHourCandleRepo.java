package com.utkarsh.tradecurse.repository;

import com.utkarsh.tradecurse.model.Company;
import com.utkarsh.tradecurse.model.SixHourCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SixHourCandleRepo extends JpaRepository<SixHourCandle, Long> {
    List<SixHourCandle> findByCompanyAndTimestampGreaterThanEqual(Company company, LocalDateTime timestampIsGreaterThan);

    List<SixHourCandle> findByCompanyAndTimestampGreaterThanEqualOrderByTimestampAsc(Company company, LocalDateTime timestampIsGreaterThan);

    List<SixHourCandle> findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(Company company, LocalDateTime timestampIsGreaterThan, LocalDateTime timestampIsLessThan);
}
