<template>
  <div className="min-h-screen text-gray-800 mx-auto py-4">
    <div class="container mx-auto">
      <div class="flex flex-wrap mb-8 mt-4">
        <!-- Product Images -->
        <div class="md:w-1/2 px-2 md:pr-4">
          <div class="w-full">
            <image-carousel :images="product?.images" />
          </div>
        </div>
        <!-- Product Info -->
        <div class="w-full md:w-1/2 px-2 md:pl-4">
          <h2 class="text-3xl font-semibold mb-3">{{ product?.title }}</h2>
          <p class="text-lg  font-semibold text-red-500 mb-3">{{ "$ " + product?.price }}</p>
          <p class="text-sm font-light text-gray-600 mb-1">SKU: {{ product?.sku }}</p>
          <p class="text-sm font-light text-gray-600 mb-5">Type: {{ product?.type }}</p>
          <button class="bg-blue-500 text-white px-6 py-2 mt-4">
            Add to Cart
          </button>
        </div>
      </div>

      <!-- Product Details -->
      <div class="border-t border-gray-200 mb-6">
        <h3 class="text-xl font-semibold my-4 text-center">Product Detail</h3>
        <div v-html="product?.description" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import ImageCarousel from "./ImageCarousel.vue";
import { useRoute } from "vue-router";
import productService from "../../api/services/productService";
import { Product } from "../../model/Product";

const product = ref<Product>();

const route = useRoute();
const productId = route.params.id as string;

const getDetail = async () => {
  product.value = await productService.detail<Product>(productId);
};

getDetail();
</script>

<style scoped>
@import "../../index.css";
</style>
