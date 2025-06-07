import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {path: '/', redirect: '/hzsca/home'},
        {path: '/hzsca', component: ()=> import('../views/HomeView.vue'),children: [
                {path: 'home', component: ()=> import('../views/Homepage.vue'),meta: {title: '首页'}},
                {path: 'adopt', component: ()=> import('../views/adopt.vue'),meta: {title: '领养'}},
                {path: 'login', component: ()=> import('../views/login.vue'),meta: {title: '登录'}},
                {path: 'regist', component: ()=> import('../views/regist.vue'),meta: {title: '注册'}},
            ]},
    ],
})

router.beforeEach((to, from, next) => {
    document.title = to.meta.title
    next()
})
export default router
