<template>
  <div class="landing-page-container">
  </div>
  <div class="upper">
  </div>
  <div class="lower">
    <div class="sub-discription carousel my-meeting" v-if="!session">
      <div :class="['container', { 'right-panel-active': isRightPanelActive }]" id="container">
        <SignUp />
        <SignIn />
        <div class="overlay-container">
          <div class="overlay">
            <div class="overlay-panel overlay-left">
              <h1>Welcome Back!</h1>
              <p>To keep connected with us please login with your personal info</p>
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
    <div class="my-meeting" v-if="session">
      <h2>My Meeting</h2>
      <div class="carousel">
        <Carousel :itemsToShow="3" :wrapAround="true" :transition="500">
          <Slide v-for="(item, index) in carouselContent" :key="index">
            <div class="carousel-section">
              <h3>{{ item.category }}</h3>
              <div class="meeting-details" v-for="(meeting, idx) in item.meetings" :key="idx">
                <div class="meeting-detail-left">
                  <p class="meeting-name">{{ meeting.name }}</p>
                  <button>IN</button>
                </div>
                <div class="meeting-detail-right" v-if="item.category !== 'TODAY'">
                  <p class="meeting-date">{{ meeting.date }}</p>
                  <p class="meeting-time">{{ meeting.time }}</p>
                </div>
                <div class="meeting-detail-right" v-else>
                  <p class="meeting-date">▶️</p>
                  <p class="meeting-time"> 들어가기</p>
                </div>
                <div v-if="item.category === 'NEXT'" class="meeting-detail-right">
                  <p class="go_detail" @click="router.push()">> details</p>
                </div>
                <div v-else class="spacer"></div>
              </div>
            </div>
          </Slide>
          <template #addons>
            <Navigation />
          </template>
        </Carousel>
      </div>
    </div>
    <div class="footer">
      Footer
    </div>
  </div>
</template>

<script setup>
import router from '@/router';
import { onMounted, ref, watch } from 'vue';
import 'vue3-carousel/dist/carousel.css';
import { Carousel, Slide, Navigation } from 'vue3-carousel';
import SignUp from '@/components/HomeView/SignUp.vue';
import SignIn from '@/components/HomeView/SignIn.vue';

const isRightPanelActive = ref(false);

const activateSignUp = () => {
  isRightPanelActive.value = true;
};

const activateSignIn = () => {
  isRightPanelActive.value = false;
};

const session = ref(!(sessionStorage.getItem('logInInfo') == null));

const handleSessionChange = () => {
  session.value = sessionStorage.getItem('loginInfo') !== null;
};

onMounted(() => {
  handleSessionChange();
});

const carouselContent = ref([
  {
    category: "TODAY",
    meetings: [
      { name: "웹 RTC", date: "2024.07.11", time: "14PM-16PM" },
      { name: "뱅킹서비스", date: "2024.07.11", time: "14PM-16PM" },
      { name: "인스타그램", date: "2024.07.11", time: "14PM-16PM" }
    ]
  },
  {
    category: "NEXT",
    meetings: [
      { name: "서비스", date: "2024.09.15", time: "10AM-13PM" },
      { name: "AI 개발", date: "2024.08.26", time: "15PM-16PM" }
    ]
  },
  {
    category: "PREV",
    meetings: [
      { name: "TTS", date: "2024.06.28", time: "10AM-13PM" },
      { name: "AI 요약", date: "2024.06.22", time: "15PM-16PM" }
    ]
  }
]);
</script>

<style scoped>
.landing-page-container {
}

.upper {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  height: 100px;
  background-color: #FEF7FF;
}

.lower {
  padding: 20px;
  height: 700px;
  background-color: #FEF7FF;
}

.my-meeting {
  width: 100%;
  text-align: center;
}

.carousel {
  margin: 20px auto;
}

.carousel__viewport {
  perspective: 2000px;
}

.carousel__track {
  transform-style: preserve-3d;
}

.carousel__slide {
  padding: 5px;
  opacity: 0.9;
  transform: rotateY(-20deg) scale(0.9);
}

.carousel__slide--sliding {
  transition: 0.5s;
}

.carousel__slide--active ~ .carousel__slide {
  transform: rotateY(20deg) scale(0.9);
}

.carousel__slide--prev {
  opacity: 1;
  transform: rotateY(-10deg) scale(0.95);
}

.carousel__slide--next {
  opacity: 1;
  transform: rotateY(10deg) scale(0.95);
}

.carousel__slide--active {
  opacity: 1;
  transform: rotateY(0) scale(1.1);
}

.carousel-section {
  padding: 10px;
  height: 300px;
  width: 400px;
  background-color: #fce9ff;
  border-radius: 30px;
}

.meeting-details {
  display: flex;
  margin: 10px 0;
  justify-content: space-between;
}

.meeting-detail-left,
.meeting-detail-right {
  width: 33%;
}

.meeting-name,
.meeting-date,
.meeting-time {
  margin: 5px 0;
  font-size: 14px;
}

.meeting-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 회원가입&로그인 */

* {
  box-sizing: border-box;
}

body {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  height: 100vh;
  margin: -20px 0 50px;
  background: #f6f5f7;
  font-family: 'Montserrat', sans-serif;
}

h1 {
  font-weight: bold;
  margin: 0;
}

a {
  color: #333;
  font-size: 14px;
  text-decoration: none;
  margin: 15px 0;
}

button {
  padding: 12px 45px;
  border-radius: 20px;
  border: 1px solid #6a1b9a;
  background-color: #6a1b9a;
  color: #FFFFFF;
  font-size: 12px;
  font-weight: bold;
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
  border-color: #FFFFFF;
}

form {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 0 50px;
  height: 100%;
  text-align: center;
  background-color: #FFFFFF;
}

input {
  width: 100%;
  padding: 12px 15px;
  margin: 8px 0;
  background-color: #eee;
  border: none;
}

.container {
  position: relative;
  overflow: hidden;
  width: 1000px;
  max-width: 100%;
  min-height: 600px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 14px 28px rgba(0,0,0,0.25), 
              0 10px 10px rgba(0,0,0,0.22);
}

.form-container {
  position: absolute;
  top: 0;
  width: 50%;
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
  0%, 49.99% {
    opacity: 0;
    z-index: 1;
  }
  
  50%, 100% {
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
  position: relative;
  left: -100%;
  width: 200%;
  height: 100%;
  opacity: 0.8;
  color: #FFFFFF;
  background: linear-gradient(to right, #f8bbd0, #fcdde4);
  background-repeat: no-repeat;
  background-size: cover;
  background-position: 0 0;
  transform: translateX(0);
  transition: transform 0.6s ease-in-out;
}

.container.right-panel-active .overlay {
  transform: translateX(50%);
}

.overlay-panel {
  position: absolute;
  top: 0;
  width: 50%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 0 40px;
  text-align: center;
  transform: translateX(0);
  transition: transform 0.6s ease-in-out;
}

.overlay-left {
  transform: translateX(-20%);
}

.container.right-panel-active .overlay-left {
  transform: translateX(0);
}

.overlay-right {
  right: 0;
  transform: translateX(0);
}

.container.right-panel-active .overlay-right {
  transform: translateX(20%);
}

.social-container {
  margin: 20px 0;
}

.social-container a {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  margin: 0 5px;
  width: 40px;
  height: 40px;
  border: 1px solid #DDDDDD;
  border-radius: 50%;
}
</style>
