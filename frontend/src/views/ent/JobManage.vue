<template>
  <div>
    <div class="card p-4 mb-3">
      <div class="d-flex gap-2">
        <input v-model="keyword" class="form-control" style="max-width: 280px" placeholder="按岗位名称搜索" @keyup.enter="load"/>
        <button class="btn btn-primary" @click="load">搜索</button>
        <button class="btn btn-success ms-auto" @click="openAdd">+ 发布新岗位</button>
      </div>
    </div>
    <div class="card p-4">
      <table class="table table-hover align-middle">
        <thead>
        <tr>
          <th>岗位名称</th>
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
          <td>{{ j.salary }}</td>
          <td>{{ j.city }}</td>
          <td style="max-width: 240px" class="text-truncate" :title="j.description">{{ j.description }}</td>
          <td><span class="badge" :class="BADGE[j.status]">{{ AUDIT_STATUS[j.status] || j.status }}</span></td>
          <td>{{ fmt(j.create_time) }}</td>
          <td>
            <button class="btn btn-sm btn-outline-primary me-1" @click="openEdit(j)">编辑</button>
            <button class="btn btn-sm btn-outline-danger" @click="remove(j)">删除</button>
          </td>
        </tr>
        <tr v-if="!list.length">
          <td colspan="7" class="text-center text-secondary py-4">暂无岗位数据</td>
        </tr>
        </tbody>
      </table>
      <div class="small text-secondary">新发布的岗位需管理员审核通过后，学生端才可见。</div>
    </div>

    <div v-if="showModal">
      <div class="modal fade show d-block" tabindex="-1">
        <div class="modal-dialog">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">{{ form.id ? '编辑岗位' : '发布新岗位' }}</h5>
              <button type="button" class="btn-close" @click="showModal = false"></button>
            </div>
            <div class="modal-body">
              <div class="mb-3">
                <label class="form-label">岗位名称 *</label>
                <input v-model="form.title" class="form-control"/>
              </div>
              <div class="row g-3 mb-3">
                <div class="col-6">
                  <label class="form-label">薪资</label>
                  <input v-model="form.salary" class="form-control" placeholder="如 8000-12000元/月"/>
                </div>
                <div class="col-6">
                  <label class="form-label">城市</label>
                  <input v-model="form.city" class="form-control" placeholder="如 深圳"/>
                </div>
              </div>
              <div>
                <label class="form-label">岗位描述</label>
                <textarea v-model="form.description" class="form-control" rows="4"></textarea>
              </div>
            </div>
            <div class="modal-footer">
              <button class="btn btn-secondary" @click="showModal = false">取消</button>
              <button class="btn btn-primary" @click="save">保存</button>
            </div>
          </div>
        </div>
      </div>
      <div class="modal-backdrop fade show"></div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import api from '../../api'
import { AUDIT_STATUS, BADGE } from '../../utils/dict'
import { showToast } from '../../utils/toast'

const list = ref([])
const keyword = ref('')
const showModal = ref(false)
const form = reactive({ id: null, title: '', salary: '', city: '', description: '' })

function fmt(t) {
  return t ? String(t).replace('T', ' ').slice(0, 16) : ''
}

async function load() {
  list.value = await api.get('/ent/jobs', { params: { keyword: keyword.value } })
}

function openAdd() {
  Object.assign(form, { id: null, title: '', salary: '', city: '', description: '' })
  showModal.value = true
}

function openEdit(j) {
  Object.assign(form, { id: j.id, title: j.title, salary: j.salary, city: j.city, description: j.description })
  showModal.value = true
}

async function save() {
  if (!form.title.trim()) {
    showToast('岗位名称不能为空', 'error')
    return
  }
  await api.post('/ent/jobs/save', form)
  showModal.value = false
  showToast('保存成功')
  load()
}

async function remove(j) {
  if (!confirm(`确定删除岗位「${j.title}」吗？`)) return
  await api.post('/ent/jobs/delete', { id: j.id })
  showToast('已删除')
  load()
}

onMounted(load)
</script>
