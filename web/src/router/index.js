import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import Login from '@/views/Login.vue'
import PortManagement from "@/views/Port/PortManagement.vue";
import ShipManagement from "@/views/Ship/ShipManagement.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
    },
    {
      path: "/Port/PortManagement",
      name: "port",
      component: PortManagement,
    },
    {
      path: "/Ship/ShipManagement",
      name: 'ship',
      component: ShipManagement,
    }
  ],
})

export default router
