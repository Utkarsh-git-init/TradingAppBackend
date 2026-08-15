package com.utkarsh.tradecurse.repository;

import com.utkarsh.tradecurse.entity.DayCandle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayCandleRepo extends JpaRepository<DayCandle, Long> {
}
