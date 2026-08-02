package com.i2iacademy.voltwise.Home_Management.Home;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplianceRepository extends JpaRepository<Appliance, UUID>{
    List<Appliance> findByHome_HomeId(UUID homeId);
}
