package com.utkarsh.tradecurse.repository;

import com.utkarsh.tradecurse.dto.PriceRangeDto;
import com.utkarsh.tradecurse.entity.Company;
import com.utkarsh.tradecurse.entity.MinuteCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MinuteCandleRepo extends JpaRepository<MinuteCandle,Long> {
    List<MinuteCandle>
    findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(
            Company company, LocalDateTime timestampIsGreaterThan, LocalDateTime timestampIsLessThan);

    @Query("""
                SELECT new com.utkarsh.tradecurse.dto.PriceRangeDto(
                            MIN(c.low), MAX(c.high))
                FROM MinuteCandle c
                WHERE c.company.id = :companyId
                AND c.timestamp >= :from
            """)
    PriceRangeDto findRange(Integer companyId, LocalDateTime from);

    Optional<MinuteCandle>
    findFirstByCompanyIdAndTimestampGreaterThanEqualOrderByTimestampAsc(
            Integer companyId,
            LocalDateTime timestamp
    );
}
