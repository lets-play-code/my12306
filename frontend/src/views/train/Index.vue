<template>
    <div class="bg-white shadow-md rounded-lg p-6">
        <h1 class="text-2xl font-bold mb-4">火车信息列表</h1>
        <div v-if="loading" class="text-center py-4 text-gray-500">加载中...</div>
        <div v-else-if="error" class="text-center py-4 text-red-500">{{ error }}</div>
        <el-table v-else :data="trains" style="width: 100%" class="border border-gray-200 rounded">
            <el-table-column prop="description" label="车次" width="180" class="font-medium"></el-table-column>
            <el-table-column label="操作" width="180">
                <template #default="{ row }">
                    <el-button type="primary" size="mini" @click="handleClick(row)" class="bg-blue-500 hover:bg-blue-600">购票</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue';
import  axios from "@/api/index";
import { showMessage } from "@/main";

const trains = ref([]);
const loading = ref(false);
const error = ref('');

const handleClick = async (row: any) => {
    try {
        const res = await axios.post('/trains/'+row.id+'/tickets', {
          from: row.stops.at(0).id,
          to: row.stops.at(-1).id
        });
        showMessage("购票成功");
    } catch (e: any) {
        console.log(e);
        if (e.response && e.response.data && e.response.data.message) {
            showMessage(e.response.data.message);
        } else {
            showMessage("购票失败，请稍后重试");
        }
    }
};

const fetchTrains = async () => {
    loading.value = true;
    error.value = '';
    try {
        trains.value = await axios.get('/trains');
        trains.value.forEach((item: any) => {
            item.description = item.name + ' ' + item.stops.at(0).name + '-' + item.stops.at(-1).name;
        });
    } catch (e: any) {
        error.value = '获取火车列表失败，请稍后重试';
        console.log(e);
    } finally {
        loading.value = false;
    }
};

onMounted(fetchTrains);
</script>