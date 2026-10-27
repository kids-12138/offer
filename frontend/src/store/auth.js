import { reactive } from 'vue'

const KEY = 'kfszy_user'

function load() {
  try {
    return JSON.parse(sessionStorage.getItem(KEY))
  } catch (e) {
    return null
  }
}

export const auth = reactive({
  user: load(),
  setUser(u) {
    this.user = u
    if (u) sessionStorage.setItem(KEY, JSON.stringify(u))
    else sessionStorage.removeItem(KEY)
  }
})

export function homeOf(role) {
  if (role === 'ADMIN') return '/admin/stats'
  if (role === 'ENTERPRISE') return '/ent/jobs'
  return '/student/jobs'
}
