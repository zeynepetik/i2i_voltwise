package com.i2iacademy.voltwise.System_Log;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.i2iacademy.voltwise.Home_Management.Home.Appliance;
import com.i2iacademy.voltwise.Home_Management.Home.Home;

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

@Entity
@Table(name="system_log")
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "log_id")
    private UUID logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_id", nullable = false)
    private Home home;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")   // nullable — home seviyesi event'lerde (quota) appliance yok
    private Appliance appliance;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private SystemLogEventType eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", columnDefinition = "jsonb")
    private Map<String, Object> details;

    protected SystemLog() {
        // JPA için
    }

    public static SystemLog of(Home home, Appliance appliance, SystemLogEventType eventType, Map<String, Object> details) {
        SystemLog log = new SystemLog();
        log.home = home;
        log.appliance = appliance;
        log.occurredAt = Instant.now();
        log.eventType = eventType;
        log.details = details;
        return log;
    }

    // getter'lar — HomeLiveState'teki konvansiyonla aynı: sadece getter, mutasyon yok
    public UUID getLogId() { return logId; }
    public Home getHome() { return home; }
    public Appliance getAppliance() { return appliance; }
    public Instant getOccurredAt() { return occurredAt; }
    public SystemLogEventType getEventType() { return eventType; }
    public Map<String, Object> getDetails() { return details; }
}
