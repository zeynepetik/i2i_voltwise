import { quotaPercent } from '../utils/format'

export default function QuotaBar({ label, value, quota, breached }) {
  const pct = quotaPercent(value, quota)

  return (
    <div>
      <div className="quota-section__head">
        <span>{label}</span>
        <span>{pct}%</span>
      </div>
      <div className="quota-track">
        <div
          className={`quota-fill ${breached ? 'breached' : 'ok'}`}
          style={{ width: `${pct}%` }}
        />
      </div>
    </div>
  )
}
