<template>
  <div>
    <div class="row g-3 mb-3">
      <div class="col-md-3" v-for="c in cards" :key="c.label">
        <div class="card p-3 stat-card">
          <div class="text-secondary small">{{ c.label }}</div>
          <h3 :style="{ color: c.color }">{{ c.value }}<span v-if="c.unit" class="fs-6 text-secondary">{{ c.unit }}</span></h3>
        </div>
      </div>
    </div>
    <div class="row g-3">
      <div class="col-md-5">
        <div class="card p-3"><h6 class="mb-3">去向类型分布</h6><div ref="typeChart" style="height: 320px"></div></div>
      </div>
      <div class="col-md-7">
        <div class="card p-3"><h6 class="mb-3">就业城市 TOP10</h6><div ref="cityChart" style="height: 320px"></div></div>
      </div>
      <div class="col-md-6">
        <div class="card p-3"><h6 class="mb-3">就业薪资分布</h6><div ref="salaryChart" style="height: 320px"></div></div>
      </div>
      <div class="col-md-6">
        <div class="card p-3"><h6 class="mb-3">各学院就业情况</h6><div ref="collegeChart" style="height: 320px"></div></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import * as echarts from 'echarts'
import { computed, onMounted, ref } from 'vue'
import api from '../../api'
import { EMP_TYPE } from '../../utils/dict'

const overview = ref({})
const typeChart = ref()
const cityChart = ref()
const salaryChart = ref()
const collegeChart = ref()

const cards = computed(() => [
  { label: '学生总数', value: overview.value.totalStudents ?? 0, color: '#2f6fed' },
  { label: '已登记去向', value: overview.value.registered ?? 0, color: '#0dcaf0' },
  { label: '就业人数', value: overview.value.employed ?? 0, color: '#198754' },
  { label: '就业率', value: overview.value.employmentRate ?? 0, unit: '%', color: '#fd7e14' },
  { label: '升学人数', value: overview.value.furtherStudy ?? 0, color: '#6f42c1' },
  { label: '自主创业', value: overview.value.entrepreneur ?? 0, color: '#d63384' },
  { label: '待业人数', value: overview.value.unemployed ?? 0, color: '#adb5bd' },
  { label: '未登记人数', value: (overview.value.totalStudents ?? 0) - (overview.value.registered ?? 0), color: '#dc3545' }
])

function barOption(names, values, color) {
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: 60, right: 20, bottom: 40, top: 20 },
    xAxis: { type: 'category', data: names, axisLabel: { interval: 0, rotate: names.length > 5 ? 30 : 0 } },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ type: 'bar', data: values, itemStyle: { color }, barMaxWidth: 40 }]
  }
}

onMounted(async () => {
  overview.value = await api.get('/admin/stats/overview')

  const typeData = await api.get('/admin/stats/type')
  echarts.init(typeChart.value).setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie', radius: ['40%', '65%'],
      label: { formatter: '{b}: {c}' },
      data: typeData.map(d => ({ name: EMP_TYPE[d.name] || d.name, value: Number(d.value) }))
    }]
  })

  const cityData = await api.get('/admin/stats/city')
  echarts.init(cityChart.value).setOption(
    barOption(cityData.map(d => d.name), cityData.map(d => Number(d.value)), '#2f6fed'))

  const salaryData = await api.get('/admin/stats/salary')
  echarts.init(salaryChart.value).setOption(
    barOption(salaryData.map(d => d.name), salaryData.map(d => Number(d.value)), '#fd7e14'))

  const collegeData = await api.get('/admin/stats/college')
  echarts.init(collegeChart.value).setOption({
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0 },
    grid: { left: 60, right: 20, bottom: 60, top: 20 },
    xAxis: { type: 'category', data: collegeData.map(d => d.name), axisLabel: { interval: 0 } },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      { name: '登记人数', type: 'bar', data: collegeData.map(d => Number(d.total)), itemStyle: { color: '#0dcaf0' }, barMaxWidth: 32 },
      { name: '就业人数', type: 'bar', data: collegeData.map(d => Number(d.employed)), itemStyle: { color: '#198754' }, barMaxWidth: 32 }
    ]
  })
})
</script>
