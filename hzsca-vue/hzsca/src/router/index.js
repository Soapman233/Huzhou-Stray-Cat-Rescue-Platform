import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {path: '/', redirect: '/hzsca/home'},
        {path: '/hzsca', component: ()=> import('../views/HomeView.vue'),children: [
                {path: 'home', component: ()=> import('../views/Homepage.vue'),meta: {title: '首页'}},
                {path: 'adopt', component: ()=> import('../views/adopt.vue'),meta: {title: '领养'}},
                {path: 'adopt/:id', component: ()=> import('../views/adoptDetail.vue'),meta: {title: '领养详情'}},
                {path: 'adopt/apply/:id', component: ()=> import('../views/Apply.vue'),meta: {title: '申请领养'}},
                {path: 'login', component: ()=> import('../views/login.vue'),meta: {title: '登录'}},
                {path: 'regist', component: ()=> import('../views/regist.vue'),meta: {title: '注册'}},
            ]},
        { path: '/login',  meta: { title: '登录系统' },  component: () => import('../views/login.vue') },
        { path: '/regist', meta: { title: '欢迎注册' },  component: () => import('../views/regist.vue') },
        { path: '/404', name: 'NotFound', meta: { title: '404找不到页面' },  component: () => import('../views/404.vue') },
        { path: '/:pathMatch(.*)', redirect: '/404' }
    ],
})

router.beforeEach((to, from, next) => {
    document.title = to.meta.title
    next()
})
export default router
