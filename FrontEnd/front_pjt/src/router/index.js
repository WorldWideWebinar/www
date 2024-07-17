import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import MeetingList from '@/components/MeetingList.vue'
import ConferenceView from '@/views/ConferenceView.vue'
import ProfileView from '@/views/ProfileView.vue'
import SignView from '@/views/SignView.vue'
import TeamFindView from '@/views/TeamFindView.vue'
import TeamCreateView from '@/views/TeamCreateView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'HomeView',
      component: HomeView
    },
    {
      path: '/accounts',
      name: 'AccountView',
      children: [
      {
        path: 'sign',
        name: 'SignView',
        component: SignView
      },
      {
        path: 'profile',
        name: 'ProfileView',
        component: ProfileView,
        props: true
      },
    ]
    },
    {
      path: '/team/find',
      name: 'TeamFindView',
      component: TeamFindView,
    },
    {
      path: '/:name',
      name: 'ReadyView',
      component: MeetingList,
      children: [
        {
          path: 'conference',
          name: 'ConferenceView',
          component: ConferenceView
        }
      ]
    },

    // {
    //   path: "/login",
    //   name:"login",
    //   component:
    // }
  ]
})

export default router
