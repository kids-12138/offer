<template>
  <div class="card p-4" style="max-width: 720px">
    <h5 class="mb-4">毕业去向登记</h5>
    <div v-if="saved" class="alert alert-info py-2 small">
      当前登记：{{ EMP_TYPE[form.type] || '-' }}
      <span v-if="form.company"> | {{ form.company }}</span>
      <span v-if="form.position"> · {{ form.position }}</span>
      <span v-if="form.city"> · {{ form.city }}</span>
    </div>
    <form @submit.prevent="save">
      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label">去向类型 *</label>
          <select v-model="form.type" class="form-select" required>
            <option value="">请选择</option>
            <option value="EMPLOYED">就业</option>
            <option value="FURTHER_STUDY">升学</option>
            <option value="ENTREPRENEUR">自主创业</option>
            <option value="UNEMPLOYED">待业</option>
          </select>
        </div>
        <div class="col-md-6" v-if="form.type === 'EMPLOYED' || form.type === 'ENTREPRENEUR'">
          <label class="form-label">薪资（元/月）</label>
          <input v-model.number="form.salary" type="number" min="0" class="form-control"/>
        </div>
        <div class="col-md-6" v-if="form.type !== 'UNEMPLOYED'">
          <label class="form-label">{{ form.type === 'FURTHER_STUDY' ? '录取院校' : '单位名称' }}</label>
          <input v-model="form.company" class="form-control"/>
        </div>
        <div class="col-md-6" v-if="form.type !== 'UNEMPLOYED'">
          <label class="form-label">{{ form.type === 'FURTHER_STUDY' ? '录取专业' : '岗位 / 身份' }}</label>
          <input v-model="form.position" class="form-control"/>
        </div>
        <div class="col-md-6" v-if="form.type === 'EMPLOYED' || form.type === 'ENTREPRENEUR'">
          <label class="form-label">就业城市</label>
          <input v-model="form.city" class="form-control" placeholder="如 深圳"/>
        </div>
        <div class="col-12">
          <label class="form-label">备注</label>
          <textarea v-model="form.remark" class="form-control" rows="3"></textarea>
        </div>
      </div>
      <button class="btn btn-primary mt-4">保存登记</button>
    </form>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import api from '../../api'
import { EMP_TYPE } from '../../utils/dict'
import { showToast } from '../../utils/toast'

const form = reactive({
  type: '', company: '', position: '', salary: null, city: '', remark: ''
})
const saved = ref(false)

onMounted(async () => {
  const e = await api.get('/student/employment')
  if (e) {
    Object.assign(form, e)
    saved.value = true
  }
})

async function save() {
  await api.post('/student/employment/save', form)
  saved.value = true
  showToast('登记成功')
}
</script>
