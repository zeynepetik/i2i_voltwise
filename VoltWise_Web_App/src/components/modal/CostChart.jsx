import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts'
import { formatShortDate } from '../../utils/format'

function CostTooltip({ active, payload, label }) {
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
      <div className="mono" style={{ fontWeight: 700 }}>${payload[0].value.toFixed(2)}</div>
    </div>
  )
}

export default function CostChart({ cost }) {
  return (
    <div className="chart-card">
      <div className="chart-card__title">Daily Cost Breakdown</div>
      <ResponsiveContainer width="100%" height={190}>
        <BarChart data={cost} margin={{ top: 6, right: 8, left: -18, bottom: 0 }}>
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
          <Tooltip content={<CostTooltip />} cursor={{ fill: 'rgba(124,92,252,0.08)' }} />
          <Bar dataKey="cost" fill="#7c5cfc" radius={[4, 4, 0, 0]} isAnimationActive={false} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}
