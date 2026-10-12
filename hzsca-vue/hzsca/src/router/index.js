import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import { ElMessage } from 'element-plus'
import AdoptionManagement from '@/views/admin/AdoptionManagement.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {path: '/', redirect: '/hzsca/home'},
        {
            path: '/hzsca/admin',
            component: () => import('../views/admin/AdminLayout.vue'),
            meta: { requiresAuth: true, role: 'admin' },
            children: [
                { path: '', redirect: '/hzsca/admin/dashboard' },
                { path: 'dashboard', component: () => import('../views/admin/AdminDashboard.vue'), meta: { title: '仪表盘', requiresAuth: true, role: 'admin' } },
                { path: 'users', component: () => import('../views/admin/UserManagement.vue'), meta: { title: '用户管理', requiresAuth: true, role: 'admin' } },
                { path: 'cats', component: () => import('../views/admin/CatManagement.vue'), meta: { title: '猫咪管理', requiresAuth: true, role: 'admin' } },
                { path: 'cat-traits', component: () => import('../views/admin/CatTraitsManagement.vue'), meta: { title: '猫咪特征管理', requiresAuth: true, role: 'admin' } },
                { path: 'activities', component: () => import('../views/admin/ActivityManagement.vue'), meta: { title: '活动管理', requiresAuth: true, role: 'admin' } },
                { path: 'volunteers', component: () => import('../views/admin/VolunteerReview.vue'), meta: { title: '志愿者审核', requiresAuth: true, role: 'admin' } },
                { path: 'adoptions', name: 'adoptions', component: AdoptionManagement, meta: { title: '领养申请管理', requiresAuth: true, role: 'admin' } },
                { path: 'rescues', component: () => import('../views/admin/RescueManagement.vue'), meta: { title: '救助上报管理', requiresAuth: true, role: 'admin' } },
                { path: 'dataset', component: () => import('../views/admin/DatasetExport.vue'), meta: { title: '数据导出', requiresAuth: true, role: 'admin' } }
            ]
        },
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
                    path: 'admin',
                    redirect: '/hzsca/admin/dashboard'
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
                },
                {
                    path: 'donate',
                    component: () => import('../views/donate.vue'),
                    meta: {title: '捐助我们'}
                },
                {
                    path: 'donate/result',
                    component: () => import('../views/PayResult.vue'),
                    meta: {title: '支付结果', requiresAuth: true}
                },
                {
                    path: 'report',
                    component: () => import('../views/report.vue'),
                    meta: {title: '工作报告'}
                },
                
            ]
        },
        { path: '/login', redirect: '/hzsca/login' },
        { path: '/regist', redirect: '/hzsca/regist' },
        { path: '/personalInfo', redirect: '/hzsca/personalInfo' },
        { path: '/admin', redirect: '/hzsca/admin/dashboard' },
        { path: '/404', name: 'NotFound', component: () => import('../views/404.vue'), meta: { title: '404' }},
        { path: '/:pathMatch(.*)*', redirect: '/404' }
    ]
})

router.beforeEach((to, from, next) => {
    document.title = to.meta.title || 'HZSCA'
    const token = localStorage.getItem('token')
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (token && userInfo.role === 'admin' && to.path.startsWith('/hzsca') && !to.path.startsWith('/hzsca/admin') && to.path !== '/hzsca/login') {
        next('/hzsca/admin/dashboard')
        return
    }
    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!token) {
            ElMessage.warning('请先登录')
            next({
                path: '/hzsca/login',
                query: { redirect: to.fullPath }
            })
            return
        }

        const requiredRole = to.meta.role || (to.meta.requiresAdmin ? 'admin' : null)
        if (requiredRole && userInfo.role !== requiredRole) {
            ElMessage.error('没有权限访问该页面')
            next('/hzsca/home')
            return
        }
    }
    next()
})

export default router
