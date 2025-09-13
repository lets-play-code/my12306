<template>
    <div class="bg-white shadow-md rounded-lg p-6">
        <h1 class="text-2xl font-bold mb-4">火车信息列表</h1>
        
        <!-- 查询表单 -->
        <div class="mb-4 flex gap-4 items-end">
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">始发站</label>
                <el-input 
                    v-model="fromStation" 
                    placeholder="请输入始发站" 
                    data-testid="from-station"
                    class="w-32"
                />
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">终点站</label>
                <el-input 
                    v-model="toStation" 
                    placeholder="请输入终点站" 
                    data-testid="to-station"
                    class="w-32"
                />
            </div>
            <el-button type="primary" @click="handleQuery" data-testid="query-button">查询</el-button>
            <el-button @click="handleClear">清空</el-button>
        </div>
        
        <div v-if="loading" class="text-center py-4 text-gray-500">加载中...</div>
        <div v-else-if="error" class="text-center py-4 text-red-500">{{ error }}</div>
        <div v-else-if="trains.length === 0" class="text-center py-4 text-gray-500">没有找到符合条件的车次</div>
        <el-table v-else :data="trains" style="width: 100%" class="border border-gray-200 rounded">
            <el-table-column prop="description" label="车次" width="200" class="font-medium"></el-table-column>
            <el-table-column label="余票" width="120" class="text-center">
                <template #default="{ row }">
                    {{ row.remainingTickets }}张
                </template>
            </el-table-column>
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
const fromStation = ref('');
const toStation = ref('');

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
        const data = await axios.get('/trains') as any;
        trains.value = data || [];
        if (Array.isArray(trains.value)) {
            trains.value.forEach((item: any) => {
                item.description = item.name + ' ' + item.stops.at(0).name + '-' + item.stops.at(-1).name;
            });
        }
    } catch (e: any) {
        error.value = '获取火车列表失败，请稍后重试';
        console.log(e);
    } finally {
        loading.value = false;
    }
};

const handleQuery = async () => {
    loading.value = true;
    error.value = '';
    try {
        const data = await axios.get('/trains', {
            params: {
                from: fromStation.value,
                to: toStation.value
            }
        }) as any;
        trains.value = data || [];
        if (Array.isArray(trains.value)) {
            trains.value.forEach((item: any) => {
                item.description = item.name + ' ' + item.stops.at(0).name + '-' + item.stops.at(-1).name;
            });
        }
    } catch (e: any) {
        error.value = '获取火车列表失败，请稍后重试';
        console.log(e);
    } finally {
        loading.value = false;
    }
};

const handleClear = () => {
    fromStation.value = '';
    toStation.value = '';
    trains.value = [];
};

onMounted(fetchTrains);
</script>