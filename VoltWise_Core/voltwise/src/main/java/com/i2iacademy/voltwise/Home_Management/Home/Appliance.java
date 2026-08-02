package com.i2iacademy.voltwise.Home_Management.Home;

import java.math.BigDecimal;
import java.util.UUID;

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
@Table(name ="appliances")
@Getter
@Setter
@NoArgsConstructor
public class Appliance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "app_id")
    private UUID appId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id", nullable = false)
    private Home home;

    @Column(name = "app_name", nullable = false, length = 64)
    private String appName;

    @Column(name = "app_category", nullable = false, length = 32)
    private String appCategory;

    @Column(name = "safe_limit_watt", nullable = false, precision = 18, scale = 2)
    private BigDecimal safeLimitWatt;
}
