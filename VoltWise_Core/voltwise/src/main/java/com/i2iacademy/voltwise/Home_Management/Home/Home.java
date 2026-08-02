package com.i2iacademy.voltwise.Home_Management.Home;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="homes")
@Getter
@Setter
@NoArgsConstructor
public class Home {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "home_id")
    private UUID homeId;

    @Column(name = "home_name", nullable = false, length = 50)
    private String homeName;

    @Column(name = "home_address", nullable = false, length = 150)
    private String homeAddress;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "base_tariff_rate", nullable = false, precision = 18, scale = 2)
    private BigDecimal baseTariffRate;

    @Column(name = "penalty_tariff_rate", nullable = false, precision = 18, scale = 2)
    private BigDecimal penaltyTariffRate;
}
