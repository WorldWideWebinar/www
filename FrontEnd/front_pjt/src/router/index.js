import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import ReadyPage from '@/components/Ready.vue'
import ConferenceView from '@/views/ConferenceView.vue'
import ProfileView from '@/views/ProfileView.vue'
import SignView from '@/views/SignView.vue'
import TeamSearchView from '@/views/TeamSearchView.vue'
import TeamCreateView from '@/views/TeamCreateView.vue'
import MeetingCreateView from '@/views/MeetingCreateView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'HomeView',
      component: HomeView
    },
    {
      path: '/meeting',
      name: 'MeetingCreateView',
      component: MeetingCreateView,
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
      path: '/team',
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
          path: ':id',
          name: 'ReadyView',
          component: ReadyPage,
        },
      ]
    },
    {
      path: '/conference/:sessionId',
      name: 'ConferenceView',
      component: ConferenceView,
      props: true
    }
  ]
})

export default router

router.beforeEach((to, from, next) => {
  console.log(`Navigating to: ${to.name}`);
  next();
});