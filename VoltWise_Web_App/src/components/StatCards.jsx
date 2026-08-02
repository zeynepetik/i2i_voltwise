import { Zap, DollarSign, AlertTriangle, Activity } from 'lucide-react'
import { StatCardSkeleton } from './Skeletons'

function StatCard({ icon, iconBg, iconColor, value, label, warn }) {
  return (
    <div className={`stat-card${warn ? ' stat-card--warn' : ''}`}>
      <div className="stat-card__icon" style={{ background: iconBg, color: iconColor }}>
        {icon}
      </div>
      <div className="stat-card__value mono">{value}</div>
      <div className="stat-card__label">{label}</div>
    </div>
  )
}

export default function StatCards({ summary, loading }) {
  if (loading) {
    return (
      <div className="stat-grid">
        {Array.from({ length: 4 }).map((_, i) => (
          <StatCardSkeleton key={i} />
        ))}
      </div>
    )
  }

  const { totalLiveDraw, totalMonthlySpend, quotaBreaches, totalHomes, activeAnomalies } = summary

  return (
    <div className="stat-grid">
      <StatCard
        icon={<Zap size={17} />}
        iconBg="var(--blue-soft)"
        iconColor="var(--blue)"
        value={`${totalLiveDraw.toFixed(1)} kW`}
        label="Total Live Draw"
      />
      <StatCard
        icon={<DollarSign size={17} />}
        iconBg="var(--purple-soft)"
        iconColor="var(--purple)"
        value={`$${totalMonthlySpend.toFixed(2)}`}
        label="Monthly Spend"
      />
      <StatCard
        icon={<AlertTriangle size={17} />}
        iconBg="var(--red-soft)"
        iconColor="var(--red)"
        value={`${quotaBreaches} / ${totalHomes}`}
        label="Quota Breaches"
      />
      <StatCard
        icon={<Activity size={17} />}
        iconBg="var(--amber-soft)"
        iconColor="var(--amber-text)"
        value={`${activeAnomalies} devices`}
        label="Active Anomalies"
        warn
      />
    </div>
  )
}
