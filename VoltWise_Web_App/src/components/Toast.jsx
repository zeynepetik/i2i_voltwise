import { AlertTriangle, Info, X } from 'lucide-react'

export default function Toast({ toast, onClose }) {
  const Icon = toast.type === 'error' ? AlertTriangle : toast.type === 'info' ? Info : AlertTriangle

  return (
    <div className={`toast ${toast.type}`} role="status">
      <span className="toast__icon">
        <Icon size={16} />
      </span>
      <span>{toast.message}</span>
      <button className="toast__close" onClick={() => onClose(toast.id)} aria-label="Dismiss notification">
        <X size={14} />
      </button>
    </div>
  )
}
