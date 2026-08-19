package com.utkarsh.tradecurse.enums;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum Interval {
    ONE_HOUR(Duration.ofHours(1)),
    SIX_HOUR(Duration.ofHours(6)),
    TWENTY_FOUR_HOURS(Duration.ofHours(24)),
    ONE_WEEK(Duration.ofDays(7)),
    ONE_MONTH(Duration.ofDays(30)),
    ONE_YEAR(Duration.ofDays(365));

    private final Duration duration;

    Interval(Duration duration){
        this.duration=duration;
    }

}