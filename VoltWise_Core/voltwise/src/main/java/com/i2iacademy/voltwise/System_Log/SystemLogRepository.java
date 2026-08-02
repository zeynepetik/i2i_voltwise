package com.i2iacademy.voltwise.System_Log;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemLogRepository extends JpaRepository<SystemLog, UUID>{

}
