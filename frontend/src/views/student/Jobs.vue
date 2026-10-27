<template>
  <div>
    <div class="card p-4 mb-3">
      <div class="d-flex gap-2">
        <input v-model="keyword" class="form-control" style="max-width: 320px"
               placeholder="按岗位名称 / 企业名称 / 城市搜索" @keyup.enter="load"/>
        <button class="btn btn-primary" @click="load">搜索</button>
      </div>
    </div>
    <div class="card p-4">
      <table class="table table-hover align-middle">
        <thead>
        <tr>
          <th>岗位名称</th>
          <th>招聘企业</th>
          <th>薪资</th>
          <th>城市</th>
          <th>岗位描述</th>
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
          <td style="max-width: 260px" class="text-truncate" :title="j.description">{{ j.description }}</td>
          <td>{{ fmt(j.create_time) }}</td>
          <td>
            <button class="btn btn-sm btn-outline-primary" @click="openApply(j)">投递简历</button>
          </td>
        </tr>
        <tr v-if="!list.length">
          <td colspan="7" class="text-center text-secondary py-4">暂无岗位数据</td>
        </tr>
        </tbody>
      </table>
    </div>

    <div v-if="showModal">
      <div class="modal fade show d-block" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">投递简历 - {{ current.title }}</h5>
              <button type="button" class="btn-close" @click="showModal = false"></button>
            </div>
            <div class="modal-body">
              <label class="form-label">自我简介 / 简历要点</label>
              <textarea v-model="summary" class="form-control" rows="5"
                        placeholder="如：熟悉Java、Spring框架，有实习经验，通过CET-6"></textarea>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" @click="showModal = false">取消</button>
              <button class="btn btn-primary" :disabled="loading" @click="apply">确认投递</button>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-backdrop fade show"></div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import api from '../../api'
import { showToast } from '../../utils/toast'

const list = ref([])
const keyword = ref('')
const showModal = ref(false)
const current = ref({})
const summary = ref('')
const loading = ref(false)

function fmt(t) {
  return t ? String(t).replace('T', ' ').slice(0, 16) : ''
}

async function load() {
  list.value = await api.get('/student/jobs/list', { params: { keyword: keyword.value } })
}

function openApply(j) {
  current.value = j
  summary.value = ''
  showModal.value = true
}

async function apply() {
  loading.value = true
  try {
    await api.post('/student/jobs/apply', { jobId: current.value.id, summary: summary.value })
    showModal.value = false
    showToast('投递成功')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>
