import { reactive } from 'vue'

export const toast = reactive({ show: false, msg: '', type: 'success' })

export function showToast(msg, type = 'success') {
  toast.msg = msg
  toast.type = type
  toast.show = true
  clearTimeout(showToast._timer)
  showToast._timer = setTimeout(() => { toast.show = false }, 2500)
}
