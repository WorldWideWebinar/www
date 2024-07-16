import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import ReadyPage from '@/components/Ready.vue'
import ConferenceView from '@/views/ConferenceView.vue'
import ProfileView from '@/views/ProfileView.vue'
import SignView from '@/views/SignView.vue'
import SessionFindView from '@/views/SessionFindView.vue'

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
      path: '/session/find',
      name: 'SessionFindView',
      component: SessionFindView,
    },
    {
      path: '/:name',
      name: 'ReadyView',
      component: ReadyPage,
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
