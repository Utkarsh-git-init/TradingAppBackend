package com.utkarsh.tradecurse.repository;

import com.utkarsh.tradecurse.dto.PriceRangeDto;
import com.utkarsh.tradecurse.entity.Company;
import com.utkarsh.tradecurse.entity.FiveMinuteCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FiveMinuteCandleRepo extends JpaRepository<FiveMinuteCandle,Long> {
    List<FiveMinuteCandle>
    findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(
            Company company, LocalDateTime timestampIsGreaterThan, LocalDateTime timestampIsLessThan);

    @Query("""
            SELECT new com.utkarsh.tradecurse.dto.PriceRangeDto(
                            MIN(c.low), MAX(c.high))
            from FiveMinuteCandle  c
            where c.company.id=:companyId
            and c.timestamp >=:from
            """)
    PriceRangeDto findRange(Integer companyId, LocalDateTime from);

    Optional<FiveMinuteCandle>
    findFirstByCompanyIdAndTimestampGreaterThanEqualOrderByTimestampAsc(
            Integer companyId,
            LocalDateTime timestamp
    );
}
