<template>
  <div class="d-flex">
    <div class="sidebar d-flex flex-column">
      <div class="brand">高校学生就业信息<br/>与去向分析平台</div>
      <nav class="nav flex-column mt-2">
        <router-link v-for="m in menus" :key="m.path" :to="m.path" class="nav-link"
                     :class="{ active: $route.path === m.path }">{{ m.title }}</router-link>
      </nav>
    </div>
    <div class="main-area">
      <div class="topbar d-flex align-items-center justify-content-end px-4 gap-3">
        <span class="text-secondary">{{ auth.user?.name || auth.user?.username }}</span>
        <button class="btn btn-sm btn-outline-secondary" @click="logout">退出登录</button>
      </div>
      <div class="content">
        <router-view/>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'
import { auth } from '../store/auth'
import { showToast } from '../utils/toast'

const router = useRouter()
const role = auth.user?.role

const menus = computed(() => {
  if (role === 'ADMIN') {
    return [
      { path: '/admin/stats', title: '数据统计分析' },
      { path: '/admin/students', title: '学生管理' },
      { path: '/admin/enterprises', title: '企业管理' },
      { path: '/admin/jobs', title: '岗位审核' },
      { path: '/admin/destinations', title: '去向登记管理' }
    ]
  }
  if (role === 'ENTERPRISE') {
    return [
      { path: '/ent/jobs', title: '岗位管理' },
      { path: '/ent/applications', title: '投递管理' },
      { path: '/ent/profile', title: '企业信息' }
    ]
  }
  return [
    { path: '/student/jobs', title: '浏览岗位' },
    { path: '/student/applications', title: '我的投递' },
    { path: '/student/destination', title: '去向登记' },
    { path: '/student/profile', title: '个人信息' }
  ]
})

onMounted(async () => {
  if (auth.user && !auth.user.name) {
    try {
      const me = await api.get('/auth/me')
      auth.setUser(me)
    } catch (e) { /* ignore */ }
  }
})

async function logout() {
  try { await api.post('/auth/logout') } catch (e) { /* ignore */ }
  auth.setUser(null)
  showToast('已退出登录')
  router.push('/login')
}
</script>
