import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import ReadyPage from '@/components/Ready.vue'
import ConferenceView from '@/views/ConferenceView.vue'
import ProfileView from '@/views/ProfileView.vue'
import SignView from '@/views/SignView.vue'
import TeamSearchView from '@/views/TeamSearchView.vue'
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
    { path: '/team',
      name: 'Team',
      children: [
        {
          path: 'search',
          name: 'TeamSearchView',
          component: TeamSearchView,
        },
        {
          path: 'create',
          name: 'TeamCreateView',
          component: TeamCreateView,
        },
        {
          path: ':name',
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
      ],
    },

    // {
    //   path: "/login",
    //   name:"login",
    //   component:
    // }
  ]
})

export default router
