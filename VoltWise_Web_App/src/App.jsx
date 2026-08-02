import { useMemo, useState } from 'react'
import Header from './components/Header'
import StatCards from './components/StatCards'
import FilterBar from './components/FilterBar'
import HomeGrid from './components/HomeGrid'
import HomeDetailModal from './components/modal/HomeDetailModal'
import ToastContainer from './components/ToastContainer'
import { useLiveHomes } from './hooks/useLiveHomes'
import { useToast } from './hooks/useToast'
import { computeSummary, filterHomes } from './utils/calculations'

export default function App() {
  const { toasts, push, dismiss } = useToast()
  const { homes, loading, status, lastUpdated } = useLiveHomes({
    onError: (msg) => push(msg, { type: 'error' })
  })
  const [filter, setFilter] = useState('all')
  const [selectedHomeId, setSelectedHomeId] = useState(null)

  const summary = useMemo(() => computeSummary(homes), [homes])
  const visibleHomes = useMemo(() => filterHomes(homes, filter), [homes, filter])
  const selectedHome = useMemo(
    () => homes.find((h) => h.id === selectedHomeId) || null,
    [homes, selectedHomeId]
  )

  return (
    <div className="app-shell">
      <Header status={status} lastUpdated={lastUpdated} />

      <main className="main">
        <div className="container">
          <StatCards summary={summary} loading={loading} />

          <FilterBar
            active={filter}
            onChange={setFilter}
            visibleCount={loading ? 0 : visibleHomes.length}
            totalCount={loading ? 0 : homes.length}
          />

          <HomeGrid homes={visibleHomes} loading={loading} onOpenHome={setSelectedHomeId} />
        </div>
      </main>

      {selectedHomeId && (
        <HomeDetailModal
          homeId={selectedHomeId}
          liveHome={selectedHome}
          onClose={() => setSelectedHomeId(null)}
        />
      )}

      <ToastContainer toasts={toasts} onClose={dismiss} />
    </div>
  )
}
