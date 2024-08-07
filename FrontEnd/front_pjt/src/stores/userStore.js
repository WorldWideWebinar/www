import { defineStore } from 'pinia'
import axiosInstance from '@/axios'
import { useTeamStore } from './teamStore'
import router from '@/router'
import { useErrorStore } from './errorStore'
import { useMeetingStore } from './meetingStore'

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: 1,
    userInfo: {
    },
    accessToken: null,
    refreshToken: null,
    userList: [],
  }),
  getters: {
    isLogin: (state) => state.userId != 0
  },

  actions: {
    async fetchUserInfo(userId) {
      const errorStore = useErrorStore()
      const teamStore = useTeamStore()
      try {
        const response = await axiosInstance.get(`api/users/${userId}`)
        const userData = response.data.result
        this.userInfo = userData


        // teamList를 이용해 teamStore에 팀 정보 추가
        if (Array.isArray(userData.teamList) && userData.teamList.length > 0) {
          await Promise.all(userData.teamList.map((teamId) => teamStore.fetchTeamById(teamId)))
        }

        return userData
      } catch (error) {
        errorStore.showError('Failed to fetch user info')
      }
    },

    async fetchAllUsers() {
      const errorStore = useErrorStore()
      try {
        const response = await axiosInstance.get(`api/users`)
        const users = response.data.result
        this.userList = users.map((user) => ({
          userId: user.userID,
          id: user.id
        }))
        return this.userList
      } catch (error) {
        errorStore.showError('Failed to fetch all users')
      }
    },

    async signIn({ id, password }) {
      const errorStore = useErrorStore()
      try {
        const response = await axiosInstance.post('api/users/login', { id, password })
        if (response.data.result.userId) {
          const { userId, jwt } = response.data.result
          this.userId = userId
          this.accessToken = jwt.accessToken
          this.refreshToken = jwt.refreshToken

          // 사용자 정보 가져오기
          const userInfo = await this.fetchUserInfo(userId)
          if (userInfo) {
            errorStore.showError('Log In Successful')
            // 로그인 성공 후 HomeView로 리디렉션
            router.push({ name: 'HomeView' })

            return { success: true, message: response.data.message }
          } else {
            return { success: false, message: 'Failed to fetch user info' }
          }
        } else {
          errorStore.showError(response.data.message)
          return { success: false, message: response.data.message }
        }
      } catch (error) {
        errorStore.showError('Failed to sign in')
        return { success: false, message: error.message }
      }
    },

    async signUp({ id, name, email, password, language }) {
      const errorStore = useErrorStore()
      try {
        const signupResponse = await axiosInstance.post('api/users', {
          id,
          name,
          idCheck: true,
          email,
          password,
          language
        })
        if (signupResponse.data.success) {
          const signInResponse = await this.signIn({ id, password })
          return signInResponse
        } else {
          errorStore.showError(signupResponse.data.message)
          return { success: false, message: signupResponse.data.message }
        }
      } catch (error) {
        errorStore.showError('Failed to sign up')
        return { success: false, message: error.message }
      }
    },

    async signOut() {
      const errorStore = useErrorStore()
      const teamStore = useTeamStore()
      const meetingStore = useMeetingStore()
      try {
        const headers = {
          Authorization: `Bearer ${this.accessToken}`
        }
        const response = await axiosInstance.post('api/users/logout', { userId: this.userId })

        if (response.data.success) {
          // 사용자 정보를 초기화
          this.userId = 0
          this.userInfo = {}
          this.accessToken = null
          this.refreshToken = null
          meetingStore.clearMeetings()
          teamStore.clearTeams()
          teamStore.clearTeamUsers()
          errorStore.showError('Log Out Successful')
          router.push({ name: 'HomeView' })
          return { success: true, message: response.data.message }
        } else {
          errorStore.showError(response.data.message)
          return { success: false, message: response.data.message }
        }
      } catch (error) {
        errorStore.showError('Failed to sign out')
        return { success: false, message: error.message }
      }
    },

    async checkIdDuplication(id) {
      try {
        const response = await axiosInstance.get(`api/users/duplication/${id}`)
        return response.data.result.available
      } catch (error) {
        throw new Error('ID already exists.')
      }
    },
    async deleteUser(userId) {
      const errorStore = useErrorStore();
      const teamStore = useTeamStore();
      const meetingStore = useMeetingStore();
      try {
        const response = await axiosInstance.delete(`api/users/${userId}`, {
          headers: {
            'Authorization': `Bearer ${this.accessToken}`
          }
        });

        if (response.data.success) {
          // 사용자 정보를 초기화
          this.userId = 0;
          this.userInfo = {};
          this.accessToken = null;
          this.refreshToken = null;
          meetingStore.clearMeetings();
          teamStore.clearTeams();
          router.push({ name: 'HomeView' });
          return { success: true, message: response.data.message };
        } else {
          errorStore.showError(response.data.message);
          return { success: false, message: response.data.message };
        }
      } catch (error) {
        errorStore.showError('Failed to delete user');
        return { success: false, message: error.message };
      }
    },

    async changeUserInfo(userId, newUserInfo) {
      const errorStore = useErrorStore();
      try {
        const response = await axiosInstance.put(`api/users/${userId}`, newUserInfo, {
          headers: {
            'Authorization': `Bearer ${this.accessToken}`
          }
        });

        if (response.data.success) {
          // 사용자 정보를 업데이트
          this.userInfo = { ...this.userInfo, ...newUserInfo };
          return { success: true, message: response.data.message };
        } else {
          errorStore.showError(response.data.message);
          return { success: false, message: response.data.message };
        }
      } catch (error) {
        errorStore.showError('Failed to update user info');
        return { success: false, message: error.message };
      }
    }
  },
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'userStore',
        storage: localStorage,
        paths: ['userId', 'userInfo', 'accessToken', 'refreshToken']
      }
    ]
  }
})
