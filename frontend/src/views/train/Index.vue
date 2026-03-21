<template>
    <div class="bg-white shadow-md rounded-lg p-6">
        <h1 class="text-2xl font-bold mb-4">火车信息列表</h1>

        <div
            v-if="upcomingPurchasedTickets.length"
            class="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-red-700"
            data-testid="departure-warning"
            role="alert"
        >
            <div class="font-semibold">发车提醒：以下已购车次将在 3 小时内发车</div>
            <ul class="mt-2 list-disc pl-5">
                <li v-for="ticket in upcomingPurchasedTickets" :key="`${ticket.trainId}-${ticket.description}-${ticket.departureTime}`">
                    {{ ticket.description }} 将于 {{ formatDepartureTime(ticket.departureTime) }} 发车，请尽快出发。
                </li>
            </ul>
        </div>

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
            <el-table-column label="发车时间" min-width="180">
                <template #default="{ row }">
                    {{ formatDepartureTime(row.departureTime) }}
                </template>
            </el-table-column>
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
import { onMounted, ref } from 'vue';
import axios from '@/api/index';
import { showMessage } from '@/main';
import {
    formatDepartureTime,
    getUpcomingPurchasedTickets,
    savePurchasedTicket,
    type PurchasedTicketReminder,
} from './purchaseReminder';

interface StopItem {
    id: number;
    name: string;
}

interface TrainItem {
    id: number;
    name: string;
    departureTime?: string;
    description: string;
    stops: StopItem[];
    remainingTickets: number;
}

const trains = ref<TrainItem[]>([]);
const loading = ref(false);
const error = ref('');
const fromStation = ref('');
const toStation = ref('');
const upcomingPurchasedTickets = ref<PurchasedTicketReminder[]>([]);

const refreshUpcomingPurchasedTickets = () => {
    upcomingPurchasedTickets.value = getUpcomingPurchasedTickets();
};

const buildDescription = (item: TrainItem) => {
    if (fromStation.value && toStation.value) {
        const fromStop = item.stops.find((stop) => stop.name === fromStation.value);
        const toStop = item.stops.find((stop) => stop.name === toStation.value);
        if (fromStop && toStop) {
            return `${item.name} ${fromStop.name}-${toStop.name}`;
        }
    }

    return `${item.name} ${item.stops.at(0)?.name ?? ''}-${item.stops.at(-1)?.name ?? ''}`;
};

const getTicketRange = (row: TrainItem) => {
    if (fromStation.value && toStation.value) {
        const fromStop = row.stops.find((stop) => stop.name === fromStation.value);
        const toStop = row.stops.find((stop) => stop.name === toStation.value);
        if (fromStop && toStop) {
            return { fromStopId: fromStop.id, toStopId: toStop.id };
        }
    }

    return {
        fromStopId: row.stops.at(0)?.id,
        toStopId: row.stops.at(-1)?.id,
    };
};

const handleClick = async (row: TrainItem) => {
    try {
        const { fromStopId, toStopId } = getTicketRange(row);
        if (!fromStopId || !toStopId) {
            showMessage('未找到有效的乘车区间');
            return;
        }

        await axios.post(`/trains/${row.id}/tickets`, {
            from: fromStopId,
            to: toStopId,
        });
        savePurchasedTicket({
            trainId: row.id,
            trainName: row.name,
            description: row.description,
            departureTime: row.departureTime,
        });
        refreshUpcomingPurchasedTickets();
        showMessage('购票成功');
        await handleQuery();
    } catch (e: any) {
        console.log(e);
        if (e.response && e.response.data && e.response.data.message) {
            showMessage(e.response.data.message);
        } else {
            showMessage('购票失败，请稍后重试');
        }
    }
};

const handleQuery = async () => {
    loading.value = true;
    error.value = '';
    try {
        const data = await axios.get('/trains', {
            params: {
                from: fromStation.value,
                to: toStation.value,
            },
        }) as TrainItem[];
        trains.value = Array.isArray(data)
            ? data.map((item) => ({
                ...item,
                description: buildDescription(item),
            }))
            : [];
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

onMounted(() => {
    refreshUpcomingPurchasedTickets();
});
</script>
