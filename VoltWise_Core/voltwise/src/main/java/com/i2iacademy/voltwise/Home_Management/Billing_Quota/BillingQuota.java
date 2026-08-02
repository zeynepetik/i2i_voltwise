package com.i2iacademy.voltwise.Home_Management.Billing_Quota;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.i2iacademy.voltwise.Home_Management.Home.Home;
import com.i2iacademy.voltwise.Home_Management.TariffState;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "billing_quota")
@Getter @Setter @NoArgsConstructor
public class BillingQuota {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bill_id")
    private UUID billId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id", nullable = false)
    private Home home;

    @Column(name = "power_quota", nullable = false, precision = 18, scale = 2)
    private BigDecimal powerQuota;

    @Column(name = "bill_quota", nullable = false, precision = 18, scale = 2)
    private BigDecimal billQuota;

    @Enumerated(EnumType.STRING)
    @Column(name = "tariff_state", nullable = false, length = 7)
    private TariffState tariffState;

    @Column(name = "penalty_activated_at")
    private java.time.Instant penaltyActivatedAt;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;
}
