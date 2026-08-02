package com.i2iacademy.voltwise.Home_Management.Consumption_Log;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final ConsumptionSnapshotService snapshotService;

    public AdminController(ConsumptionSnapshotService snapshotService) {
        this.snapshotService = snapshotService;
    }

    /**
     * DEV/DEMO amaçlı — production'da bu endpoint kaldırılmalı ya da
     * auth arkasına alınmalı. Şimdilik ödev teslimi için "job gerçekten
     * doğru veri üretiyor mu" diye elle tetiklemek için var.
     */
    @PostMapping("/trigger-snapshot")
    public String triggerSnapshot() {
        int count = snapshotService.takeSnapshotForAllHomes(LocalDate.now());
        return "Snapshot alındı: " + count + " home işlendi.";
    }
}
