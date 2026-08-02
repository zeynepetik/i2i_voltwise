package com.i2iacademy.voltwise.Home_Management.Consumption_Log;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.i2iacademy.voltwise.Home_Management.Home.Home;
import com.i2iacademy.voltwise.Home_Management.Home.HomeLiveState;
import com.i2iacademy.voltwise.Home_Management.Home.HomeRepository;

@Service
public class ConsumptionSnapshotService {
    private static final Logger log = LoggerFactory.getLogger(ConsumptionSnapshotService.class);

    private final HomeRepository homeRepository;
    private final ConsumptionLogRepository consumptionLogRepository;
    private final IgniteClient igniteClient;

    public ConsumptionSnapshotService(HomeRepository homeRepository,
                                       ConsumptionLogRepository consumptionLogRepository,
                                       IgniteClient igniteClient) {
        this.homeRepository = homeRepository;
        this.consumptionLogRepository = consumptionLogRepository;
        this.igniteClient = igniteClient;
    }

    /**
     * Her gece yarısı (UTC, container timezone'una göre), her home için
     * Ignite'taki ANLIK kümülatif durumu bir "gün sonu" snapshot'ı olarak
     * Postgres'e dondurur. Neden Ignite'tan okuyoruz, Postgres'ten değil:
     * Ignite zaten "şu an doğru" tek kaynak (source of truth for live state),
     * biz sadece onun bir anlık fotoğrafını kalıcı hale getiriyoruz.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void takeDailySnapshot() {
        log.info("Günlük consumption snapshot job'u başlıyor.");
        int count = takeSnapshotForAllHomes(LocalDate.now());
        log.info("Günlük consumption snapshot tamamlandı: {} home işlendi.", count);
    }

    /**
     * hem gerçek scheduled job hem de manuel test endpoint'i
     * bunu çağırır. Tek bir yerde tutmak, ikisinin davranışının sessizce
     * birbirinden sapmasını (ör. biri BigDecimal scale'ini farklı yuvarlar)
     * önler.
     */
    public int takeSnapshotForAllHomes(LocalDate snapshotDate) {
        List<Home> homes = homeRepository.findAll();
        ClientCache<UUID, HomeLiveState> cache = igniteClient.getOrCreateCache("homeLiveState");
        int processed = 0;

        for (Home home : homes) {
            HomeLiveState state = cache.get(home.getHomeId());
            if (state == null) {
                log.warn("Home {} için Ignite'ta live state yok, snapshot atlanıyor.", home.getHomeId());
                continue;
            }

            ConsumptionLog snapshot = new ConsumptionLog();
            snapshot.setHome(home);
            snapshot.setTotalPower(state.getCumulativeUsageKwh());
            snapshot.setTotalCost(state.getCumulativeBillingAmount());
            snapshot.setCreatedAt(Instant.now());
            snapshot.setSnapshotDate(snapshotDate);

            consumptionLogRepository.save(snapshot);
            processed++;
        }
        return processed;
    }
}
