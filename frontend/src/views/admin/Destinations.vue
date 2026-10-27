<template>
  <div>
    <div class="card p-4 mb-3">
      <div class="d-flex gap-2">
        <input v-model="keyword" class="form-control" style="max-width: 280px"
               placeholder="按学生姓名 / 学号 / 学院搜索" @keyup.enter="load"/>
        <button class="btn btn-primary" @click="load">搜索</button>
      </div>
    </div>
    <div class="card p-4">
      <table class="table table-hover align-middle">
        <thead>
        <tr>
          <th>学生</th>
          <th>学号</th>
          <th>学院 / 专业</th>
          <th>去向类型</th>
          <th>单位 / 院校</th>
          <th>岗位 / 专业</th>
          <th>薪资</th>
          <th>城市</th>
          <th>备注</th>
          <th>登记时间</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="e in list" :key="e.id">
          <td class="fw-bold">{{ e.student_name }}</td>
          <td>{{ e.student_no }}</td>
          <td>{{ e.college }} / {{ e.major }}</td>
          <td><span class="badge bg-info text-dark">{{ EMP_TYPE[e.type] || e.type }}</span></td>
          <td>{{ e.company }}</td>
          <td>{{ e.position }}</td>
          <td>{{ e.salary ? e.salary + '元/月' : '-' }}</td>
          <td>{{ e.city }}</td>
          <td style="max-width: 180px" class="text-truncate" :title="e.remark">{{ e.remark }}</td>
          <td>{{ fmt(e.update_time) }}</td>
        </tr>
        <tr v-if="!list.length">
          <td colspan="10" class="text-center text-secondary py-4">暂无数据</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import api from '../../api'
import { EMP_TYPE } from '../../utils/dict'

const list = ref([])
const keyword = ref('')

function fmt(t) {
  return t ? String(t).replace('T', ' ').slice(0, 16) : ''
}

async function load() {
  list.value = await api.get('/admin/employments', { params: { keyword: keyword.value } })
}

onMounted(load)
</script>
