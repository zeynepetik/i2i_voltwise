import { useEffect, useRef, useState } from 'react'
import { fetchHomes, toFriendlyError } from '../data/mockApi'

const POLL_INTERVAL_MS = 1500

/**
 * Owns the dashboard's live data lifecycle:
 *  - initial load (with a skeleton-driving `loading` flag)
 *  - background polling every 1.5s without blocking the UI
 *  - connection status ('live' | 'reconnecting') for the header badge
 *  - friendly error messages surfaced through onError, never raw stack traces
 *
 * The polling loop schedules its *next* call only after the current one
 * settles, so a slow or failed request can never pile up overlapping
 * requests or cause jank in the grid/chart updates.
 */
export function useLiveHomes({ onError } = {}) {
  const [homes, setHomes] = useState([])
  const [loading, setLoading] = useState(true)
  const [status, setStatus] = useState('live') // 'live' | 'reconnecting'
  const [lastUpdated, setLastUpdated] = useState(null)
  const mounted = useRef(true)
  const onErrorRef = useRef(onError)
  onErrorRef.current = onError

  useEffect(() => {
    mounted.current = true
    let timer = null

    async function poll() {
      try {
        const data = await fetchHomes()
        if (!mounted.current) return
        setHomes(data)
        setLastUpdated(new Date())
        setStatus('live')
      } catch (err) {
        if (!mounted.current) return
        setStatus('reconnecting')
        onErrorRef.current?.(toFriendlyError(err))
      } finally {
        if (!mounted.current) return
        setLoading(false)
        timer = setTimeout(poll, POLL_INTERVAL_MS)
      }
    }

    poll()

    return () => {
      mounted.current = false
      if (timer) clearTimeout(timer)
    }
  }, [])

  return { homes, loading, status, lastUpdated }
}
