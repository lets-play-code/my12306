import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/login/Index.vue'
import Train from '../views/train/Index.vue'

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
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
