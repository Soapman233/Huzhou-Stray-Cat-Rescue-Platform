import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import { ElMessage } from 'element-plus'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {path: '/', redirect: '/hzsca/home'},
        {
            path: '/hzsca',
            component: HomeView,
            children: [
                {
                    path: 'home',
                    component: () => import('../views/Homepage.vue'),
                    meta: {title: '首页'}
                },
                {
                    path: 'adopt',
                    component: () => import('../views/adopt.vue'),
                    meta: {title: '领养'}
                },
                {
                    path: 'adopt/:id',
                    component: () => import('../views/adoptDetail.vue'),
                    meta: {title: '领养详情'}
                },
                {
                    path: 'adopt/apply/:id',
                    component: () => import('../views/Apply.vue'),
                    meta: {title: '申请领养', requiresAuth: true}
                },
                {
                    path: 'personalInfo',
                    component: () => import('../views/PersonalInfo.vue'),
                    meta: {title: '个人信息', requiresAuth: true}
                },
                {
                    path: 'login',
                    component: () => import('../views/login.vue'),
                    meta: {title: '登录'}
                },
                {
                    path: 'regist',
                    component: () => import('../views/regist.vue'),
                    meta: {title: '注册'}
                },
                {
                    path: 'admin/users',
                    component: () => import('../views/admin/UserManagement.vue'),
                    meta: {title: '用户管理', requiresAuth: true, role: 'admin'}
                },
                {
                    path: 'admin/cats',
                    component: () => import('../views/admin/CatManagement.vue'),
                    meta: {title: '猫咪管理', requiresAuth: true, role: 'admin'}
                },
                {
                    path: 'activity',
                    component: () => import('../views/activity.vue'),
                    meta: {title: '活动'}
                },
                {
                    path: 'activity/activityall',
                    component: () => import('../views/activity/activityall.vue'),
                    meta: {title: '所有活动'}
                },
                {
                    path: 'activity/activityback',
                    component: () => import('../views/activity/activityback.vue'),
                    meta: {title: '活动回顾'}
                }
            ]
        },
        { path: '/login', redirect: '/hzsca/login' },
        { path: '/regist', redirect: '/hzsca/regist' },
        { path: '/personalInfo', redirect: '/hzsca/personalInfo' },
        { path: '/admin', redirect: '/hzsca/admin/users' },
        { path: '/404', name: 'NotFound', component: () => import('../views/404.vue'), meta: { title: '404' }},
        { path: '/:pathMatch(.*)*', redirect: '/404' }
    ]
})

router.beforeEach((to, from, next) => {
    document.title = to.meta.title || 'HZSCA'
    if (to.matched.some(record => record.meta.requiresAuth)) {
        const token = localStorage.getItem('token')
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

        if (!token) {
            ElMessage.warning('请先登录')
            next({
                path: '/hzsca/login',
                query: { redirect: to.fullPath }
            })
            return
        }

        if (to.meta.role && userInfo.role !== to.meta.role) {
            ElMessage.error('没有权限访问该页面')
            next('/hzsca/home')
            return
        }
    }
    next()
})

export default router
