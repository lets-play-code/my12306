<template>
  <div class="container mx-auto py-6">
    <h1 class="text-3xl font-bold mb-6">Factory Management</h1>
    <div class="mb-4">
      <button @click="openAddModal" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
        添加工厂
      </button>
    </div>
    <div v-if="factories.length === 0">
      <p class="text-gray-500 text-xl">No factories found.</p>
    </div>
    <table class="table-auto w-full mt-6" v-else>
      <thead>
      <tr>
        <th class="px-4 py-2">ID</th>
        <th class="px-4 py-2">名称</th>
        <th class="px-4 py-2">联系人</th>
        <th class="px-4 py-2">手机号</th>
        <th class="px-4 py-2">固话</th>
        <th class="px-4 py-2">地址</th>
        <th class="px-4 py-2">操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="factory in factories" :key="factory.id" class="bg-white">
        <td class="border px-4 py-2">{{ factory.id }}</td>
        <td class="border px-4 py-2">{{ factory.name }}</td>
        <td class="border px-4 py-2">{{ factory.contactPerson }}</td>
        <td class="border px-4 py-2">{{ factory.phoneNumber }}</td>
        <td class="border px-4 py-2">{{ factory.fixPhoneNumber }}</td>
        <td class="border px-4 py-2">{{ factory.address }}</td>
        <td class="border px-4 py-2">
          <button @click="openEditModal(factory)" :data-testid="factory.id"
                  class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-1 px-2 rounded">编辑
          </button>
          <button @click="deleteFactory(factory)" :data-testid="factory.id"
                  class="bg-red-500 hover:bg-red-700 text-white font-bold py-1 px-2 rounded ml-2">删除
          </button>
        </td>
      </tr>
      </tbody>
    </table>

    <!-- Add/Edit Factory Modal -->
    <div v-if="showModal" class="fixed z-10 inset-0 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen">
        <div class="bg-white w-full max-w-lg p-5 rounded shadow">
          <h2 class="text-2xl font-bold mb-5">{{ isEdit ? '编辑工厂' : '添加工厂' }}</h2>
          <form @submit.prevent="isEdit ? updateFactory() : addFactory()">
            <div class="mb-4">
              <label class="block text-gray-700 text-sm font-bold mb-2" for="factoryName">名称</label>
              <input
                  class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                  id="factoryName" data-testid="name" type="text" v-model="currentFactory.name" required>
            </div>
            <div class="mb-4">
              <label class="block text-gray-700 text-sm font-bold mb-2" for="contactPerson">联系人</label>
              <input
                  class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                  id="contactPerson" data-testid="contactPerson" type="text" v-model="currentFactory.contactPerson"
                  required>
            </div>
            <div class="mb-4">
              <label class="block text-gray-700 text-sm font-bold mb-2" for="phoneNumber">手机号</label>
              <input
                  class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                  id="phoneNumber" data-testid="phoneNumber" type="text" v-model="currentFactory.phoneNumber" required>
            </div>
            <div class="mb-4">
              <label class="block text-gray-700 text-sm font-bold mb-2" for="fixPhoneNumber">固话</label>
              <input
                  class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                  id="fixPhoneNumber" data-testid="fixPhoneNumber" type="text" v-model="currentFactory.fixPhoneNumber">
            </div>
            <div class="mb-4">
              <label class="block text-gray-700 text-sm font-bold mb-2" for="address">地址</label>
              <input
                  class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
                  id="address" data-testid="address" type="text" v-model="currentFactory.address" required>
            </div>
            <div class="flex items-center justify-between">
              <button
                  class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                  data-testid="save-button"
                  type="submit">
                {{ isEdit ? '更新工厂' : '添加工厂' }}
              </button>
              <button @click="closeModal"
                      class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
                      type="button">
                取消
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import { Factory } from '../../model/Factory';
import factoryService from '../../api/services/factoryService';
import { PageResult } from '../../api/services/baseService';

const factories = ref<Factory[]>([]);
const showModal = ref(false);
const isEdit = ref(false);
const currentFactory = ref<Factory>({
  name: '',
  contactPerson: '',
  phoneNumber: '',
  fixPhoneNumber: '',
  address: '',
});

const fetchFactories = async () => {
  const result: PageResult<Factory> = await factoryService.list({});
  factories.value = result.list;
};

const openAddModal = () => {
  isEdit.value = false;
  currentFactory.value = {
    name: '',
    contactPerson: '',
    phoneNumber: '',
    fixPhoneNumber: '',
    address: '',
  };
  showModal.value = true;
};

const openEditModal = (factory: Factory) => {
  isEdit.value = true;
  currentFactory.value = {...factory};
  showModal.value = true;
};

const addFactory = async () => {
  await factoryService.create(currentFactory.value);

  await fetchFactories();
  closeModal();
};

const updateFactory = async () => {
  if (currentFactory.value.id) {
    await factoryService.update(currentFactory.value.id, currentFactory.value);
    await fetchFactories();
    closeModal();
  }
};

const deleteFactory = async (factory: Factory) => {
  if (factory.id) {
    await factoryService.delete(factory.id);
    await fetchFactories();
  }
};

const closeModal = () => {
  showModal.value = false;
};

fetchFactories();
</script>

<style scoped>

</style>
