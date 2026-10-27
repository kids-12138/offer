import axios from 'axios'
import { auth, homeOf } from '../store/auth'
import router from '../router'

const api = axios.create({ baseURL: '/backend/api', timeout: 15000 })

api.interceptors.response.use(
  resp => {
    const r = resp.data
    if (r.code === 401) {
      auth.setUser(null)
      router.push('/login')
      return Promise.reject(new Error(r.msg || '请先登录'))
    }
    if (r.code !== 200) {
      return Promise.reject(new Error(r.msg || '操作失败'))
    }
    return r.data
  },
  () => Promise.reject(new Error('网络错误，请确认后端服务已启动'))
)

export { homeOf }
export default api
