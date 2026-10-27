import { createRouter, createWebHistory } from 'vue-router'
import { auth, homeOf } from '../store/auth'
import Layout from '../components/Layout.vue'

const routes = [
  { path: '/login', component: () => import('../views/Login.vue'), meta: { public: true } },
  { path: '/register', component: () => import('../views/Register.vue'), meta: { public: true } },
  {
    path: '/',
    component: Layout,
    children: [
      { path: 'student/jobs', component: () => import('../views/student/Jobs.vue'), meta: { role: 'STUDENT' } },
      { path: 'student/applications', component: () => import('../views/student/MyApplications.vue'), meta: { role: 'STUDENT' } },
      { path: 'student/destination', component: () => import('../views/student/Destination.vue'), meta: { role: 'STUDENT' } },
      { path: 'student/profile', component: () => import('../views/student/Profile.vue'), meta: { role: 'STUDENT' } },
      { path: 'ent/jobs', component: () => import('../views/ent/JobManage.vue'), meta: { role: 'ENTERPRISE' } },
      { path: 'ent/applications', component: () => import('../views/ent/Applications.vue'), meta: { role: 'ENTERPRISE' } },
      { path: 'ent/profile', component: () => import('../views/ent/Profile.vue'), meta: { role: 'ENTERPRISE' } },
      { path: 'admin/stats', component: () => import('../views/admin/Stats.vue'), meta: { role: 'ADMIN' } },
      { path: 'admin/students', component: () => import('../views/admin/Students.vue'), meta: { role: 'ADMIN' } },
      { path: 'admin/enterprises', component: () => import('../views/admin/Enterprises.vue'), meta: { role: 'ADMIN' } },
      { path: 'admin/jobs', component: () => import('../views/admin/Jobs.vue'), meta: { role: 'ADMIN' } },
      { path: 'admin/destinations', component: () => import('../views/admin/Destinations.vue'), meta: { role: 'ADMIN' } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach(to => {
  const user = auth.user
  if (to.path === '/') return user ? homeOf(user.role) : '/login'
  if (to.meta.public) {
    if (user && (to.path === '/login' || to.path === '/register')) return homeOf(user.role)
    return true
  }
  if (!user) return { path: '/login', query: { redirect: to.fullPath } }
  if (to.meta.role && to.meta.role !== user.role) return homeOf(user.role)
  return true
})

export default router
