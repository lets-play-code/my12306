<template>
  <div className="container relative">
    <div v-if="products.length > 0" className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
      <ProductCard v-for="product in products" :key="product.id" :product="product"/>
    </div>
    <div v-else>
      <div class="flex flex-col items-center justify-center">
        <img src="@/assets/empty-box.png" alt="No products available" class="h-64 mb-8"/>
        <h3 class="text-2xl font-medium text-gray-900 mb-4">Sorry, no products available</h3>
        <p class="text-lg text-gray-700 text-center">We're sorry, but there are currently no products available.
          Please check back later.</p>
      </div>
    </div>
    <Loader :isLoading="isLoading"/>
  </div>
  <div class="mt-6">
    <Pagination
        v-if="pagination.total > 0"
        :page-num="pagination.pageNum"
        :total="pagination.total"
        :page-size="pagination.pageSize"
        @update-page="updatePage"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

import { showMessage } from "@/main";
import ProductCard from "./ProductCard.vue";
import {Product} from "../../model/Product";
import {PageResult} from "../../api/services/baseService";
import productService from "../../api/services/productService";
import Pagination from "../../components/Pagination.vue";
import Loader from "../../components/Loader.vue";

const errorMessage = ref('')

const isLoading = ref<boolean>(true);

const products = ref<Product[]>([]);

const pagination = ref({
  pageNum: 1,
  pageSize: 12,
  total: 0,
});


const fetch = async () => {
  try {
    isLoading.value = true;
    const params = Object.assign({}, {
      pageNum: pagination.value.pageNum,
      pageSize: pagination.value.pageSize
    })
    const result: PageResult<Product> = await productService.list(params);
    products.value = result.list;
    pagination.value.total = result.total;
  } catch (e) {
    showMessage((e as Error).message);
  }
  isLoading.value = false;
};

const updatePage = (pageNumber: number) => {
  window.scrollTo(0, 0);
  pagination.value.pageNum = pageNumber;
  fetch();
};

fetch()

</script>

<style>
@import "../../index.css";
</style>
