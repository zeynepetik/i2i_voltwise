import { Activity, ChevronRight } from 'lucide-react'

export default function AIAlerts({ alerts }) {
  if (!alerts || alerts.length === 0) return null

  return (
    <div className="ai-alerts">
      <div className="ai-alerts__title">
        <Activity size={15} />
        AI Behavioral Alerts
      </div>
      {alerts.map((alert, i) => (
        <div className="ai-alerts__item" key={i}>
          <ChevronRight size={14} style={{ marginTop: 2, flexShrink: 0 }} />
          <span>{alert}</span>
        </div>
      ))}
    </div>
  )
}
