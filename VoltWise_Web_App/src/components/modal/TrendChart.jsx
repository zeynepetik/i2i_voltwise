import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'
import { formatShortDate } from '../../utils/format'

function TrendTooltip({ active, payload, label }) {
  if (!active || !payload || !payload.length) return null
  return (
    <div
      style={{
        background: '#fff',
        border: '1px solid #e4e8f0',
        borderRadius: 8,
        padding: '8px 12px',
        boxShadow: '0 8px 20px rgba(20,24,40,0.12)',
        fontSize: 12.5
      }}
    >
      <div style={{ color: '#6b7280', marginBottom: 2 }}>{formatShortDate(label)}</div>
      <div className="mono" style={{ fontWeight: 700 }}>{payload[0].value.toFixed(2)} kWh</div>
    </div>
  )
}

export default function TrendChart({ trend }) {
  return (
    <div className="chart-card">
      <div className="chart-card__title">14-Day Consumption Trend</div>
      <ResponsiveContainer width="100%" height={190}>
        <AreaChart data={trend} margin={{ top: 6, right: 8, left: -18, bottom: 0 }}>
          <defs>
            <linearGradient id="trendFill" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor="#3b57f5" stopOpacity={0.22} />
              <stop offset="100%" stopColor="#3b57f5" stopOpacity={0} />
            </linearGradient>
          </defs>
          <CartesianGrid strokeDasharray="3 3" stroke="#eef1f6" vertical={false} />
          <XAxis
            dataKey="date"
            tickFormatter={formatShortDate}
            tick={{ fontSize: 11, fill: '#9aa1b1' }}
            axisLine={{ stroke: '#e4e8f0' }}
            tickLine={false}
            interval={1}
          />
          <YAxis tick={{ fontSize: 11, fill: '#9aa1b1' }} axisLine={false} tickLine={false} width={30} />
          <Tooltip content={<TrendTooltip />} />
          <Area
            type="monotone"
            dataKey="kwh"
            stroke="#3b57f5"
            strokeWidth={2.5}
            fill="url(#trendFill)"
            dot={false}
            activeDot={{ r: 4 }}
            isAnimationActive={false}
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  )
}
