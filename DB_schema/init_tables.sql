CREATE TABLE IF NOT EXISTS homes (
    home_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    home_name           VARCHAR(50)  NOT NULL UNIQUE,
    home_address        VARCHAR(150) NOT NULL,
    email                VARCHAR(50)  NOT NULL,
    base_tariff_rate    NUMERIC(18,2) NOT NULL,
    penalty_tariff_rate NUMERIC(18,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS appliances (
    app_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    home_id         UUID NOT NULL REFERENCES homes(home_id) ON DELETE CASCADE,
    app_name        VARCHAR(64) NOT NULL,
    app_category    VARCHAR(32) NOT NULL,
    safe_limit_watt NUMERIC(18,2) NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_appliances_home_id ON appliances(home_id);

CREATE TABLE IF NOT EXISTS billing_quota (
    bill_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    home_id             UUID NOT NULL REFERENCES homes(home_id) ON DELETE CASCADE,
    power_quota         NUMERIC(18,2) NOT NULL, -- kWh
    bill_quota          NUMERIC(18,2) NOT NULL, -- TL
    tariff_state        VARCHAR(7) NOT NULL CHECK (tariff_state IN ('NORMAL', 'PENALTY')),
    penalty_activated_at TIMESTAMPTZ,
    period_start        DATE NOT NULL,
    period_end          DATE NOT NULL,
    CHECK (period_end > period_start)
);
CREATE INDEX IF NOT EXISTS idx_billing_quota_home_id ON billing_quota(home_id);

CREATE TABLE IF NOT EXISTS consumption_log (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    home_id       UUID NOT NULL REFERENCES homes(home_id) ON DELETE CASCADE,
    total_power   NUMERIC(18,2) NOT NULL, -- kWh
    total_cost    NUMERIC(18,2) NOT NULL, -- TL
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    snapshot_date DATE NOT NULL,
    UNIQUE (home_id, snapshot_date)
);
CREATE INDEX IF NOT EXISTS idx_consumption_log_home_id ON consumption_log(home_id);

CREATE TABLE IF NOT EXISTS system_log (
    log_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    home_id      UUID NOT NULL REFERENCES homes(home_id) ON DELETE CASCADE,
    app_id       UUID REFERENCES appliances(app_id) ON DELETE CASCADE,
    occurred_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    event_type   VARCHAR(32) NOT NULL CHECK (event_type IN (
                    'QUOTA_BREACH_80', 'QUOTA_BREACH_100',
                    'PENALTY_TARIFF_ACTIVATED',
                    'DEVICE_ANOMALY_DETECTED', 'DEVICE_ANOMALY_CLEARED',
                    'AI_ADVISORY_GENERATED'
                 )),
    details      JSONB NOT NULL DEFAULT '{}'::jsonb
);
CREATE INDEX IF NOT EXISTS idx_system_log_home_id ON system_log(home_id);
CREATE INDEX IF NOT EXISTS idx_system_log_app_id  ON system_log(app_id);

CREATE TABLE IF NOT EXISTS ai_advice (
    ai_id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    home_id          UUID NOT NULL REFERENCES homes(home_id) ON DELETE CASCADE,
    log_id           UUID NOT NULL REFERENCES system_log(log_id) ON DELETE CASCADE,
    advice_text      TEXT NOT NULL,
    email_dispatched BOOLEAN NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_ai_advice_home_id ON ai_advice(home_id);
CREATE INDEX IF NOT EXISTS idx_ai_advice_log_id  ON ai_advice(log_id);

--on delete cascade added to fasten the test operations