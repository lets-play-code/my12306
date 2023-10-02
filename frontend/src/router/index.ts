import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/login/Index.vue'
import Home from '../views/home/Index.vue'
import Factory from '../views/demo/Index.vue'
import Product from '../views/product/Index.vue'
import ProductDetail from "../views/product/ProductDetail.vue";
import ProductImporter from "../views/product/ProductImporter.vue";

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home
    },
    {
        path: '/demo/index',
        name: 'Demo',
        component: Factory
    },
    {
        path: '/product/index',
        name: 'Product',
        component: Product
    },
    {
        path: '/product/importer',
        name: 'ProductImporter',
        component: ProductImporter
    },
    {
        path: '/product/detail/:id',
        name: 'ProductDetail',
        component: ProductDetail
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },

    // {
    //     path: '/product',
    //     children: [{
    //         path: '/index',
    //         component: Product,
    //     }, {
    //         path: '/detail/:id',
    //         component: ProductDetail
    //     }]
    // }
    // 其他路由
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
