import { formatCurrency } from '../../utils/format'
import QuotaBar from '../QuotaBar'

export default function BudgetOverview({ home, breached }) {
  const billOver = home.monthlyBill > home.billQuota

  return (
    <div>
      <div className="section-title">Budget Overview</div>
      <div className="budget-block">
        <QuotaBar
          label="Power quota"
          value={home.liveDrawKw}
          quota={home.quotaKw}
          breached={home.liveDrawKw >= home.quotaKw}
        />
        <div>
          <div className="budget-row__head">
            <span>Bill budget</span>
            <span className="mono">
              {formatCurrency(home.monthlyBill)} / {formatCurrency(home.billQuota)}
            </span>
          </div>
          <div className="quota-track">
            <div
              className={`quota-fill ${billOver ? 'breached' : 'ok'}`}
              style={{
                width: `${Math.min(100, Math.round((home.monthlyBill / home.billQuota) * 100))}%`
              }}
            />
          </div>
        </div>
      </div>
    </div>
  )
}
