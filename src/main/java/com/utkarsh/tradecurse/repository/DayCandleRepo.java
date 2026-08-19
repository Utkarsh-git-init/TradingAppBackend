package com.utkarsh.tradecurse.repository;

import com.utkarsh.tradecurse.dto.PriceRangeDto;
import com.utkarsh.tradecurse.entity.Company;
import com.utkarsh.tradecurse.entity.DayCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface DayCandleRepo extends JpaRepository<DayCandle, Long> {

    @Query("""
            SELECT new com.utkarsh.tradecurse.dto.PriceRangeDto(
                            MIN(c.low), MAX(c.high))
            from DayCandle c
            where c.company.id=:companyId
            and c.timestamp>=:from
            """)
    PriceRangeDto findRange(Integer companyId, LocalDateTime from);

    Optional<DayCandle>
    findFirstByCompanyIdAndTimestampGreaterThanEqualOrderByTimestampAsc(
            Integer companyId,
            LocalDateTime timestamp
    );
}
