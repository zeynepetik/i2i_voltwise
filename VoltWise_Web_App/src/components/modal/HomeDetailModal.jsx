import { useEffect, useRef, useState, useCallback } from 'react'
import { X, AlertTriangle, RefreshCw } from 'lucide-react'
import { fetchHomeDetail, toFriendlyError } from '../../data/mockApi'
import { formatCurrency, formatKw, formatKwh } from '../../utils/format'
import { anomalousAppliances, isQuotaBreached } from '../../utils/calculations'
import { ModalSkeleton } from '../Skeletons'
import BudgetOverview from './BudgetOverview'
import AIAlerts from './AIAlerts'
import ApplianceList from './ApplianceList'
import TrendChart from './TrendChart'
import CostChart from './CostChart'

// Merges freshly-polled grid fields into the richer detail payload without
// disturbing the parts (history, notes) that only the detail endpoint returns.
function mergeLive(detail, liveHome) {
  if (!detail || !liveHome) return detail
  const wattsById = new Map(liveHome.appliances.map((a) => [a.id, a.watts]))
  return {
    ...detail,
    liveDrawKw: liveHome.liveDrawKw,
    dailyUsageKwh: liveHome.dailyUsageKwh,
    monthlyBill: liveHome.monthlyBill,
    appliances: detail.appliances.map((a) =>
      wattsById.has(a.id) ? { ...a, watts: wattsById.get(a.id) } : a
    )
  }
}

export default function HomeDetailModal({ homeId, liveHome, onClose }) {
  const [status, setStatus] = useState('loading') // 'loading' | 'error' | 'ready'
  const [detail, setDetail] = useState(null)
  const [errorMsg, setErrorMsg] = useState('')
  const overlayRef = useRef(null)

  const load = useCallback(() => {
    setStatus('loading')
    fetchHomeDetail(homeId)
      .then((data) => {
        setDetail(data)
        setStatus('ready')
      })
      .catch((err) => {
        setErrorMsg(toFriendlyError(err))
        setStatus('error')
      })
  }, [homeId])

  useEffect(() => {
    load()
  }, [load])

  // Keep the open modal in sync with the dashboard's live polling loop.
  useEffect(() => {
    if (status === 'ready' && liveHome) {
      setDetail((prev) => mergeLive(prev, liveHome))
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [liveHome])

  useEffect(() => {
    function onKeyDown(e) {
      if (e.key === 'Escape') onClose()
    }
    document.addEventListener('keydown', onKeyDown)
    return () => document.removeEventListener('keydown', onKeyDown)
  }, [onClose])

  function handleOverlayClick(e) {
    if (e.target === overlayRef.current) onClose()
  }

  const breached = detail ? isQuotaBreached(detail) : false
  const anomalies = detail ? anomalousAppliances(detail) : []

  return (
    <div className="modal-overlay" ref={overlayRef} onMouseDown={handleOverlayClick}>
      <div className="modal-panel" role="dialog" aria-modal="true" aria-label={detail?.name || 'Home details'}>
        <div className={`modal-header${breached ? ' breached' : ''}`}>
          <button className="modal-close" onClick={onClose} aria-label="Close">
            <X size={17} />
          </button>
          <div className="modal-header__top">
            <span className="modal-header__title">
              {status === 'loading' ? 'Loading home…' : detail?.name}
            </span>
            {breached && (
              <span className="pill" style={{ background: 'var(--red)', color: '#fff' }}>
                <AlertTriangle size={12} /> QUOTA BREACHED
              </span>
            )}
          </div>
          {detail && (
            <>
              <div className="modal-header__address">{detail.address}</div>
              <div className="modal-header__meta">
                {detail.occupants} occupants · {detail.sqft.toLocaleString()} sq ft
              </div>
            </>
          )}
        </div>

        {status === 'loading' && <ModalSkeleton />}

        {status === 'error' && (
          <div className="modal-body">
            <div className="modal-error">
              <AlertTriangle size={16} />
              <span>{errorMsg}</span>
              <button className="modal-retry" onClick={load}>
                <RefreshCw size={12} style={{ marginRight: 5, verticalAlign: -2 }} />
                Retry
              </button>
            </div>
          </div>
        )}

        {status === 'ready' && detail && (
          <div className="modal-body">
            <div className="stat-mini-grid">
              <div className={`stat-mini${detail.liveDrawKw >= detail.quotaKw ? ' stat-mini--danger' : ''}`}>
                <div className="stat-mini__label">Live Draw</div>
                <div className="stat-mini__value mono">
                  {formatKw(detail.liveDrawKw)}
                  <span className="unit">kW</span>
                </div>
                <div className="stat-mini__sub">Quota: {detail.quotaKw} kW</div>
              </div>
              <div className="stat-mini">
                <div className="stat-mini__label">Daily Usage</div>
                <div className="stat-mini__value mono">
                  {formatKwh(detail.dailyUsageKwh)}
                  <span className="unit">kWh</span>
                </div>
                <div className="stat-mini__sub">Today so far</div>
              </div>
              <div className={`stat-mini${detail.monthlyBill > detail.billQuota ? ' stat-mini--danger' : ''}`}>
                <div className="stat-mini__label">Monthly Bill</div>
                <div className="stat-mini__value mono">{formatCurrency(detail.monthlyBill)}</div>
                <div className="stat-mini__sub">Budget: {formatCurrency(detail.billQuota)}</div>
              </div>
              <div className={`stat-mini${anomalies.length > 0 ? ' stat-mini--warn' : ''}`}>
                <div className="stat-mini__label">Appliances</div>
                <div className="stat-mini__value mono">{detail.appliances.length} total</div>
                <div className="stat-mini__sub">
                  {anomalies.length} anomalous
                </div>
              </div>
            </div>

            <BudgetOverview home={detail} breached={breached} />
            <AIAlerts alerts={detail.aiAlerts} />
            <ApplianceList appliances={detail.appliances} />
            <TrendChart trend={detail.trend} />
            <CostChart cost={detail.cost} />
          </div>
        )}
      </div>
    </div>
  )
}
