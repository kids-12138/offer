<template>
  <div class="login-bg d-flex align-items-center justify-content-center">
    <div class="card p-4" style="width: 400px">
      <h4 class="text-center mb-4 fw-bold">注册账号</h4>
      <div v-if="err" class="alert alert-danger py-2">{{ err }}</div>
      <div v-if="ok" class="alert alert-success py-2">
        注册成功！
        <template v-if="form.role === 'ENTERPRISE'">企业账号需管理员审核通过后方可登录。</template>
        <router-link to="/login">去登录</router-link>
      </div>
      <form @submit.prevent="submit" v-if="!ok">
        <div class="mb-3 d-flex gap-3">
          <div class="form-check">
            <input class="form-check-input" type="radio" value="STUDENT" v-model="form.role" id="r1"/>
            <label class="form-check-label" for="r1">学生注册</label>
          </div>
          <div class="form-check">
            <input class="form-check-input" type="radio" value="ENTERPRISE" v-model="form.role" id="r2"/>
            <label class="form-check-label" for="r2">企业注册</label>
          </div>
        </div>
        <div class="mb-3">
          <label class="form-label">{{ form.role === 'ENTERPRISE' ? '企业名称' : '姓名' }}</label>
          <input v-model="form.name" class="form-control"/>
        </div>
        <div class="mb-3">
          <label class="form-label">用户名</label>
          <input v-model="form.username" class="form-control"/>
        </div>
        <div class="mb-3">
          <label class="form-label">密码</label>
          <input v-model="form.password" type="password" class="form-control"/>
        </div>
        <button class="btn btn-primary w-100" :disabled="loading">{{ loading ? '注册中...' : '注 册' }}</button>
      </form>
      <div class="text-center mt-3">
        <router-link to="/login" class="small">已有账号？返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import api from '../api'

const form = reactive({ role: 'STUDENT', name: '', username: '', password: '' })
const err = ref('')
const ok = ref(false)
const loading = ref(false)

async function submit() {
  if (!form.username || !form.password || !form.name) {
    err.value = '请填写完整信息'
    return
  }
  loading.value = true
  err.value = ''
  try {
    await api.post('/auth/register', form)
    ok.value = true
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
