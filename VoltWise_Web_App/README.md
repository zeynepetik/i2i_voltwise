# VoltWise — Energy Analytics Platform (Frontend)

React SPA for the VoltWise energy analytics & budget-audit platform. Built with
Vite + React + Recharts, no CSS framework — a hand-rolled design system in
`src/index.css` that mirrors the provided mockups.

## Getting started

```bash
npm install
npm run dev       # http://localhost:5173
npm run build      # production build to dist/
npm run preview    # preview the production build
```

## Project structure

```
src/
  data/
    homesSeed.js        Mock seed data: 6 homes, appliances, 14-day history
    mockApi.js           Simulated network layer (latency + random failures)
  hooks/
    useLiveHomes.js       Polling loop (1.5s) driving the dashboard grid
    useToast.js            Toast queue for user-facing error messages
  utils/
    format.js               Currency/kW/kWh/date formatting helpers
    calculations.js       Quota-breach & anomaly aggregation helpers
    applianceIcons.js    Appliance category → lucide-react icon map
  components/
    Header.jsx                 Brand bar + live/reconnecting status
    StatCards.jsx              Total draw / spend / breaches / anomalies
    FilterBar.jsx                All Homes / Quota Breached / Anomalies
    HomeCard.jsx / HomeGrid.jsx  Dashboard grid + per-home summary card
    QuotaBar.jsx                Shared progress-bar primitive
    Skeletons.jsx                Loading placeholders (grid, stats, modal)
    Toast.jsx / ToastContainer.jsx  Graceful error notifications
    modal/
      HomeDetailModal.jsx    Orchestrates fetch/loading/error/live-merge
      BudgetOverview.jsx      Power quota + bill budget bars
      AIAlerts.jsx                 AI behavioral alert list
      ApplianceList.jsx         Splits anomalous vs. normal devices
      ApplianceRow.jsx          Single appliance line item
      TrendChart.jsx              14-day consumption AreaChart (recharts)
      CostChart.jsx                Daily cost BarChart (recharts)
```

## How the requirements map to the code

- **Real-Time Dashboard Grid** — `HomeGrid` + `HomeCard` render all homes;
  clicking a card opens `HomeDetailModal`, which fetches per-home appliance
  and billing detail.
- **Dynamic Quota Breach / Anomaly Identification** — `isQuotaBreached` /
  `anomalousAppliances` in `utils/calculations.js` drive the red banner on
  breached cards and the anomalous/normal split inside the modal.
- **Interactive Analytical Charts** — `TrendChart` and `CostChart` (Recharts)
  sit under the appliance list inside the modal, driven by 14-day mock
  telemetry.
- **UI Fluidity During Aggressive Polling** — `useLiveHomes` polls every
  1.5s but always waits for the in-flight request to settle before
  scheduling the next one, so updates can never overlap or pile up. Chart
  animations are disabled (`isAnimationActive={false}`) so re-renders don't
  cause jank, and layout dimensions are fixed to avoid shifting.
- **Asynchronous Loading Indication** — Skeleton components
  (`Skeletons.jsx`) cover the initial dashboard load, and the modal shows
  its own `ModalSkeleton` while fetching home detail.
- **Graceful Client-Side Error Interception** — `mockApi.js` randomly
  rejects requests to simulate network failures; `toFriendlyError()`
  translates any failure into plain, human-readable copy, which is the
  only thing ever surfaced to the user (via toast on the dashboard, or an
  inline retry panel in the modal). No raw errors or stack traces reach
  the UI.

## Swapping in a real backend

Everything backend-shaped lives in `src/data/mockApi.js`. Replace
`fetchHomes()` and `fetchHomeDetail()` with real `fetch`/WebSocket calls
that resolve to the same shape used in `homesSeed.js`, and the rest of the
app — hooks, components, charts — needs no changes.
