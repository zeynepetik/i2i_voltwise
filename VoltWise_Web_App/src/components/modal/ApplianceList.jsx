import { AlertTriangle } from 'lucide-react'
import ApplianceRow from './ApplianceRow'

export default function ApplianceList({ appliances }) {
  const anomalous = appliances.filter((a) => a.status !== 'normal')
  const normal = appliances.filter((a) => a.status === 'normal')

  return (
    <div>
      <div className="section-title">Appliance Consumption</div>

      {anomalous.length > 0 && (
        <div style={{ marginBottom: 18 }}>
          <div className="anomalous-label">
            <AlertTriangle size={13} />
            Anomalous Devices
          </div>
          {anomalous.map((a) => (
            <ApplianceRow key={a.id} appliance={a} />
          ))}
        </div>
      )}

      {normal.length > 0 && (
        <div>
          {anomalous.length > 0 && (
            <div className="anomalous-label" style={{ color: 'var(--text-faint)' }}>
              Normal Devices
            </div>
          )}
          {normal.map((a) => (
            <ApplianceRow key={a.id} appliance={a} />
          ))}
        </div>
      )}
    </div>
  )
}
