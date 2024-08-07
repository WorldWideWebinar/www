import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import HomeView from '@/views/HomeView.vue'
import ReadyView from '@/views/ReadyView.vue'
import ConferenceView from '@/views/ConferenceView.vue'
import ProfileView from '@/views/ProfileView.vue'
import SignView from '@/views/SignView.vue'
import TeamCreateView from '@/views/TeamCreateView.vue'
import MeetingCreateView from '@/views/MeetingCreateView.vue'
import UserEditView from '@/views/UserEditView.vue'

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
        {
          path: ':userId',
          name: 'UserEditView',
          component: UserEditView,
          props: true
        }
      ]
    },
    { 
      path: '/team',
      name: 'Team',
      children: [
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
      path: '/conference/:sessionId/:token',
      name: 'ConferenceView',
      component: ConferenceView,
      props: true
    }
  ],
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { left: 0, top: 0 };
    }
  }
})

export default router



router.beforeEach((to, from, next) => {
  const userStore = useUserStore();

  if (to.name === 'SignView' && userStore.isLogin) {
    next({ name: 'HomeView' });
  } else if (!userStore.isLogin && to.name !== 'HomeView' && to.name !== 'SignView') {
    next({ name: 'SignView' });
  } else {
    next();
  }
});

router.beforeEach((to, from, next) => {
  console.log(`Navigating to: ${to.name}`);
  next();
});