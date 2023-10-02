<template>
  <div class="bg-white shadow-md rounded p-6 mb-4 flex flex-col items-center relative">
    <h1 class="text-2xl font-bold mb-4">Product Information Import</h1>
    <p class="text-gray-600 mb-4 text-center">
      Add product information in bulk by importing an Excel file. After importing the data, you can view and manage the
      products on the product list page.
    </p>
    <div class="flex items-center">
      <label class="bg-blue-500 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded cursor-pointer">
        Choose File
        <input type="file" accept=".xlsx,.xls" @change="handleFileUpload" class="hidden" />
      </label>
      <button :disabled="!selectedFile" @click="uploadFile"
              class="ml-4 bg-green-500 hover:bg-green-700 text-white font-medium py-2 px-4 rounded focus:outline-none focus:shadow-outline disabled:opacity-50 disabled:cursor-not-allowed">
        Upload File
      </button>
    </div>
    <div v-if="selectedFile" class="mt-2 text-gray-600">
      Selected file: <span class="font-semibold">{{ selectedFile.name }}</span>
    </div>
    <!-- Success Modal -->
    <div v-if="showSuccessModal" class="fixed inset-0 z-10">
      <div class="fixed inset-0 bg-gray-900 opacity-50"></div>
      <div
          class="bg-white w-80 p-6 shadow-md rounded absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
        <h2 class="text-xl font-bold mb-4">File Upload Successful</h2>
        <p class="text-gray-600 mb-4">
          Your file has been successfully uploaded. Please view and manage the newly imported products on the product
          list page.
        </p>
        <button @click="showSuccessModal = false"
                class="bg-blue-500 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded focus:outline-none focus:shadow-outline">
          OK
        </button>
      </div>
    </div>
    <!-- Error Modal -->
    <div v-if="showErrorModal" class="fixed inset-0 z-10">
      <div class="fixed inset-0 bg-gray-900 opacity-50"></div>
      <div
          class="bg-white w-80 p-6 shadow-md rounded absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
        <h2 class="text-xl font-bold text-red-600 mb-4">File Upload Failed</h2>
        <p class="text-gray-600 mb-4">
          An error occurred during the file upload process. Please check if your file format is correct or try again
          later.
        </p>
        <button @click="showErrorModal = false"
                class="bg-blue-500 hover:bg-blue-700 text-white font-medium py-2 px-4 rounded focus:outline-none focus:shadow-outline">
          OK
        </button>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import excelImporterService from "../../api/services/ProductImporterService";
import axios from "axios";

const selectedFile = ref<File | null>(null);
const showSuccessModal = ref(false);
const showErrorModal = ref(false);

const handleFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  selectedFile.value = target.files ? target.files[0] : null;
};

const uploadFile = async () => {
  if (!selectedFile.value) {
    return;
  }
  const formData = new FormData();
  formData.append('file', selectedFile.value);
  try {
    await excelImporterService.importExcel(formData);
    showSuccessModal.value = true;
  } catch (error) {
    showErrorModal.value = true;
    console.error('上传文件失败', error);
  }

};
</script>

<style scoped></style>
