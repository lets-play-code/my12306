<template>
    <div class="bg-white shadow-md rounded-lg p-6">
        <h1 class="text-2xl font-bold mb-4">我的车票</h1>

        <div v-if="loading" class="text-center py-4 text-gray-500">加载中...</div>
        <div v-else-if="error" class="text-center py-4 text-red-500">{{ error }}</div>
        <template v-else>
            <div
                v-if="upcomingSoonTickets.length > 0"
                data-testid="upcoming-soon-alert"
                class="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-red-700"
            >
                你有 {{ upcomingSoonTickets.length }} 张车票将在 3 小时内发车，请留意出行时间
            </div>

            <div v-if="tickets.length === 0" class="text-center py-4 text-gray-500">暂无已购车票</div>
            <div v-else class="space-y-4">
                <div
                    v-for="ticket in tickets"
                    :key="ticket.id"
                    :data-testid="`ticket-row-${ticket.trainName}`"
                    :class="rowClass(ticket)"
                    class="rounded-lg border p-4"
                >
                    <div class="font-medium">{{ ticket.trainName }} {{ ticket.fromStation }}-{{ ticket.toStation }}</div>
                    <div class="mt-1 text-sm">{{ formatDeparture(ticket.departureTime) }}</div>
                    <div class="mt-1 text-sm">{{ ticket.statusText }}</div>
                </div>
            </div>
        </template>
    </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import ticketService, { MyTicket } from '@/api/services/ticket'

const tickets = ref<MyTicket[]>([])
const loading = ref(false)
const error = ref('')

const upcomingSoonTickets = computed(() => tickets.value.filter(ticket => ticket.status === 'UPCOMING_SOON'))

const rowClass = (ticket: MyTicket) => {
    if (ticket.status === 'UPCOMING_SOON') {
        return 'border-red-200 bg-red-50 text-red-700'
    }
    if (ticket.status === 'DEPARTED') {
        return 'border-gray-200 bg-gray-50 text-gray-500'
    }
    return 'border-gray-200 bg-white text-gray-900'
}

const formatDeparture = (departureTime: string) => departureTime.replace('T', ' ')

const loadTickets = async () => {
    loading.value = true
    error.value = ''
    try {
        tickets.value = await ticketService.myTickets()
    } catch (e) {
        error.value = '获取我的车票失败，请稍后重试'
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    loadTickets()
})
</script>
