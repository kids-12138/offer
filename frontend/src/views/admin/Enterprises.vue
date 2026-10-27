<template>
  <div>
    <div class="card p-4 mb-3">
      <div class="d-flex gap-2">
        <input v-model="keyword" class="form-control" style="max-width: 280px"
               placeholder="按企业名称 / 行业搜索" @keyup.enter="load"/>
        <button class="btn btn-primary" @click="load">搜索</button>
      </div>
    </div>
    <div class="card p-4">
      <table class="table table-hover align-middle">
        <thead>
        <tr>
          <th>用户名</th>
          <th>企业名称</th>
          <th>行业</th>
          <th>地址</th>
          <th>联系人</th>
          <th>联系电话</th>
          <th>审核状态</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="e in list" :key="e.id">
          <td>{{ e.username }}</td>
          <td class="fw-bold">{{ e.name }}</td>
          <td>{{ e.industry }}</td>
          <td style="max-width: 220px" class="text-truncate" :title="e.address">{{ e.address }}</td>
          <td>{{ e.contact }}</td>
          <td>{{ e.phone }}</td>
          <td><span class="badge" :class="BADGE[e.status]">{{ AUDIT_STATUS[e.status] || e.status }}</span></td>
          <td>
            <button v-if="e.status !== 'APPROVED'" class="btn btn-sm btn-success me-1" @click="audit(e, 'APPROVED')">通过</button>
            <button v-if="e.status !== 'REJECTED'" class="btn btn-sm btn-outline-danger" @click="audit(e, 'REJECTED')">驳回</button>
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

async function load() {
  list.value = await api.get('/admin/enterprises', { params: { keyword: keyword.value } })
}

async function audit(e, status) {
  await api.post('/admin/enterprises/audit', { id: String(e.id), status })
  showToast('操作成功')
  load()
}

onMounted(load)
</script>
