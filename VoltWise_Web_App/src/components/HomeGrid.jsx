import { SearchX } from 'lucide-react'
import HomeCard from './HomeCard'
import { HomeCardSkeleton } from './Skeletons'

export default function HomeGrid({ homes, loading, onOpenHome }) {
  if (loading) {
    return (
      <div className="home-grid">
        {Array.from({ length: 6 }).map((_, i) => (
          <HomeCardSkeleton key={i} />
        ))}
      </div>
    )
  }

  if (homes.length === 0) {
    return (
      <div className="empty-state">
        <div className="empty-state__icon">
          <SearchX size={20} />
        </div>
        <div>No homes match this filter right now.</div>
      </div>
    )
  }

  return (
    <div className="home-grid">
      {homes.map((home) => (
        <HomeCard key={home.id} home={home} onOpen={onOpenHome} />
      ))}
    </div>
  )
}
