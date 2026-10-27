<template>
  <div class="card p-4">
    <h5 class="mb-4">我的投递</h5>
    <table class="table table-hover align-middle">
      <thead>
      <tr>
        <th>岗位名称</th>
        <th>企业</th>
        <th>薪资</th>
        <th>城市</th>
        <th>状态</th>
        <th>投递时间</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="a in list" :key="a.id">
        <td class="fw-bold">{{ a.title }}</td>
        <td>{{ a.ent_name }}</td>
        <td>{{ a.salary }}</td>
        <td>{{ a.city }}</td>
        <td><span class="badge" :class="BADGE[a.status]">{{ APP_STATUS[a.status] || a.status }}</span></td>
        <td>{{ fmt(a.create_time) }}</td>
        <td>
          <button v-if="a.status === 'SUBMITTED'" class="btn btn-sm btn-outline-danger" @click="cancel(a)">撤回</button>
        </td>
      </tr>
      <tr v-if="!list.length">
        <td colspan="7" class="text-center text-secondary py-4">暂无投递记录</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import api from '../../api'
import { APP_STATUS, BADGE } from '../../utils/dict'
import { showToast } from '../../utils/toast'

const list = ref([])

function fmt(t) {
  return t ? String(t).replace('T', ' ').slice(0, 16) : ''
}

async function load() {
  list.value = await api.get('/student/applications/list')
}

async function cancel(a) {
  if (!confirm('确定撤回该投递吗？')) return
  await api.post('/student/applications/cancel', { id: a.id })
  showToast('已撤回')
  load()
}

onMounted(load)
</script>
