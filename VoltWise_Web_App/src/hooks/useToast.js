import { useCallback, useRef, useState } from 'react'

let idCounter = 0

/**
 * Minimal toast queue. Toasts self-dismiss after `duration`ms, and can
 * also be dismissed manually. Kept dependency-free and local to the app
 * rather than pulling in a toast library.
 */
export function useToast() {
  const [toasts, setToasts] = useState([])
  const timers = useRef({})

  const dismiss = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id))
    if (timers.current[id]) {
      clearTimeout(timers.current[id])
      delete timers.current[id]
    }
  }, [])

  const push = useCallback(
    (message, { type = 'info', duration = 4000 } = {}) => {
      const id = ++idCounter
      setToasts((prev) => {
        // Avoid stacking duplicate connection warnings during a rough patch
        if (prev.some((t) => t.message === message)) return prev
        return [...prev, { id, message, type }]
      })
      timers.current[id] = setTimeout(() => dismiss(id), duration)
      return id
    },
    [dismiss]
  )

  return { toasts, push, dismiss }
}
