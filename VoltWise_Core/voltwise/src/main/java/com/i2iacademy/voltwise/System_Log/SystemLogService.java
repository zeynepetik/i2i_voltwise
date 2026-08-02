package com.i2iacademy.voltwise.System_Log;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.i2iacademy.voltwise.Home_Management.Home.Appliance;
import com.i2iacademy.voltwise.Home_Management.Home.ApplianceRepository;
import com.i2iacademy.voltwise.Home_Management.Home.Home;
import com.i2iacademy.voltwise.Home_Management.Home.HomeRepository;

@Service
public class SystemLogService {
    private static final Logger log = LoggerFactory.getLogger(SystemLogService.class);

    private final SystemLogRepository systemLogRepository;
    private final HomeRepository homeRepository;
    private final ApplianceRepository applianceRepository;

    public SystemLogService(SystemLogRepository systemLogRepository,
                             HomeRepository homeRepository,
                             ApplianceRepository applianceRepository) {
        this.systemLogRepository = systemLogRepository;
        this.homeRepository = homeRepository;
        this.applianceRepository = applianceRepository;
    }

    public UUID log(UUID homeId, UUID appId, SystemLogEventType eventType, Map<String, Object> details) {
        try {
            /*repoda getReference oluştrumak yerine burda getReferenceById kullanıldı
            Çünkü JPAye gereçek veriyi select iel çekme, bu idye proxy nesensi var diyoruz
            saece fk ilişkisi kaydedilmesi için
            bu satyede her logda select sorgusu yapılmaz
            */
            Home homeRef = homeRepository.getReferenceById(homeId);
            Appliance applianceRef = appId != null ? applianceRepository.getReferenceById(appId) : null;
            SystemLog entry = SystemLog.of(homeRef, applianceRef, eventType, details);
            systemLogRepository.save(entry);
            return entry.getLogId();
        } catch (Exception e) {
            log.error("system_log yazılamadı: homeId={}, eventType={}", homeId, eventType, e);
            return null;
        }
    }
}
