<template>
  <Loading v-model="loading" />
  <div class="container-wrapper">
    <div :class="['container', { 'right-panel-active': isRightPanelActive }]" id="container">
      <div class="sign">
        <SignUp />
        <SignIn />
      </div>
      <div class="overlay-container">
        <div class="overlay">
          <div class="overlay-panel overlay-left">
            <h1>Welcome Back!</h1>
            <p>Please login and stay connected with us</p>
            <button class="ghost" @click="activateSignIn">Sign In</button>
          </div>
          <div class="overlay-panel overlay-right">
            <h1>Hello, Friend!</h1>
            <p>Enter your personal details and start journey with us</p>
            <button class="ghost" @click="activateSignUp">Sign Up</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import SignUp from '@/components/SignView/SignUp.vue'
import SignIn from '@/components/SignView/SignIn.vue'
import Loading from '@/components/Loading.vue'

const isRightPanelActive = ref(false)
const route = useRoute()
const loading = ref(true)
const activateSignUp = () => {
  isRightPanelActive.value = true
}

const activateSignIn = () => {
  isRightPanelActive.value = false
}

// 페이지가 로드될 때 query 파라미터를 확인하여 적절한 overlay를 활성화
onMounted(() => {
  const mode = route.query.mode
  if (mode === 'signup') {
    activateSignUp()
  } else {
    activateSignIn()
  }
  loading.value = false
})
</script>


<style scoped>
* {
  box-sizing: border-box;
}

html, body {
  height: 100%;
  margin: 0;
}

body {
  background: #f6f5f7;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: 'Montserrat', sans-serif;
}

h1 {
  font-weight: bold;
  margin: 0;
}

p {
  font-size: 14px;
  font-weight: 100;
  line-height: 20px;
  letter-spacing: 0.5px;
  margin: 20px 0 30px;
}

a {
  color: #333;
  font-size: 14px;
  text-decoration: none;
  margin: 15px 0;
}

button {
  border-radius: 20px;
  border: 1px solid rgb(166, 125, 247);
  background-color: rgb(166, 125, 247);
  color: #ffffff;
  font-size: 12px;
  font-weight: bold;
  padding: 12px 45px;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: transform 80ms ease-in;
}


button:active {
  transform: scale(0.95);
}

button:focus {
  outline: none;
}

button.ghost {
  background-color: transparent;
  border-color: #ffffff;
}

button.ghost:hover {
  background-color: #e9adc2;
  border: 1px solid #e9adc2;
}

form {
  background-color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 0 50px;
  height: 100%;
  text-align: center;
}

input {
  background-color: #eee;
  border: none;
  padding: 12px 15px;
  margin: 8px 0;
  width: 100%;
}

.container-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 100%;
}

#container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: auto; /* 상하좌우 중앙 정렬 */
}

.container {
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22);
  position: relative;
  overflow: hidden;
  width: 900px; /* 사이즈 키움 */
  max-width: 100%;
  min-height: 600px; /* 사이즈 키움 */
}

.form-container {
  position: absolute;
  top: 0;
  height: 100%;
  transition: all 0.6s ease-in-out;
}

.sign-in-container {
  left: 0;
  width: 50%;
  z-index: 2;
}

.container.right-panel-active .sign-in-container {
  transform: translateX(100%);
}

.sign-up-container {
  left: 0;
  width: 50%;
  opacity: 0;
  z-index: 1;
}

.container.right-panel-active .sign-up-container {
  transform: translateX(100%);
  opacity: 1;
  z-index: 5;
  animation: show 0.6s;
}

@keyframes show {
  0%,
  49.99% {
    opacity: 0;
    z-index: 1;
  }

  50%,
  100% {
    opacity: 1;
    z-index: 5;
  }
}

.overlay-container {
  position: absolute;
  top: 0;
  left: 50%;
  width: 50%;
  height: 100%;
  overflow: hidden;
  transition: transform 0.6s ease-in-out;
  z-index: 100;
}

.container.right-panel-active .overlay-container {
  transform: translateX(-100%);
}

.overlay {
  background: #f8bbd0;
  background: -webkit-linear-gradient(to right, #f8bbd0, #fcdde4);
  background: linear-gradient(to right, #f8bbd0, #fcdde4);
  background-repeat: no-repeat;
  background-size: cover;
  background-position: 0 0;
  color: #ffffff;
  position: relative;
  opacity: 0.8;
  left: -100%;
  height: 100%;
  width: 200%;
  transform: translateX(0);
  transition: transform 0.6s ease-in-out;
}

.container.right-panel-active .overlay {
  transform: translateX(50%);
}

.overlay-panel {
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 0 40px;
  text-align: center;
  top: 0;
  height: 100%;
  width: 50%;
  transform: translateX(0);
  transition: transform 0.6s ease-in-out;
}

.overlay-left {
  transform: translateX(-20%);
  background: linear-gradient(to right, #f8bbd0, #fcdde4); /* left panel 전용 그라데이션 */
}

.container.right-panel-active .overlay-left {
  transform: translateX(0);
}

.overlay-right {
  right: 0;
  transform: translateX(0);
  background: linear-gradient(to left, #fcdde4, #f8bbd0); /* right panel 전용 그라데이션 */
}

.container.right-panel-active .overlay-right {
  transform: translateX(20%);
}

@media (max-width: 992px) {
  .container-wrapper {
    margin: auto 30px;
  }
}
</style>

