package com.i2iacademy.voltwise.Home_Management.Consumption_Log;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumptionLogRepository extends JpaRepository<ConsumptionLog, UUID>{
    List<ConsumptionLog> findByHome_HomeIdOrderBySnapshotDateAsc(UUID homeId);
}
