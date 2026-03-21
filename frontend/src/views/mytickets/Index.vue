<template>
    <div class="bg-white shadow-md rounded-lg p-6">
        <h1 class="text-2xl font-bold mb-4">我的车票</h1>

        <div v-if="loading" class="text-center py-4 text-gray-500">加载中...</div>
        <div v-else-if="error" class="text-center py-4 text-red-500">{{ error }}</div>
        <div v-else-if="tickets.length === 0" class="text-center py-4 text-gray-500">暂无车票</div>
        <el-table v-else :data="tickets" style="width: 100%" class="border border-gray-200 rounded">
            <el-table-column label="状态" width="100">
                <template #default="{ row }">
                    <span v-if="isWithin3Hours(row.departureTime)" class="text-red-600 font-bold">即将发车</span>
                    <span v-else class="text-green-600">正常</span>
                </template>
            </el-table-column>
            <el-table-column prop="trainName" label="车次" width="120"></el-table-column>
            <el-table-column prop="fromStation" label="出发站" width="120"></el-table-column>
            <el-table-column prop="toStation" label="到达站" width="120"></el-table-column>
            <el-table-column prop="departureTime" label="发车时间" width="120"></el-table-column>
            <el-table-column prop="seatName" label="座位号" width="100"></el-table-column>
            <el-table-column label="操作" width="100">
                <template #default="{ row }">
                    <el-button type="danger" size="mini" @click="handleCancel(row)">退票</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from "@/api/index";
import { showMessage } from "@/main";

interface Ticket {
    id: number;
    trainName: string;
    fromStation: string;
    toStation: string;
    departureTime: string;
    seatName: string;
}

const tickets = ref<Ticket[]>([]);
const loading = ref(false);
const error = ref('');

const isWithin3Hours = (departureTime: string): boolean => {
    if (!departureTime) return false;
    const now = new Date();
    const [h, m] = departureTime.split(':').map(Number);
    const departure = new Date();
    departure.setHours(h, m, 0, 0);
    const diffHours = (departure.getTime() - now.getTime()) / (1000 * 60 * 60);
    return diffHours >= 0 && diffHours <= 3;
};

const fetchTickets = async () => {
    loading.value = true;
    error.value = '';
    try {
        const data = await axios.get('/tickets') as Ticket[];
        tickets.value = data || [];
    } catch (e: any) {
        error.value = '获取车票列表失败，请稍后重试';
        console.log(e);
    } finally {
        loading.value = false;
    }
};

const handleCancel = async (row: Ticket) => {
    try {
        showMessage("退票功能待实现");
    } catch (e: any) {
        console.log(e);
        showMessage("退票失败，请稍后重试");
    }
};

onMounted(() => {
    fetchTickets();
});
</script>
