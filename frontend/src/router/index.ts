import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/login/Index.vue'
import Train from '../views/train/Index.vue'
import MyTickets from '../views/my-tickets/Index.vue'

const routes = [
    {
        path: '/',
        name: 'Train',
        component: Train
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/my-tickets',
        name: 'MyTickets',
        component: MyTickets
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
