-- DEMO AMAÇLI: her home için son 14 gün boyunca sentetik, kabaca artan
-- bir tüketim eğrisi üretir. Gerçek production akışında bu veriler
-- takeDailySnapshot() job'u ile organik olarak birikir.
DO $$
DECLARE
    h RECORD;
    i INT;
    base_power NUMERIC;
    base_cost NUMERIC;
BEGIN
    FOR h IN SELECT home_id FROM homes LOOP
        base_power := 15 + random() * 40;   -- 15-55 kWh aralığında rastgele taban
        base_cost := base_power * 0.155;

        FOR i IN 0..13 LOOP
            INSERT INTO consumption_log (id, home_id, total_power, total_cost, created_at, snapshot_date)
            VALUES (
                gen_random_uuid(),
                h.home_id,
                ROUND((base_power + (random() - 0.5) * base_power * 0.3)::numeric, 2),
                ROUND((base_cost + (random() - 0.5) * base_cost * 0.3)::numeric, 2),
                NOW() - ((13 - i) || ' days')::interval,
                (CURRENT_DATE - (13 - i))
            );
        END LOOP;
    END LOOP;
END $$;