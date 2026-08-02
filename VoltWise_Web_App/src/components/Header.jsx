import { Zap, Wifi } from 'lucide-react'
import { formatTime } from '../utils/format'

export default function Header({ status, lastUpdated }) {
  const reconnecting = status === 'reconnecting'

  return (
    <header className="app-header">
      <div className="container app-header__inner">
        <div className="brand">
          <div className="brand__mark">
            <Zap size={18} fill="currentColor" strokeWidth={0} />
          </div>
          <span className="brand__name">VoltWise</span>
          <span className="brand__tagline">Energy Analytics Platform</span>
        </div>

        <div className="header-status">
          <span className="updated-at">
            Updated {lastUpdated ? formatTime(lastUpdated) : '—'}
          </span>
          <span className={`live-badge${reconnecting ? ' reconnecting' : ''}`}>
            <Wifi size={13} />
            {reconnecting ? 'Reconnecting' : 'Live'}
            <span className="live-dot" />
          </span>
        </div>
      </div>
    </header>
  )
}
