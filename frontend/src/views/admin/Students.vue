<template>
  <div>
    <div class="card p-4 mb-3">
      <div class="d-flex gap-2">
        <input v-model="keyword" class="form-control" style="max-width: 280px"
               placeholder="按姓名 / 学号 / 学院搜索" @keyup.enter="load"/>
        <button class="btn btn-primary" @click="load">搜索</button>
      </div>
    </div>
    <div class="card p-4">
      <table class="table table-hover align-middle">
        <thead>
        <tr>
          <th>用户名</th>
          <th>姓名</th>
          <th>学号</th>
          <th>性别</th>
          <th>学院</th>
          <th>专业</th>
          <th>班级</th>
          <th>账号状态</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="s in list" :key="s.id">
          <td>{{ s.username }}</td>
          <td class="fw-bold">{{ s.name }}</td>
          <td>{{ s.student_no }}</td>
          <td>{{ s.gender }}</td>
          <td>{{ s.college }}</td>
          <td>{{ s.major }}</td>
          <td>{{ s.class_name }}</td>
          <td>
            <span class="badge" :class="s.user_status === 1 ? 'bg-success' : 'bg-secondary'">
              {{ s.user_status === 1 ? '正常' : '已禁用' }}
            </span>
          </td>
          <td>
            <button class="btn btn-sm me-1" :class="s.user_status === 1 ? 'btn-outline-warning' : 'btn-outline-success'"
                    @click="toggle(s)">{{ s.user_status === 1 ? '禁用' : '启用' }}</button>
            <button class="btn btn-sm btn-outline-danger" @click="remove(s)">删除</button>
          </td>
        </tr>
        <tr v-if="!list.length">
          <td colspan="9" class="text-center text-secondary py-4">暂无数据</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import api from '../../api'
import { showToast } from '../../utils/toast'

const list = ref([])
const keyword = ref('')

async function load() {
  list.value = await api.get('/admin/students', { params: { keyword: keyword.value } })
}

async function toggle(s) {
  const status = s.user_status === 1 ? 0 : 1
  await api.post('/admin/students/status', { userId: s.user_id, status })
  showToast('操作成功')
  load()
}

async function remove(s) {
  if (!confirm(`确定删除学生「${s.name}」吗？该操作不可恢复。`)) return
  await api.post('/admin/students/delete', { userId: s.user_id })
  showToast('已删除')
  load()
}

onMounted(load)
</script>
