<template>
  <div class="card p-4">
    <h5 class="mb-4">收到的投递</h5>
    <table class="table table-hover align-middle">
      <thead>
      <tr>
        <th>学生</th>
        <th>学号</th>
        <th>学院 / 专业</th>
        <th>联系电话</th>
        <th>应聘岗位</th>
        <th>自我简介</th>
        <th>状态</th>
        <th>投递时间</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="a in list" :key="a.id">
        <td class="fw-bold">{{ a.student_name }}</td>
        <td>{{ a.student_no }}</td>
        <td>{{ a.college }} / {{ a.major }}</td>
        <td>{{ a.phone }}</td>
        <td>{{ a.job_title }}</td>
        <td style="max-width: 220px" class="text-truncate" :title="a.summary">{{ a.summary }}</td>
        <td><span class="badge" :class="BADGE[a.status]">{{ APP_STATUS[a.status] || a.status }}</span></td>
        <td>{{ fmt(a.create_time) }}</td>
        <td>
          <template v-if="a.status === 'SUBMITTED'">
            <button class="btn btn-sm btn-success me-1" @click="handle(a, 'ACCEPTED')">录取</button>
            <button class="btn btn-sm btn-outline-danger" @click="handle(a, 'REJECTED')">婉拒</button>
          </template>
        </td>
      </tr>
      <tr v-if="!list.length">
        <td colspan="9" class="text-center text-secondary py-4">暂无投递记录</td>
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
  list.value = await api.get('/ent/applications')
}

async function handle(a, status) {
  await api.post('/ent/applications/handle', { id: a.id, status })
  showToast('操作成功')
  load()
}

onMounted(load)
</script>
