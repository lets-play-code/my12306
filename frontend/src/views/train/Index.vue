<template>
    <div class="bg-white shadow-md rounded-lg p-6">
        <!-- 临近车次警告横幅 -->
        <el-alert
            v-if="showUpcomingAlert && upcomingTickets.length > 0"
            type="error"
            title="⚠️ 温馨提示"
            :closable="true"
            @close="handleCloseAlert"
            class="mb-4"
        >
            <template #default>
                <div class="text-sm">
                    <p class="mb-2 font-medium">您购买的车次即将发车，请注意出行时间：</p>
                    <ul class="list-disc list-inside space-y-1">
                        <li v-for="ticket in upcomingTickets" :key="ticket.id" class="text-red-600">
                            <span class="font-bold">{{ ticket.trainName }}</span> |
                            {{ ticket.travelDate }} |
                            {{ formatRemainingTime(ticket.remainingMinutes) }}后发车 |
                            {{ ticket.fromStation }} → {{ ticket.toStation }}
                        </li>
                    </ul>
                </div>
            </template>
        </el-alert>

        <h1 class="text-2xl font-bold mb-4">火车信息列表</h1>
        
        <!-- 查询表单 -->
        <div class="mb-4 flex gap-4 items-end">
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">出发日期</label>
                <el-date-picker
                    v-model="travelDate"
                    type="date"
                    placeholder="选择日期"
                    :disabled-date="disabledDate"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    data-testid="travel-date"
                    class="w-36"
                />
            </div>
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
import axios from "@/api/index";
import { showMessage } from "@/main";
import ticketsService, { UpcomingTicketResponse } from "@/api/services/tickets";
import authentication from "@/services/authenticationService";

// 新增状态
const showUpcomingAlert = ref(true);
const upcomingTickets = ref<UpcomingTicketResponse[]>([]);
const travelDate = ref<string>(new Date().toISOString().split('T')[0]);

// 新增方法
const handleCloseAlert = () => {
    showUpcomingAlert.value = false;
};

const formatRemainingTime = (minutes: number): string => {
    if (minutes >= 60) {
        const hours = Math.floor(minutes / 60);
        const mins = minutes % 60;
        return `${hours}小时${mins > 0 ? mins + '分钟' : ''}`;
    }
    return `${minutes}分钟`;
};

const disabledDate = (time: Date) => {
    return time.getTime() < Date.now() - 8.64e7; // 禁用今天之前的日期
};

// 在 onMounted 中获取临近提醒
onMounted(async () => {
    if (authentication.isLoggedIn()) {
        try {
            const tickets = await ticketsService.getUpcomingTickets();
            upcomingTickets.value = tickets || [];
        } catch (e) {
            console.log('获取临近车次失败:', e);
        }
    }
});

const trains = ref([]);
const loading = ref(false);
const error = ref('');
const fromStation = ref('');
const toStation = ref('');

const handleClick = async (row: any) => {
    try {
        // 如果用户指定了始发站和终点站，使用用户查询的站点；否则使用全程
        let fromStopId, toStopId;
        
        if (fromStation.value && toStation.value) {
            // 查找用户指定站点对应的 stop ID
            const fromStop = row.stops.find((stop: any) => stop.name === fromStation.value);
            const toStop = row.stops.find((stop: any) => stop.name === toStation.value);
            
            if (fromStop && toStop) {
                fromStopId = fromStop.id;
                toStopId = toStop.id;
            } else {
                // 如果找不到对应站点，回退到全程
                fromStopId = row.stops.at(0).id;
                toStopId = row.stops.at(-1).id;
            }
        } else {
            // 没有指定站点，购买全程票
            fromStopId = row.stops.at(0).id;
            toStopId = row.stops.at(-1).id;
        }
        
        // 添加 travelDate 参数
        const res = await axios.post('/trains/'+row.id+'/tickets', {
            from: fromStopId,
            to: toStopId,
            travelDate: travelDate.value
        });
        showMessage("购票成功");
        // 购票成功后刷新列表
        await handleQuery();
        
        // 购票成功后刷新临近提醒
        if (authentication.isLoggedIn()) {
            const tickets = await ticketsService.getUpcomingTickets();
            upcomingTickets.value = tickets || [];
            showUpcomingAlert.value = true;
        }
    } catch (e: any) {
        console.log(e);
        if (e.response && e.response.data && e.response.data.message) {
            showMessage(e.response.data.message);
        } else {
            showMessage("购票失败，请稍后重试");
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
                to: toStation.value
            }
        }) as any;
        trains.value = data || [];
        if (Array.isArray(trains.value)) {
            trains.value.forEach((item: any) => {
                // If user specified from/to stations, display the queried route
                if (fromStation.value && toStation.value) {
                    // Find the stops matching the queried stations
                    const fromStop = item.stops.find((stop: any) => stop.name === fromStation.value);
                    const toStop = item.stops.find((stop: any) => stop.name === toStation.value);
                    if (fromStop && toStop) {
                        item.description = item.name + ' ' + fromStop.name + '-' + toStop.name;
                    } else {
                        item.description = item.name + ' ' + item.stops.at(0).name + '-' + item.stops.at(-1).name;
                    }
                } else {
                    // No specific query, display full route
                    item.description = item.name + ' ' + item.stops.at(0).name + '-' + item.stops.at(-1).name;
                }
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
</script>
