const BASE_URL = import.meta.env.VITE_API_BASE_URL

function mapHomeSummary(dto) {
  return {
    id: dto.homeId,
    name: dto.homeName,
    address: dto.homeAddress,
    liveDrawKw: dto.liveDrawKw,
    quotaKw: dto.quotaKw,
    dailyUsageKwh: dto.dailyUsageKwh,
    monthlyBill: dto.monthlyBill,
    billQuota: dto.billQuota,
    appliances: [],
    aiAlerts: [],      // YENİ — grid'de badge sayısı için gerekli, backend'de henüz sayılmıyor
    occupants: 0,       // YENİ — backend'de bu kavram yok, placeholder
    sqft: 0              // YENİ — backend'de bu kavram yok, placeholder
  }
}

export async function fetchHomes() {
  const res = await fetch(`${BASE_URL}/homes`)
  if (!res.ok) throw new Error('NETWORK_TIMEOUT')
  const data = await res.json()
  return data.map(mapHomeSummary)
}

function mapApplianceLive(dto) {
  return {
    id: dto.appId,
    category: dto.appCategory,
    name: dto.appName,
    icon: dto.appCategory,       // applianceIcons.js'teki mapleme ile uyumlu olmalı
    watts: Number(dto.currentWatt),
    kwhDay: 0,                   // backend'de appliance-seviye günlük kWh yok, bugün 0
    status: dto.status,
    note: '',
    violations: dto.consecutiveBreaches
  }
}

export async function fetchHomeDetail(homeId) {
  const [summaryRes, appliancesRes, adviceRes, historyRes] = await Promise.all([
    fetch(`${BASE_URL}/homes`),
    fetch(`${BASE_URL}/homes/${homeId}/appliances`),
    fetch(`${BASE_URL}/homes/${homeId}/ai_advice`),
    fetch(`${BASE_URL}/homes/${homeId}/history`)
  ])
  if (!summaryRes.ok || !appliancesRes.ok || !adviceRes.ok || !historyRes.ok) throw new Error('NETWORK_TIMEOUT')

  const allHomes = await summaryRes.json()
  const homeDto = allHomes.find((h) => h.homeId === homeId)
  if (!homeDto) throw new Error('NOT_FOUND')

  const appliancesDto = await appliancesRes.json()
  const adviceDto = await adviceRes.json()
  const historyDto = await historyRes.json()

  return {
    ...mapHomeSummary(homeDto),
    appliances: appliancesDto.map(mapApplianceLive),
    trend: historyDto.map((h) => ({ date: h.snapshotDate, kwh: Number(h.totalPower) })),
    cost: historyDto.map((h) => ({ date: h.snapshotDate, cost: Number(h.totalCost) })),
    aiAlerts: adviceDto.map((a) => a.adviceText)
  }
}

export function toFriendlyError(err) {
  const code = err?.message
  switch (code) {
    case 'NETWORK_TIMEOUT':
      return 'Connection to the telemetry service timed out. Retrying shortly.'
    case 'NOT_FOUND':
      return "This home's data is no longer available."
    default:
      return "Something interrupted the live feed. We're reconnecting automatically."
  }
}