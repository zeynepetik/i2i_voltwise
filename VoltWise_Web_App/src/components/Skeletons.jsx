export function StatCardSkeleton() {
  return (
    <div className="skeleton-card">
      <div className="skeleton" style={{ width: 34, height: 34, borderRadius: 9, marginBottom: 12 }} />
      <div className="skeleton" style={{ width: '60%', height: 22, marginBottom: 8 }} />
      <div className="skeleton" style={{ width: '40%', height: 12 }} />
    </div>
  )
}

export function HomeCardSkeleton() {
  return (
    <div className="skeleton-card" style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
        <div style={{ flex: 1 }}>
          <div className="skeleton" style={{ width: '55%', height: 16, marginBottom: 8 }} />
          <div className="skeleton" style={{ width: '75%', height: 12 }} />
        </div>
        <div className="skeleton" style={{ width: 34, height: 34, borderRadius: 9 }} />
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        <div className="skeleton" style={{ height: 52, borderRadius: 8 }} />
        <div className="skeleton" style={{ height: 52, borderRadius: 8 }} />
      </div>
      <div className="skeleton" style={{ height: 56, borderRadius: 8 }} />
      <div className="skeleton" style={{ height: 6, borderRadius: 999 }} />
    </div>
  )
}

export function ApplianceRowSkeleton() {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 14, padding: '10px 2px' }}>
      <div className="skeleton" style={{ width: 34, height: 34, borderRadius: 9 }} />
      <div style={{ flex: 1 }}>
        <div className="skeleton" style={{ width: '40%', height: 14, marginBottom: 6 }} />
        <div className="skeleton" style={{ width: '25%', height: 10 }} />
      </div>
      <div className="skeleton" style={{ width: 60, height: 14 }} />
    </div>
  )
}

export function ModalSkeleton() {
  return (
    <div className="modal-body">
      <div className="stat-mini-grid">
        {Array.from({ length: 4 }).map((_, i) => (
          <div key={i} className="skeleton-card" style={{ padding: 12 }}>
            <div className="skeleton" style={{ width: '50%', height: 10, marginBottom: 8 }} />
            <div className="skeleton" style={{ width: '65%', height: 18 }} />
          </div>
        ))}
      </div>
      <div>
        {Array.from({ length: 5 }).map((_, i) => (
          <ApplianceRowSkeleton key={i} />
        ))}
      </div>
      <div className="skeleton" style={{ height: 180, borderRadius: 12 }} />
    </div>
  )
}
