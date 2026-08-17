package com.utkarsh.tradecurse.repository;

import com.utkarsh.tradecurse.entity.Company;
import com.utkarsh.tradecurse.entity.MinuteCandle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MinuteCandleRepo extends JpaRepository<MinuteCandle,Long> {
    List<MinuteCandle> findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(Company company, LocalDateTime timestampIsGreaterThan, LocalDateTime timestampIsLessThan);

}
