import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {path: '/', redirect: '/hzsca/home'},
    {path: '/hzsca', component: ()=> import('../views/HomeView.vue'),children: [
        {path: 'home', component: ()=> import('../views/Homepage.vue'),meta: {title: '首页'}},
      ]},
  ],
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title
  next()
})
export default router
