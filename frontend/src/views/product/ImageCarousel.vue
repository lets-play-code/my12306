<!-- ImageCarousel.vue -->
<template>
  <div>
    <div
        v-for="(image, index) in imageList"
        :key="index"
        class="object-cover w-full h-auto"
        :class="{
        hidden: currentImage !== index,
        block: currentImage === index,
      }"
    >
      <img :src="wrapHost(image)" :alt="'Image ' + (index + 1)" class="w-auto h-auto mx-auto"/>
    </div>

    <div class="mt-4 flex justify-center items-center space-x-2">
      <button class="bg-white text-black px-2 py-1" @click="prevImage">
        &lt;
      </button>
      <div class="flex space-x-2">
        <img
            v-for="(image, index) in imageList"
            :key="index"
            :src="wrapHost(image)"
            :alt="'Thumbnail ' + (index + 1)"
            class="w-16 h-16 cursor-pointer border border-gray-300"
            @click="setCurrentImage(index)"
        />
      </div>
      <button class="bg-white text-black px-2 py-1" @click="nextImage">
        &gt;
      </button>
    </div>
  </div>
</template>

<script setup>
import {computed, ref} from 'vue';
import {useHost} from '@/utils/useHost.ts';

const props = defineProps({
  images: {
    type: Array,
    required: true
  }
});


const {wrapHost} = useHost();

const currentImage = ref(0);

const imageList = computed(() => {
  return props.images.map((image) => image.url);
});
const setCurrentImage = (index) => {
  currentImage.value = index;
};

const nextImage = () => {
  currentImage.value = (currentImage.value + 1) % props.images.length;
};

const prevImage = () => {
  currentImage.value = (currentImage.value - 1 + props.images.length) % props.images.length;
};
</script>

<style scoped>
@import "../../index.css";
</style>
