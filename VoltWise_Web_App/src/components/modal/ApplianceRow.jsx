import { getApplianceIcon } from '../../utils/applianceIcons'
import { formatWatts, formatKwh } from '../../utils/format'

const STATUS_LABEL = {
  critical: 'Critical',
  warning: 'Warning',
  normal: 'Normal'
}

export default function ApplianceRow({ appliance }) {
  const Icon = getApplianceIcon(appliance.category)
  const flagged = appliance.status !== 'normal'

  return (
    <div className={`appliance-row ${appliance.status}`}>
      <div className="appliance-row__icon">
        <Icon size={16} />
      </div>
      <div className="appliance-row__main">
        <div className="appliance-row__title">
          {appliance.name}
          <span className={`status-chip ${appliance.status}`}>
            <span className="status-dot" />
            {STATUS_LABEL[appliance.status]}
          </span>
          {flagged && appliance.violations > 0 && (
            <span className="appliance-row__violations">{appliance.violations}× violations</span>
          )}
        </div>
        {flagged && appliance.note && <div className="appliance-row__note">{appliance.note}</div>}
      </div>
      <div className="appliance-row__figures">
        <div className="appliance-row__watts mono">{formatWatts(appliance.watts)}W</div>
        <div className="appliance-row__kwh mono">{formatKwh(appliance.kwhDay)} kWh/d</div>
      </div>
    </div>
  )
}
