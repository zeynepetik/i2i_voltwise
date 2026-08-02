const FILTERS = [
  { key: 'all', label: 'All Homes' },
  { key: 'breached', label: 'Quota Breached' },
  { key: 'anomalies', label: 'Anomalies' }
]

export default function FilterBar({ active, onChange, visibleCount, totalCount }) {
  return (
    <div className="filter-bar">
      <div className="filter-bar__group">
        <span className="filter-bar__label">Filter:</span>
        {FILTERS.map((f) => (
          <button
            key={f.key}
            className={`filter-chip${active === f.key ? ' active' : ''}`}
            onClick={() => onChange(f.key)}
          >
            {f.label}
          </button>
        ))}
      </div>
      <span className="filter-count">
        {visibleCount} of {totalCount} homes
      </span>
    </div>
  )
}
