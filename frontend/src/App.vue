<template>
  <div>
    <router-view/>
    <div v-if="toast.show" class="toast-container position-fixed top-0 end-0 p-3" style="z-index:2000">
      <div class="toast show align-items-center border-0" :class="toast.type === 'error' ? 'bg-danger' : 'bg-success'" style="color:#fff">
        <div class="d-flex">
          <div class="toast-body">{{ toast.msg }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { toast } from './utils/toast'
</script>
