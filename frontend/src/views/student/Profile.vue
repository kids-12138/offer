<template>
  <div class="card p-4">
    <h5 class="mb-4">个人信息</h5>
    <form @submit.prevent="save" style="max-width: 640px">
      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label">姓名 *</label>
          <input v-model="form.name" class="form-control" required/>
        </div>
        <div class="col-md-6">
          <label class="form-label">学号</label>
          <input v-model="form.studentNo" class="form-control"/>
        </div>
        <div class="col-md-6">
          <label class="form-label">性别</label>
          <select v-model="form.gender" class="form-select">
            <option value="">请选择</option>
            <option>男</option>
            <option>女</option>
          </select>
        </div>
        <div class="col-md-6">
          <label class="form-label">年级</label>
          <input v-model="form.grade" class="form-control" placeholder="如 2022"/>
        </div>
        <div class="col-md-6">
          <label class="form-label">学院</label>
          <input v-model="form.college" class="form-control" placeholder="如 计算机学院"/>
        </div>
        <div class="col-md-6">
          <label class="form-label">专业</label>
          <input v-model="form.major" class="form-control" placeholder="如 软件工程"/>
        </div>
        <div class="col-md-6">
          <label class="form-label">班级</label>
          <input v-model="form.className" class="form-control" placeholder="如 软件2201"/>
        </div>
        <div class="col-md-6">
          <label class="form-label">联系电话</label>
          <input v-model="form.phone" class="form-control"/>
        </div>
        <div class="col-md-6">
          <label class="form-label">邮箱</label>
          <input v-model="form.email" class="form-control"/>
        </div>
      </div>
      <button class="btn btn-primary mt-4">保存</button>
    </form>
  </div>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import api from '../../api'
import { showToast } from '../../utils/toast'

const form = reactive({
  studentNo: '', name: '', gender: '', college: '', major: '',
  className: '', grade: '', phone: '', email: ''
})

onMounted(async () => {
  const s = await api.get('/student/profile')
  if (s) Object.assign(form, s)
})

async function save() {
  await api.post('/student/profile/update', form)
  showToast('保存成功')
}
</script>
