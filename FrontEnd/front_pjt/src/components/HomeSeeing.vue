<template>
  <div class="home-container">
    <div class="upper">
      <div class="main-title">
        <div class="title"><span class="w">W</span>orld</div>
        <div class="title"><span class="w">W</span>ide</div>
        <div class="title"><span class="w">W</span>ebinar</div>
      </div>
      <div class="main-image">
        <img src="../assets/img/chat.png" alt="logo">
      </div>

      <!-- <div class="main-btn">
        <div v-if="!session">
          <button @click="router.push({name:'SignView'})">login</button>
        </div>
        <div v-else>
          <button @click="router.push({name:'ProfileView'})">mypage</button>
        </div>
      </div> -->
    </div>

    <div class="middle">
      <div class="explanation">
        We provide multi-lingual translation service.
        <div class="language">
          <table class="language-table">
            <tr>
              <td>English</td>
              <td>한국어</td>
            </tr>
            <tr>
              <td>Français</td>
              <td>中國語</td>
            </tr>
            <tr>
              <td>Español</td>
              <td>日本語</td>
            </tr>
          </table>
        </div>
      </div>
    </div>

    <div class="lower" >
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
        <!-- <div class="discription">
          <p> 1번 설명</p>
          <br>
          <p>
            {{ discriptionContent[0] }}
          </p>
        </div>
        <div class="discription">
          <p> 2번 설명</p>
          <br>
          <p>
            {{ discriptionContent[1] }}
          </p>
        </div>
        <div class="discription">
          <p> 3번 설명</p>
          <br>
          <p>
            {{ discriptionContent[2] }}
          </p>
        </div> -->
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
                  <div class="meeting-detail-right" v-if="item.category!=='TODAY'">
                    <p class="meeting-date">{{ meeting.date }}</p>
                    <p class="meeting-time">{{ meeting.time }}</p>
                  </div>
                  <div class="meeting-detail-right" v-else>
                    <p class="meeting-date">▶️</p>
                    <p class="meeting-time"> 들어가기</p>
                  </div>
                  <div v-if="item.category==='NEXT'" class="meeting-detail-right">
                    <p class="go_detail" @click="router.push()">> details</p> <!-- 여기서 저장 되어 있는 미팅 그룹의 아이디로 들어간다.-->
                  </div>
                  <div v-else class="spacer"></div>
                </div>
              </div>
            </Slide>
            <template #addons>
              <Navigation />
              <!-- <Pagination /> -->
            </template>
          </Carousel>
        </div>
      </div>
      <div class="footer">
        Footer
      </div>
    </div>
  </div>
</template>

<script setup>
import router from '@/router';
import { onMounted, ref, watch } from 'vue';
import 'vue3-carousel/dist/carousel.css';
import { Carousel, Slide, Pagination, Navigation } from 'vue3-carousel';
import SignUp from '@/components/SignUp.vue';
import SignIn from '@/components/SignIn.vue';

const isRightPanelActive = ref(false);

const activateSignUp = () => {
  isRightPanelActive.value = true;
};

const activateSignIn = () => {
  isRightPanelActive.value = false;
};


let session = ref(!(sessionStorage.getItem('logInInfo') == null));


const handleSessionChange = () => {
  if (sessionStorage.getItem('loginInfo') == null) {
    session.value = false;
  } else {
    session.value = true;
  }
};
onMounted(() => {
  handleSessionChange();
});
watch(session, () => {
  
});

// const discriptionContent = ref([
//   '첫 번째 설명 내용입니다. 이 설명은 예시를 위한 것이며, 다양한 정보를 포함할 수 있습니다. 예를 들어, 이 내용에는 사용 방법이나 주요 특징 등이 포함될 수 있습니다. 이를 통해 사용자는 해당 항목에 대해 보다 자세히 이해할 수 있습니다.',
//   '두 번째 설명 내용입니다. 이 설명은 사용자에게 유용한 정보를 제공하는 데 목적이 있습니다. 예를 들어, 이 설명에는 제품의 장점이나 혜택, 사용 시 주의사항 등이 포함될 수 있습니다. 이를 통해 사용자는 제품이나 서비스를 더욱 효과적으로 활용할 수 있습니다.',
//   '세 번째 설명 내용입니다. 이 설명은 다른 내용과의 조화를 이루며 전체적인 이해를 돕습니다. 예를 들어, 이 설명에는 관련된 추가 정보나 참고 자료, FAQ 등이 포함될 수 있습니다. 이를 통해 사용자는 보다 포괄적인 정보를 얻고 궁금증을 해결할 수 있습니다.'
// ]);

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
.home-container {
  margin: 30px auto;
  width: 100%;
}

.upper {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  width: 100%;
  margin: 20px auto;
  padding-bottom: 50px;
}

.main-title {
  font-size: 3rem;
  font-weight: bolder;
  opacity: 0;
  transform: translateX(-50px);
  animation: slideInFromLeft 1s forwards;
  animation-delay: 1s; /* 1초 후에 애니메이션 시작 */
  margin-right: 100px; /* 간격 조정 */
  /* background-image: url('../assets/img/computer.png'); 
  background-size: 90%;
  background-repeat: no-repeat;
  background-position: center;  */
}

.main-image {
  text-align: center;
}

.main-image img {
  width: 300px;
  margin: 0;
  opacity: 0;
  transform: translateX(50px);
  animation: slideInFromRight 1s forwards;
}

@keyframes slideInFromRight {
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInFromLeft {
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.title .w {
  font-size: 5rem;
  display: inline-block;
  transition: transform 0.2s ease-in-out;
}

.title .w:hover {
  animation: shake 0.5s;
  animation-iteration-count: infinite;
}

@keyframes shake {
  0% { transform: translate(1px, 1px) rotate(0deg); }
  10% { transform: translate(-1px, -2px) rotate(-1deg); }
  20% { transform: translate(-3px, 0px) rotate(1deg); }
  30% { transform: translate(3px, 2px) rotate(0deg); }
  40% { transform: translate(1px, -1px) rotate(1deg); }
  50% { transform: translate(-1px, 2px) rotate(-1deg); }
  60% { transform: translate(-3px, 1px) rotate(0deg); }
  70% { transform: translate(3px, 1px) rotate(-1deg); }
  80% { transform: translate(-1px, -1px) rotate(1deg); }
  90% { transform: translate(1px, 2px) rotate(0deg); }
  100% { transform: translate(1px, -2px) rotate(-1deg); }
}

/* middle */
.middle {
  margin: 0 auto;
  text-align: center;
  background-color: rgba(211, 211, 211, 0.2);
}

.explanation {
  font-size: 1.2rem;
  font-weight: 700;
  margin: 0 auto;
  padding: 50px 0;
}

.language {
  margin: 10px auto;
  width: 400px;
}

.language-table {
  margin: 10px auto;
  border: 2px dashed lightgray;
}

.language-table td {
  border: 2px dashed lightgray;
  width: 150px;
  padding: 10px;
}

.language-table td:hover {
  background-color: #faebf0;
  cursor: pointer;
}




.main-btn {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

button {
  padding: 5px 10px;
  font-size: 12px;
  background-color: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 20px;
  border: 1px solid #6a1b9a;
  background-color: #6a1b9a;
  font-size: 12px;
  font-weight: bold;
  padding: 12px 45px;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: transform 80ms ease-in;
}

button:hover {
  background-color: #45a049;
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

.lower {
  height: 600px;
  background-color: #FEF7FF;
  padding: 20px;
}

.sub-discription {
  display: flex;
  justify-content: space-around;
  padding-top: 30px;
  padding-bottom: 40px;
}

.discription {
  width: 30%;
  min-height: 400px;
  background-color: #fce9ff;
  padding: 20px 10px 10px 10px;
}

.my-meeting {
  width: 100%;
  text-align: center;
}

.carousel {
  margin: 20px auto;
}

.carousel__slide {
  padding: 5px;
}

.carousel__viewport {
  perspective: 2000px;
}

.carousel__track {
  transform-style: preserve-3d;
}

.carousel__slide--sliding {
  transition: 0.5s;
}

.carousel__slide {
  opacity: 0.9;
  transform: rotateY(-20deg) scale(0.9);
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
  background-color: #fce9ff;
  height: 300px;
  width: 400px;
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
  font-size: 14px;
  margin: 5px 0;
}

.meeting-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.footer {
  margin: 0 auto;
}

* {
  box-sizing: border-box;
}

body {
  background: #f6f5f7;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  font-family: 'Montserrat', sans-serif;
  height: 100vh;
  margin: -20px 0 50px;
}

h1 {
  font-weight: bold;
  margin: 0;
}

h2 {
  text-align: center;
}

p {
  font-size: 14px;
  font-weight: 100;
  line-height: 20px;
  letter-spacing: 0.5px;
  margin: 20px 0 30px;
}

span {
  font-size: 12px;
}

a {
  color: #333;
  font-size: 14px;
  text-decoration: none;
  margin: 15px 0;
}

form {
  background-color: #FFFFFF;
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

.container {
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 14px 28px rgba(0, 0, 0, 0.25),
    0 10px 10px rgba(0, 0, 0, 0.22);
  position: relative;
  overflow: hidden;
  width: 768px;
  max-width: 100%;
  min-height: 480px;
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
  color: #FFFFFF;
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
  border: 1px solid #DDDDDD;
  border-radius: 50%;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  margin: 0 5px;
  height: 40px;
  width: 40px;
}
</style>
