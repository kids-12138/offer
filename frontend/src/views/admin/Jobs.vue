<template>
  <div>
    <div class="card p-4 mb-3">
      <div class="d-flex gap-2">
        <input v-model="keyword" class="form-control" style="max-width: 280px"
               placeholder="按岗位 / 企业名称搜索" @keyup.enter="load"/>
        <select v-model="status" class="form-select" style="max-width: 160px" @change="load">
          <option value="">全部状态</option>
          <option value="PENDING">待审核</option>
          <option value="APPROVED">已通过</option>
          <option value="REJECTED">未通过</option>
        </select>
        <button class="btn btn-primary" @click="load">搜索</button>
      </div>
    </div>
    <div class="card p-4">
      <table class="table table-hover align-middle">
        <thead>
        <tr>
          <th>岗位名称</th>
          <th>企业</th>
          <th>薪资</th>
          <th>城市</th>
          <th>描述</th>
          <th>审核状态</th>
          <th>发布时间</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="j in list" :key="j.id">
          <td class="fw-bold">{{ j.title }}</td>
          <td>{{ j.ent_name }}</td>
          <td>{{ j.salary }}</td>
          <td>{{ j.city }}</td>
          <td style="max-width: 220px" class="text-truncate" :title="j.description">{{ j.description }}</td>
          <td><span class="badge" :class="BADGE[j.status]">{{ AUDIT_STATUS[j.status] || j.status }}</span></td>
          <td>{{ fmt(j.create_time) }}</td>
          <td>
            <button v-if="j.status !== 'APPROVED'" class="btn btn-sm btn-success me-1" @click="audit(j, 'APPROVED')">通过</button>
            <button v-if="j.status !== 'REJECTED'" class="btn btn-sm btn-outline-danger" @click="audit(j, 'REJECTED')">驳回</button>
          </td>
        </tr>
        <tr v-if="!list.length">
          <td colspan="8" class="text-center text-secondary py-4">暂无数据</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import api from '../../api'
import { AUDIT_STATUS, BADGE } from '../../utils/dict'
import { showToast } from '../../utils/toast'

const list = ref([])
const keyword = ref('')
const status = ref('')

function fmt(t) {
  return t ? String(t).replace('T', ' ').slice(0, 16) : ''
}

async function load() {
  list.value = await api.get('/admin/jobs', { params: { keyword: keyword.value, status: status.value } })
}

async function audit(j, s) {
  await api.post('/admin/jobs/audit', { id: String(j.id), status: s })
  showToast('操作成功')
  load()
}

onMounted(load)
</script>
