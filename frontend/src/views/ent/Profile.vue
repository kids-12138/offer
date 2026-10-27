<template>
  <div class="card p-4" style="max-width: 720px">
    <div class="d-flex align-items-center gap-2 mb-4">
      <h5 class="mb-0">企业信息</h5>
      <span class="badge" :class="BADGE[ent.status]">{{ AUDIT_STATUS[ent.status] || ent.status }}</span>
    </div>
    <div v-if="ent.status === 'PENDING'" class="alert alert-warning py-2 small">
      企业账号正在审核中，审核通过后才能发布岗位。
    </div>
    <form @submit.prevent="save">
      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label">企业名称 *</label>
          <input v-model="form.name" class="form-control" required/>
        </div>
        <div class="col-md-6">
          <label class="form-label">所属行业</label>
          <input v-model="form.industry" class="form-control" placeholder="如 信息技术"/>
        </div>
        <div class="col-12">
          <label class="form-label">地址</label>
          <input v-model="form.address" class="form-control"/>
        </div>
        <div class="col-md-6">
          <label class="form-label">联系人</label>
          <input v-model="form.contact" class="form-control"/>
        </div>
        <div class="col-md-6">
          <label class="form-label">联系电话</label>
          <input v-model="form.phone" class="form-control"/>
        </div>
      </div>
      <button class="btn btn-primary mt-4">保存</button>
    </form>
  </div>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import api from '../../api'
import { AUDIT_STATUS, BADGE } from '../../utils/dict'
import { showToast } from '../../utils/toast'

const ent = reactive({})
const form = reactive({ name: '', industry: '', address: '', contact: '', phone: '' })

onMounted(async () => {
  const e = await api.get('/ent/profile')
  Object.assign(ent, e)
  Object.assign(form, e)
})

async function save() {
  await api.post('/ent/profile/update', form)
  Object.assign(ent, form)
  showToast('保存成功')
}
</script>
