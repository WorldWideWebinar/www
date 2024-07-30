
<template>
  <div class="home-container">
    <div class="before-scroll">
      <div class="upper">
        <div class="main-title">
          <div class="title"><span class="w">W</span>orld</div>
          <div class="title"><span class="w">W</span>ide</div>
          <div class="title"><span class="w">W</span>ebinar</div>
        </div>
        <div class="main-image">
          <img class="main-character" src="@/assets/img/main_logo.png" alt="logo">
        </div>
      </div>
      <div class="main-btn">
        <div v-if="!isLogin">
          <button @click="router.push({name:'SignView'})" style="margin-right: 50px;">Sign in</button>
          <button @click="router.push({name:'SignView'})">Sign up</button>
        </div>
        <div v-else>
          <button @click="router.push({name:'ProfileView'})">My Page</button>
          <button @click="handleSignOut">Log Out</button>
        </div>
      </div>

      <div class="scroll">
        <div class="txt">Scroll Down</div>
        <div class="circle"></div>
      </div>
    </div>


    <div class="lower" >
      <!-- 로그인 전 -->
      <div class="sub-discription carousel my-meeting" v-if="!session">
        <div class="discription">
          <p class="plus">Easy to manage by team</p>
          <img class="plus-image" src="../../assets/img/team.png" alt="team">
          <div class="plus-detail">
            <p>You can ~</p>
          </div>
        </div>
        <div class="discription">
          <p class="plus">Multi-Lingual Translation</p>
          <img class="plus-image" src="../../assets/img/translation.png" alt="translation">
          <div class="plus-detail">
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
        <div class="discription">
          <p class="plus">Real-Time Conference</p>
          <img class="plus-image" src="../../assets/img/conference.png" alt="conference">
          <div class="plus-detail">
            <p>You can ~</p>
          </div>
        </div>
      </div>
      <!-- 로그인 후 -->
      <div class="my-meeting" v-if="isLogin">
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
    </div>

    <!-- footer  -->
    <div class="footer">
      <div class="sub-footer">
        <p class="www">World Wide Webinar</p>
        <p class="copy-right">© 2024 All Rights Reserved.</p>
      </div>
      <!-- <div class="sub-footer">
        <p class="team-name">주영 업고 튀어</p>
        <p class="team-member">권용수 | 김수빈 | 박준영 | 이선재 | 주연수</p>
      </div> -->
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import 'vue3-carousel/dist/carousel.css';
import { Carousel, Slide, Pagination, Navigation } from 'vue3-carousel';

const router = useRouter();
const userStore = useUserStore();

const isLogin = computed(() => userStore.isLogin);

const handleSignOut = async () => {
  const result = await userStore.signOut();
  if (result.isSuccess) {
    alert('Successfully logged out');
    router.push({ name: 'HomeView' });
  } else {
    alert(`Logout failed: ${result.message}`);
  }
};

// `isLogin` 상태를 확인하기 위해 `watch` 추가
watch(isLogin, (newValue) => {
  console.log('isLogin changed:', newValue);
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
.home-container {
  margin: 0px auto;
  width: 100%;
}

.before-scroll {
  height: 90vh;
}

.upper {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  width: 100%;
  margin: 0px auto;
  padding-top: 40px;
  padding-bottom: 70px;
  background-color: rgba(211, 211, 211, 0.2);
}

.main-title {
  font-size: 3rem;
  font-weight: bolder;
  opacity: 0;
  transform: translateX(-50px);
  animation: slideInFromLeft 1s forwards;
  animation-delay: 1s; /* 1초 후에 애니메이션 시작 */
  margin: 0 100px; /* 간격 조정 */
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

/* 로그인/회원가입 */
.main-btn {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0px auto 40px auto;
  padding-bottom: 40px;
  background-color: rgba(211, 211, 211, 0.2);
}

button {
  font-size: 12px;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 20px;
  background-color: #6a1b9a;
  font-size: 12px;
  font-weight: bold;
  padding: 12px 45px;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: transform 80ms ease-in;
}

button:hover {
  background-color: #b380bc;
}

/* scroll */
.scroll {
  position: relative;
  text-align: center;
  padding-bottom: 15px;
}

.scroll .txt {
  font-size: 0.65rem;
  font-weight: bold;
  /* padding-top: 20px; */
  margin-bottom: 20px;
}

.scroll .circle {
    position: absolute;
    left: 50%;
    top: 100%;
    transform: translateX(-50%);
    width: 8px;
    height: 8px;
    background-color: black;
    border-radius: 50%;
    animation: bounce .65s infinite alternate cubic-bezier(.1, .49, .42, .99);
}

@keyframes bounce {
  0% {
    transform: translate(-50%, 0);
  }
  100% {
    transform: translate(-50%, -20px);
  }
}

/* lower */
.lower {
  height: 600px;
  padding: 0px 30px;
  margin: 0 auto;
  text-align: center;
}

/* 로그인 전 */
.sub-discription {
  display: flex;
  justify-content: space-around;
  margin: 0 auto;
  padding: 50px 0;
}

.discription {
  width: 30%;
  min-height: 400px;
  background-color: rgba(250, 235, 240, 0.5);
  padding: 40px 10px 10px 10px;
}

.discription:hover {
  background-color: rgba(250, 235, 240, 1);
  cursor: pointer;
}

.discription .plus {
  font-size: 1.2rem;
  font-weight: bold;
  padding: 0px 15px;
  min-height: 50px;
}

.discription .plus-image {
  margin: 0px;
  width: 100px;
}

/* 세부사항 */
.plus-detail {
  margin: 40px auto 20px auto;
  width: 90%;
}

.language-table {
  border: 1px solid lightgray;
  width: 100%;
  margin: 0px auto;
}

.language-table td {
  border: 1px solid lightgray;
  width: 48%;
  padding: 10px 0px;
}

.language-table td:hover {
  background-color: #FEF7FF;
  cursor: pointer;
}


/* 로그인 후 */
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

/* footer */
.footer {
  margin: 0 auto;
  background-color: rgba(211, 211, 211, 0.2);
  text-align: center;
  color: #8A8F95;
  font-size: 0.7rem;
}

.sub-footer {
  padding: 17px;
}

.sub-footer p {
  margin: 5px;
}

.www, .team-name {
  font-weight: bolder;
}

@media (max-width: 992px) {
  .main-image,
  .main-character {
    width: 30%;
  }

  .main-title {
    width: 100%;
  }
}
</style>
