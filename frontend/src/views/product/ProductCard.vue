<template>
  <div class="bg-white rounded shadow p-4 hover:shadow-lg transition-shadow duration-300"
       :data-testid="productDetail.id">
    <img :src="wrapHost(imageUrl)" :alt="productDetail.title" class="w-full h-48 object-cover mb-4 rounded">
    <h2 class="text-xl font-semibold mb-2 text-blue-600 truncate">{{ productDetail.title }}</h2>
    <p class="text-gray-500 mb-2 truncate">{{ productDetail.type }}</p>
    <div class="flex justify-between items-center">
      <span class="text-lg font-semibold text-red-500">{{ '$ ' + productDetail.price }}</span>
      <button @click="goToProductDetail(productDetail.id)"
              class="bg-blue-600 text-white px-4 py-1 rounded hover:bg-blue-700">
        Details
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { PropType, ref } from "vue";
import { useRouter } from "vue-router";
import { Product } from "../../model/Product";
import { useHost } from "../../utils/useHost";

const props = defineProps({
  product: {
    type: Object as PropType<Product>,
    required: true,
  },
});

const {wrapHost} = useHost();

const router = useRouter();

const productDetail = ref<Product>({
      id: 1,
      images: ["https://cc-west-usa.oss-accelerate.aliyuncs.com/cc8cd638-517e-4958-b8f1-a9d198a2cca3.jpg?x-oss-process=image/format,webp,image/resize,m_fill,w_179,h_190"],
      title: "Product 1",
      description: "This is a great product.",
      price: "$99.99",
      type: "apple",
      sku: "123456"
    }
);

const imageUrl = ref();
const refresh = () => {
  productDetail.value = props.product!;
  let firstImage = props.product.images[0] as any;
  if (firstImage) {
    imageUrl.value = firstImage.url!;
  }
}

refresh();
const goToProductDetail = (id: number) => {
  router.push(`/product/detail/${id}`);
};
</script>

<style>
@import "../../index.css";
</style>
