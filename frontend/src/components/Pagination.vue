<template>
  <nav class="flex justify-center items-center my-4">
    <button
      @click="prevPage"
      :disabled="pageNum === 1 || totalPages <= 1"
      :class="[
        pageNum === 1 || totalPages <= 1 ? 'opacity-50 cursor-not-allowed' : '',
        'border border-gray-500 text-gray-500 rounded-md px-4 py-2 m-2 transition duration-500 ease select-none hover:text-white hover:bg-gray-500 focus:outline-none focus:shadow-outline',
      ]"
    >
      Previous
    </button>
    <div v-if="totalPages > 1" class="flex">
      <button
        v-for="page in paginationNumbers"
        :key="page"
        @click="goToPage(page)"
        :disabled="pageNum === page"
        :class="[
          pageNum === page ? 'bg-gray-500 text-white' : 'text-gray-500',
          'border border-gray-500 rounded-md px-3 py-2 m-1 transition duration-500 ease select-none hover:text-white hover:bg-gray-500 focus:outline-none focus:shadow-outline',
        ]"
      >
        {{ page === -1 ? "..." : page }}
      </button>
    </div>
    <button
      @click="nextPage"
      :disabled="pageNum === totalPages || totalPages <= 1"
      :class="[
        pageNum === totalPages || totalPages <= 1 ? 'opacity-50 cursor-not-allowed' : '',
        'border border-gray-500 text-gray-500 rounded-md px-4 py-2 m-2 transition duration-500 ease select-none hover:text-white hover:bg-gray-500 focus:outline-none focus:shadow-outline',
      ]"
    >
      Next
    </button>
    <div class="flex items-center space-x-2">
      <span class="text-gray-500">Page {{ pageNum }} of {{ totalPages }}</span>
      <input data-testid="jump-page-input"
        v-model="jumpPageInput"
        @keydown.enter="jumpToPage"
        type="number"
        class="border border-gray-500 text-gray-500 rounded-md px-2 py-2 text-center"
        :max="totalPages"
        :min="1"
      />
      <button
        @click="jumpToPage"
        class="border border-gray-500 text-gray-500 rounded-md px-4 py-2 transition duration-500 ease select-none hover:text-white hover:bg-gray-500 focus:outline-none focus:shadow-outline"
      >
        Go
      </button>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { computed, ref, defineProps, defineEmits } from "vue";

const props = defineProps({
  pageNum: {
    type: Number,
    required: true
  },
  total: {
    type: Number,
    required: true
  },
  pageSize: {
    type: Number,
    default: 12
  }
});

const emit = defineEmits(["updatePage"]);

const totalPages = computed(() => Math.ceil(props.total / props.pageSize));

const prevPage = () => {
  if (props.pageNum > 1) {
    emit("updatePage", props.pageNum - 1);
  }
};

const nextPage = () => {
  if (props.pageNum < totalPages.value) {
    emit("updatePage", props.pageNum + 1);
  }
};

const goToPage = (page: number) => {
  if (page !== -1 && page !== props.pageNum) {
    emit("updatePage", page);
  }
};

const paginationNumbers = computed(() => {
  const visiblePages = 5;
  const pages = [];

  if (totalPages.value <= visiblePages) {
    for (let i = 1; i <= totalPages.value; i++) {
      pages.push(i);
    }
  } else {
    const halfVisiblePages = Math.floor(visiblePages / 2);
    if (props.pageNum - halfVisiblePages <= 1) {
      for (let i = 1; i <= visiblePages; i++) {
        pages.push(i);
      }
      pages.push(-1);
      pages.push(totalPages.value);
    } else if (props.pageNum + halfVisiblePages >= totalPages.value) {
      pages.push(1);
      pages.push(-1);
      for (let i = totalPages.value - visiblePages + 1; i <= totalPages.value; i++) {
        pages.push(i);
      }
    } else {
      pages.push(1);
      pages.push(-1);
      for (let i = props.pageNum - halfVisiblePages + 1; i <= props.pageNum + halfVisiblePages - 1; i++) {
        pages.push(i);
      }
      pages.push(-1);
      pages.push(totalPages.value);
    }
  }

  return pages;
});

const jumpPageInput = ref("");
const jumpToPage = () => {
  const pageNumber = parseInt(jumpPageInput.value);
  if (pageNumber >= 1 && pageNumber <= totalPages.value && pageNumber !== props.pageNum) {
    emit("updatePage", pageNumber);
  }
  jumpPageInput.value = "";
};
</script>
