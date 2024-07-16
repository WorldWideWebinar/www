import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import ReadyPage from '@/components/Ready.vue'
import ConferenceView from '@/views/ConferenceView.vue'

const routes = [
  {
    path: '/',
    name: 'HomeView',
    component: HomeView
  },
  {
    path: '/rnd',
    name: 'rnd',
    component: ReadyPage,
    children: [
      {
        path: 'conference',
        name: 'ConferenceView',
        component: ConferenceView
      }
    ]
  },
  {
    path: '/dev',
    name: 'dev',
    component: ReadyPage
  },
  {
    path: '/purchase',
    name: 'purchase',
    component: ReadyPage
  },
  {
    path: '/sales',
    name: 'sales',
    component: ReadyPage
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router
