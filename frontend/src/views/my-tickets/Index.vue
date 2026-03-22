<template>
    <div class="bg-white shadow-md rounded-lg p-6">
        <h1 class="text-2xl font-bold mb-4">我的车票</h1>

        <div v-if="loading" class="text-center py-4 text-gray-500">加载中...</div>
        <div v-else-if="error" class="text-center py-4 text-red-500">{{ error }}</div>
        <div v-else-if="tickets.length === 0" class="text-center py-4 text-gray-500">暂无已购车票</div>
        <div v-else class="space-y-4">
            <div
                v-for="ticket in tickets"
                :key="ticket.id"
                class="border border-gray-200 rounded-lg p-4"
            >
                <div class="font-medium">{{ ticket.trainName }} {{ ticket.fromStation }}-{{ ticket.toStation }}</div>
                <div class="text-sm text-gray-500">{{ ticket.departureTime }}</div>
                <div class="text-sm mt-1">{{ ticket.statusText }}</div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import ticketService, { MyTicket } from '@/api/services/ticket'

const tickets = ref<MyTicket[]>([])
const loading = ref(false)
const error = ref('')

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
