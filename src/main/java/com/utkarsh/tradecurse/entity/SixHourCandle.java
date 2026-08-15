package com.utkarsh.tradecurse.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"company_id", "timestamp"}
        ),
        indexes = {
                @Index(name = "idx_six_hour_company_timestamp", columnList = "company_id,timestamp")
        }
)
public class SixHourCandle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    private LocalDateTime timestamp;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal open;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal high;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal low;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal close;
}
