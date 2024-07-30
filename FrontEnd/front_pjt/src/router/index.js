import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import HomeView from '@/views/HomeView.vue'
import ReadyView from '@/views/ReadyView.vue'
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
          component: ReadyView,
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
  const userStore = useUserStore();

  if (to.name === 'SignView' && userStore.isLogin) {
    // If trying to access SignView and user is already logged in, redirect to HomeView
    next({ name: 'HomeView' });
  } else if (!userStore.isLogin && to.name !== 'HomeView' && to.name !== 'SignView') {
    // If user is not logged in and trying to access any page except HomeView or SignView, redirect to SignView
    next({ name: 'SignView' });
  } else {
    // Allow navigation to target route
    next();
  }
});

router.beforeEach((to, from, next) => {
  console.log(`Navigating to: ${to.name}`);
  next();
});