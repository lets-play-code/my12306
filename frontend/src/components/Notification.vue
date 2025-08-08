<template>
  <transition name="fade">
    <div v-if="visible" class="fixed top-4 left-1/2 transform -translate-x-1/2 bg-red-500 text-white py-2 px-4 rounded-md shadow-md" :class="customClass">
      {{ message }}
    </div>
  </transition>
</template>

<script setup lang="ts">
import { ref, watch, onBeforeUnmount } from 'vue';

const props = defineProps({
  message: {
    type: String,
    required: true,
  },
  duration: {
    type: Number,
    default: 3000,
  },
  customClass: {
    type: String,
    default: '',
  }
});

const visible = ref(false);
let timer: number | undefined;

const show = () => {
  visible.value = true;
  if (timer) window.clearTimeout(timer);
  timer = window.setTimeout(() => {
    visible.value = false;
    timer = undefined;
  }, props.duration);
};

watch(() => props.message, (val) => {
  if (val) show();
});

onBeforeUnmount(() => {
  if (timer) window.clearTimeout(timer);
});
</script>

<style>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>
