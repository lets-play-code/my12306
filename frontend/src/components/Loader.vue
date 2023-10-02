<template>
  <transition name="fade">
    <div v-if="innerIsLoading" class="fixed inset-0 flex items-center justify-center">
      <div class="ring"></div>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { defineProps, ref, watch } from "vue";

const props = defineProps<{ isLoading: boolean }>();

const innerIsLoading = ref(props.isLoading);

watch(
    () => props.isLoading,
    (newValue) => {
      innerIsLoading.value = newValue;
    },
);
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity .5s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}

.ring {
  width: 64px;
  height: 64px;
  border: 4px solid rgba(59, 130, 246, 0.2);
  border-radius: 50%;
  border-top-color: rgba(59, 130, 246, 1);
  animation: spin 2s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
