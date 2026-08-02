import Toast from './Toast'

export default function ToastContainer({ toasts, onClose }) {
  if (toasts.length === 0) return null

  return (
    <div className="toast-stack">
      {toasts.map((t) => (
        <Toast key={t.id} toast={t} onClose={onClose} />
      ))}
    </div>
  )
}
