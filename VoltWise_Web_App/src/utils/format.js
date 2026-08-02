/**
 * Small, dependency-free formatting helpers shared across the app.
 * Keeping these pure functions in one place means every card, modal,
 * and chart renders numbers identically.
 */

export function formatCurrency(value) {
  return `$${value.toFixed(2)}`
}

export function formatKw(value) {
  return value.toFixed(1)
}

export function formatWatts(value) {
  return Math.round(value).toLocaleString('en-US')
}

export function formatKwh(value) {
  return value.toFixed(1)
}

export function clamp(value, min, max) {
  return Math.min(Math.max(value, min), max)
}

export function quotaPercent(value, quota) {
  if (quota <= 0) return 0
  return clamp(Math.round((value / quota) * 100), 0, 100)
}

export function formatTime(date) {
  return date.toLocaleTimeString('en-US', {
    hour12: false,
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

export function formatShortDate(isoDate) {
  const d = new Date(isoDate)
  return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}
