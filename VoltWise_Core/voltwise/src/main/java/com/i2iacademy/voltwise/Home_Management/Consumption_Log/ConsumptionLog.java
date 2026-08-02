package com.i2iacademy.voltwise.Home_Management.Consumption_Log;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.i2iacademy.voltwise.Home_Management.Home.Home;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "consumption_log")
@Getter @Setter @NoArgsConstructor
public class ConsumptionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id", nullable = false)
    private Home home;

    @Column(name = "total_power", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalPower;

    @Column(name = "total_cost", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;
}
