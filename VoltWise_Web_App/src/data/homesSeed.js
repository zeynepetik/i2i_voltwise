/**
 * Deterministic mock "telemetry" seed data.
 *
 * In production this file would not exist — the SPA would hydrate purely
 * from the VoltWise backend/API layer. It stands in for that service here
 * so the frontend can be built, demoed, and polled end-to-end in isolation.
 */

// Small deterministic PRNG so history charts look organic but never
// change shape on every re-render (they only get a fresh "today" jitter).
function mulberry32(seed) {
  return function () {
    seed |= 0
    seed = (seed + 0x6d2b79f5) | 0
    let t = Math.imul(seed ^ (seed >>> 15), 1 | seed)
    t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296
  }
}

function buildTrend(seedNum, base, days = 14, startDate = '2026-07-14') {
  const rand = mulberry32(seedNum)
  const trend = []
  const cost = []
  const start = new Date(startDate)
  for (let i = 0; i < days; i++) {
    const d = new Date(start)
    d.setDate(start.getDate() + i)
    const wobble = (rand() - 0.5) * base * 0.22
    const kwh = Math.max(base * 0.5, base + wobble)
    trend.push({ date: d.toISOString().slice(0, 10), kwh: Number(kwh.toFixed(1)) })
    cost.push({ date: d.toISOString().slice(0, 10), cost: Number((kwh * 0.155).toFixed(2)) })
  }
  return { trend, cost }
}

const applianceCategories = {
  hvac: { icon: 'hvac', label: 'HVAC System' },
  waterHeater: { icon: 'waterHeater', label: 'Water Heater' },
  dryer: { icon: 'dryer', label: 'Electric Dryer' },
  fridge: { icon: 'fridge', label: 'Refrigerator' },
  washer: { icon: 'washer', label: 'Washing Machine' },
  tv: { icon: 'tv', label: 'Smart TV' },
  office: { icon: 'office', label: 'Home Office' },
  lighting: { icon: 'lighting', label: 'Lighting' },
  microwave: { icon: 'microwave', label: 'Microwave' },
  coffee: { icon: 'coffee', label: 'Coffee Maker' },
  pool: { icon: 'pool', label: 'Pool Pump' },
  ev: { icon: 'ev', label: 'EV Charger' }
}

function appliance(category, watts, kwhDay, status = 'normal', note = '', violations = 0) {
  return {
    id: `${category}-${Math.random().toString(36).slice(2, 8)}`,
    category,
    name: applianceCategories[category].label,
    icon: applianceCategories[category].icon,
    watts,
    kwhDay,
    status, // 'normal' | 'warning' | 'critical'
    note,
    violations
  }
}

const { trend: martinezTrend, cost: martinezCost } = buildTrend(11, 47, 14)
const { trend: nguyenTrend, cost: nguyenCost } = buildTrend(22, 28, 14)
const { trend: okaforTrend, cost: okaforCost } = buildTrend(33, 68, 14)
const { trend: sullivanTrend, cost: sullivanCost } = buildTrend(44, 18, 14)
const { trend: patelTrend, cost: patelCost } = buildTrend(55, 41, 14)
const { trend: lindbergTrend, cost: lindbergCost } = buildTrend(66, 16, 14)

export const HOMES_SEED = [
  {
    id: 'martinez-residence',
    name: 'Martinez Residence',
    address: '1482 Cedarwood Lane, Austin TX',
    occupants: 4,
    sqft: 2400,
    liveDrawKw: 6.8,
    quotaKw: 6,
    dailyUsageKwh: 48.4,
    monthlyBill: 206.07,
    billQuota: 180,
    trend: martinezTrend,
    cost: martinezCost,
    aiAlerts: [
      'HVAC runtime exceeding 14h/day — check thermostat schedule',
      'Peak draw at 7–9 PM suggests behavior shift'
    ],
    appliances: [
      appliance('hvac', 4557, 11.5, 'critical', 'HVAC running 38% above seasonal baseline — possible refrigerant leak', 9),
      appliance('waterHeater', 4973, 16.2, 'warning', 'Heating cycle repeated 11x today — sediment buildup suspected', 4),
      appliance('dryer', 5253, 17.4, 'normal'),
      appliance('fridge', 170, 0.9, 'normal'),
      appliance('washer', 497, 1.1, 'normal'),
      appliance('tv', 126, 0.3, 'normal'),
      appliance('office', 375, 1.3, 'normal'),
      appliance('lighting', 418, 2.2, 'normal'),
      appliance('microwave', 1206, 6.3, 'normal'),
      appliance('coffee', 743, 4.0, 'normal')
    ]
  },
  {
    id: 'nguyen-household',
    name: 'Nguyen Household',
    address: '309 Birchwood Court, Denver CO',
    occupants: 3,
    sqft: 1850,
    liveDrawKw: 3.3,
    quotaKw: 5,
    dailyUsageKwh: 29.8,
    monthlyBill: 125.6,
    billQuota: 160,
    trend: nguyenTrend,
    cost: nguyenCost,
    aiAlerts: ['Dishwasher cycle running later than usual pattern — no action needed'],
    appliances: [
      appliance('hvac', 2210, 8.1, 'normal'),
      appliance('waterHeater', 1890, 6.4, 'normal'),
      appliance('fridge', 148, 0.8, 'normal'),
      appliance('washer', 412, 0.9, 'normal'),
      appliance('tv', 98, 0.2, 'normal'),
      appliance('office', 290, 1.0, 'normal'),
      appliance('lighting', 260, 1.4, 'normal'),
      appliance('coffee', 610, 3.1, 'normal')
    ]
  },
  {
    id: 'okafor-estate',
    name: 'Okafor Estate',
    address: '7710 Marble Ridge Drive, Phoenix AZ',
    occupants: 6,
    sqft: 4100,
    liveDrawKw: 8.9,
    quotaKw: 8,
    dailyUsageKwh: 71.7,
    monthlyBill: 305.13,
    billQuota: 250,
    trend: okaforTrend,
    cost: okaforCost,
    aiAlerts: [
      'Pool pump running continuously for 6 days — verify timer settings',
      'EV charger drawing peak-rate power on 3 consecutive nights'
    ],
    appliances: [
      appliance('hvac', 6120, 19.4, 'critical', 'Dual-zone HVAC cycling 2.6x more than baseline — filter check recommended', 7),
      appliance('pool', 2380, 14.1, 'warning', 'Pump running 22h/day — expected 8h/day schedule', 6),
      appliance('ev', 7400, 18.8, 'warning', 'Charging sessions shifted into peak tariff window', 3),
      appliance('waterHeater', 3210, 9.7, 'normal'),
      appliance('fridge', 210, 1.1, 'normal'),
      appliance('washer', 520, 1.2, 'normal'),
      appliance('dryer', 4890, 15.9, 'normal'),
      appliance('tv', 165, 0.4, 'normal'),
      appliance('office', 410, 1.6, 'normal'),
      appliance('lighting', 640, 3.3, 'normal')
    ]
  },
  {
    id: 'sullivan-flat',
    name: 'Sullivan Flat',
    address: '22 Harborview Blvd Apt 4B, Seattle WA',
    occupants: 2,
    sqft: 1100,
    liveDrawKw: 2.1,
    quotaKw: 4,
    dailyUsageKwh: 18.6,
    monthlyBill: 78.91,
    billQuota: 120,
    trend: sullivanTrend,
    cost: sullivanCost,
    aiAlerts: [],
    appliances: [
      appliance('hvac', 1120, 5.2, 'normal'),
      appliance('waterHeater', 980, 3.8, 'normal'),
      appliance('fridge', 132, 0.7, 'normal'),
      appliance('washer', 380, 0.6, 'normal'),
      appliance('tv', 88, 0.2, 'normal'),
      appliance('lighting', 190, 1.0, 'normal'),
      appliance('coffee', 520, 2.4, 'normal')
    ]
  },
  {
    id: 'patel-family-home',
    name: 'Patel Family Home',
    address: '554 Greenstone Pkwy, Atlanta GA',
    occupants: 5,
    sqft: 3200,
    liveDrawKw: 5.5,
    quotaKw: 7,
    dailyUsageKwh: 43.0,
    monthlyBill: 182.58,
    billQuota: 175,
    trend: patelTrend,
    cost: patelCost,
    aiAlerts: ['Water heater standby draw above household average — worth a filter/anode check'],
    appliances: [
      appliance('waterHeater', 4110, 13.2, 'critical', 'Standby draw 31% above similar-size households', 5),
      appliance('hvac', 3980, 12.6, 'normal'),
      appliance('dryer', 5010, 16.8, 'normal'),
      appliance('fridge', 188, 1.0, 'normal'),
      appliance('washer', 460, 1.0, 'normal'),
      appliance('tv', 140, 0.3, 'normal'),
      appliance('office', 340, 1.2, 'normal'),
      appliance('lighting', 520, 2.7, 'normal'),
      appliance('microwave', 1180, 5.9, 'normal')
    ]
  },
  {
    id: 'lindberg-cottage',
    name: 'Lindberg Cottage',
    address: '88 Lakeview Terrace, Minneapolis MN',
    occupants: 2,
    sqft: 1350,
    liveDrawKw: 1.9,
    quotaKw: 3,
    dailyUsageKwh: 16.3,
    monthlyBill: 68.38,
    billQuota: 100,
    trend: lindbergTrend,
    cost: lindbergCost,
    aiAlerts: [],
    appliances: [
      appliance('hvac', 990, 4.4, 'normal'),
      appliance('waterHeater', 860, 3.1, 'normal'),
      appliance('fridge', 124, 0.6, 'normal'),
      appliance('washer', 350, 0.5, 'normal'),
      appliance('tv', 76, 0.2, 'normal'),
      appliance('lighting', 165, 0.9, 'normal'),
      appliance('coffee', 480, 2.1, 'normal')
    ]
  }
]
