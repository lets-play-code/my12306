<script setup lang="ts">
import { inject } from "vue";
import Header from "./components/layout/Header.vue";
import Footer from "./components/layout/Footer.vue";
import Notification from "./components/Notification.vue";

const errorMessage = inject<string>("errorMessage");
</script>

<template>
  <div class="container max-w-full w-full bg-gray-100">
    <Header />
    <Notification :message="errorMessage || ''" :duration="5000" class="bg-red-400" />
    <div class="min-h-[550px] max-w-[1200px] mx-auto mt-4">
      <router-view />
    </div>
    <Footer />
  </div>
</template>
<style>
</style>
