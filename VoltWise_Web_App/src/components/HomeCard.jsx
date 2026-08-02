import { AlertTriangle, Home, ChevronRight, Activity, Zap } from 'lucide-react'
import QuotaBar from './QuotaBar'
import { isQuotaBreached, anomalousAppliances } from '../utils/calculations'
import { formatCurrency, formatKw, formatKwh } from '../utils/format'

export default function HomeCard({ home, onOpen }) {
  const breached = isQuotaBreached(home)
  const billOver = home.monthlyBill > home.billQuota
  const anomalies = anomalousAppliances(home)
  const alertCount = home.aiAlerts.length

  return (
    <button className={`home-card${breached ? ' breached' : ''}`} onClick={() => onOpen(home.id)}>
      {breached && (
        <div className="home-card__banner">
          <AlertTriangle size={13} /> QUOTA BREACHED
        </div>
      )}

      <div className="home-card__body">
        <div className="home-card__head">
          <div>
            <div className="home-card__name">{home.name}</div>
            <div className="home-card__address">{home.address}</div>
            <div className="home-card__meta">
              {home.occupants} occupants · {home.sqft.toLocaleString()} sqft
            </div>
          </div>
          <div className="home-card__home-icon">
            <Home size={16} />
          </div>
        </div>

        <div className="metric-row">
          <div className={`metric-box ${home.liveDrawKw >= home.quotaKw ? 'metric-box--draw-alert' : 'metric-box--draw-ok'}`}>
            <div className="metric-box__label">Live Draw</div>
            <div className="metric-box__value mono" style={{ color: home.liveDrawKw >= home.quotaKw ? 'var(--red-text)' : 'var(--blue-text)' }}>
              {formatKw(home.liveDrawKw)}
              <span className="metric-box__unit">kW</span>
            </div>
          </div>
          <div className="metric-box">
            <div className="metric-box__label">Daily Usage</div>
            <div className="metric-box__value mono">
              {formatKwh(home.dailyUsageKwh)}
              <span className="metric-box__unit">kWh</span>
            </div>
          </div>
        </div>

        <div className={`bill-box ${billOver ? 'bill-box--over' : 'bill-box--under'}`}>
          <div>
            <div className="bill-box__label">Monthly Bill</div>
            <div className="bill-box__value mono">
              {formatCurrency(home.monthlyBill)}{' '}
              <span className="bill-box__quota">/ {formatCurrency(home.billQuota)} quota</span>
            </div>
          </div>
          {billOver ? (
            <AlertTriangle size={16} color="var(--red-text)" />
          ) : (
            <Zap size={16} color="var(--green-text)" />
          )}
        </div>

        <QuotaBar label="Power quota" value={home.liveDrawKw} quota={home.quotaKw} breached={breached} />

        {(anomalies.length > 0 || alertCount > 0) && (
          <div className="badge-row">
            {anomalies.length > 0 && (
              <span className="pill pill--amber">
                <AlertTriangle size={12} />
                {anomalies.length} {anomalies.length === 1 ? 'anomaly' : 'anomalies'}
              </span>
            )}
            {alertCount > 0 && (
              <span className="pill pill--purple">
                <Activity size={12} />
                {alertCount} {alertCount === 1 ? 'alert' : 'alerts'}
              </span>
            )}
            <ChevronRight size={16} className="card-chevron" />
          </div>
        )}
      </div>
    </button>
  )
}
