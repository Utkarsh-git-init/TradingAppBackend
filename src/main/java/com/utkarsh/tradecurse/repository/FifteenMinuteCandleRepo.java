package com.utkarsh.tradecurse.repository;

import com.utkarsh.tradecurse.dto.PriceRangeDto;
import com.utkarsh.tradecurse.entity.Company;
import com.utkarsh.tradecurse.entity.FifteenMinuteCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FifteenMinuteCandleRepo extends JpaRepository<FifteenMinuteCandle, Long> {
    List<FifteenMinuteCandle>
    findByCompanyAndTimestampGreaterThanEqualAndTimestampLessThanOrderByTimestampAsc(
            Company company, LocalDateTime timestampIsGreaterThan, LocalDateTime timestampIsLessThan);

    @Query("""
            SELECT new com.utkarsh.tradecurse.dto.PriceRangeDto(
                            MIN(c.low), MAX(c.high))
            from FifteenMinuteCandle c
            where c.company.id=:companyId
            and c.timestamp>=:from
            """)
    PriceRangeDto findRange(Integer companyId, LocalDateTime from);

    Optional<FifteenMinuteCandle>
    findFirstByCompanyIdAndTimestampGreaterThanEqualOrderByTimestampAsc(
            Integer companyId,
            LocalDateTime timestamp
    );
}
