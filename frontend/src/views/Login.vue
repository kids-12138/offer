<template>
  <div class="login-bg d-flex align-items-center justify-content-center">
    <div class="card p-4" style="width: 400px">
      <h4 class="text-center mb-1 fw-bold">高校学生就业信息与去向分析平台</h4>
      <p class="text-center text-secondary small mb-4">Employment Information &amp; Destination Analysis</p>
      <div v-if="err" class="alert alert-danger py-2">{{ err }}</div>
      <form @submit.prevent="submit">
        <div class="mb-3">
          <label class="form-label">用户名</label>
          <input v-model="form.username" class="form-control" placeholder="请输入用户名"/>
        </div>
        <div class="mb-3">
          <label class="form-label">密码</label>
          <input v-model="form.password" type="password" class="form-control" placeholder="请输入密码"/>
        </div>
        <button class="btn btn-primary w-100" :disabled="loading">{{ loading ? '登录中...' : '登 录' }}</button>
      </form>
      <div class="text-center mt-3">
        <span class="text-secondary small">还没有账号？</span>
        <router-link to="/register" class="small">立即注册</router-link>
      </div>
      <hr/>
      <div class="small text-secondary">
        测试账号：管理员 admin / admin123，学生 s01 / 123456，企业 huawei / 123456
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api'
import { auth, homeOf } from '../store/auth'

const router = useRouter()
const route = useRoute()
const form = reactive({ username: '', password: '' })
const err = ref('')
const loading = ref(false)

async function submit() {
  if (!form.username || !form.password) {
    err.value = '请输入用户名和密码'
    return
  }
  loading.value = true
  err.value = ''
  try {
    const user = await api.post('/auth/login', form)
    auth.setUser(user)
    router.push(route.query.redirect || homeOf(user.role))
  } catch (e) {
    err.value = e.message
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-bg {
  min-height: 100vh;
  background: linear-gradient(135deg, #2f6fed 0%, #6a5cff 100%);
}
</style>
