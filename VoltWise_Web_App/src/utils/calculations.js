export function isQuotaBreached(home) {
  return home.liveDrawKw >= home.quotaKw || home.monthlyBill > home.billQuota
}

export function anomalousAppliances(home) {
  return home.appliances.filter((a) => a.status !== 'normal')
}

export function computeSummary(homes) {
  const totalLiveDraw = homes.reduce((sum, h) => sum + h.liveDrawKw, 0)
  const totalMonthlySpend = homes.reduce((sum, h) => sum + h.monthlyBill, 0)
  const quotaBreaches = homes.filter(isQuotaBreached).length
  const activeAnomalies = homes.reduce((sum, h) => sum + anomalousAppliances(h).length, 0)

  return {
    totalLiveDraw,
    totalMonthlySpend,
    quotaBreaches,
    totalHomes: homes.length,
    activeAnomalies
  }
}

export function filterHomes(homes, filter) {
  switch (filter) {
    case 'breached':
      return homes.filter(isQuotaBreached)
    case 'anomalies':
      return homes.filter((h) => anomalousAppliances(h).length > 0)
    default:
      return homes
  }
}
