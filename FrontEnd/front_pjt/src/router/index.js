import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import MeetingList from '@/components/MeetingList.vue'
import ConferenceView from '@/views/ConferenceView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'HomeView',
      component: HomeView
    },
    {
      path: '/rnd',
      name: 'rnd',
      component: MeetingList,
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
      component: MeetingList
    },
    {
      path: '/purchase',
      name: 'purchase',
      component: MeetingList
    },
    {
      path: '/sales',
      name: 'sales',
      component: MeetingList
    }
  ]
})

export default router
