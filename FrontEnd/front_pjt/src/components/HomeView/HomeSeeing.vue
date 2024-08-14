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
          <img class="main-character" src="@/assets/img/main_logo.png" alt="logo" />
        </div>
      </div>
      <div class="main-btn">
        <div v-if="!isLogin">
          <button @click="navigateToSignVeiw('signin')" style="margin-right: 50px">
            SIGN IN
          </button>
          <button @click="navigateToSignVeiw('signup')">SIGN UP</button>
        </div>
        <div v-else>
          <button @click="router.push({ name: 'ProfileView' })" style="margin-right: 50px">
            MY PAGE
          </button>
          <button @click="handleSignOut">LOG OUT</button>
        </div>
      </div>
      <div class="scroll">
        <div class="txt">Scroll Down</div>
        <div class="circle"></div>
      </div>
    </div>

    <div class="middle">
      <div class="sub-discription">
        <div class="discription">
          <p class="plus">Easy to manage by team</p>
          <img class="plus-image" src="../../assets/img/team.png" alt="team" />
          <div class="plus-detail">
            <p>
              Our platform offers seamless team management capabilities, allowing you to
              effortlessly organize, schedule, and manage virtual meetings.
            </p>
            <p>
              Collaborate with your team members in real-time, assign roles, and keep track of
              meeting progress and outcomes.
            </p>
          </div>
        </div>
        <div class="discription">
          <p class="plus">Multi-Lingual Translation</p>
          <img class="plus-image" src="../../assets/img/translation.png" alt="translation" />
          <div class="plus-detail">
            <p>Break language barriers with our instant, multi-lingual, real-time translation.</p>
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
          <img class="plus-image" src="../../assets/img/conference.png" alt="conference" />
          <div class="plus-detail">
            <p>
              Experience seamless real-time video conferencing with our platform, which ensures
              high-quality video and audio for uninterrupted communication.
            </p>
            <p>
              Engage with participants through interactive features such as screen sharing, chat,
              and virtual whiteboards.
            </p>
          </div>
        </div>
      </div>

      <!-- 로그인 후 -->
      <div class="lower" v-if="isLogin">
        <div class="my-meeting">
          <h3 style="font-weight: bolder">My Meetings</h3>

          <div v-if="!groupMeetings">
            <p>회의 없음.</p>
          </div>
          <div v-else class="carousel">
            <Carousel v-if="carouselReady" :itemsToShow="3" :wrapAround="true" :transition="500">
              <Slide v-for="(item, index) in groupedMeetings"
                :key="index"
                class="slide"
                :class="slideClass(index)"
              >
                <div class="meeting-type">{{ index }} Meetings</div>
                <div class="meeting-table-container">
                  <table class="meeting-table" v-if="item.length>0">
                    <thead>
                      <tr>
                        <th>Date</th>
                        <th>Time</th>
                        <th>Agenda</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(meeting, idx) in item" :key="idx">
                        <td>{{ meeting.start_at }}</td>
                        <td>{{ meeting.start_at }}</td>
                        <td>{{ meeting.agenda }}</td>
                      </tr>
                    </tbody>
                  </table>
                  <div v-else>
                    No meeting Day
                  </div>
                </div>
              </Slide>
              <template #addons>
                <Navigation />
              </template>
            </Carousel>
          </div>
        </div>
      </div>
    </div>

    <!-- footer -->
    <div class="footer">
      <div class="sub-footer">
        <p class="www">World Wide Webinar</p>
        <p class="copy-right">© 2024 All Rights Reserved.</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import { useTeamStore } from '@/stores/teamStore'
import 'vue3-carousel/dist/carousel.css'
import { Carousel, Slide, Navigation } from 'vue3-carousel'

const router = useRouter()
const userStore = useUserStore()
const teamStore = useTeamStore()
const isLogin = computed(() => userStore.isLogin)

const handleSignOut = async () => {
  const result = await userStore.signOut()
  if (result.success) {
    router.push({ name: 'HomeView' })
  }
}

const groupedMeetings = ref({ PREV: [], TODAY: [], NEXT: [] })

const loadMeetings = async (teamId) => {} 

<<<<<<< HEAD
const carouselReady = ref(false)

=======
>>>>>>> 5c72a7a245c98ceff5d260e2c5f62b72d8d2d9b0
const slideClass = (group) => {
  switch (group) {
    case 'PREV':
      return 'slide-prev'
    case 'TODAY':
      return 'slide-today'
    case 'NEXT':
      return 'slide-next'
    default:
      return ''
  }
}

const navigateToSignVeiw = (mode) => {
  router.push({ name: 'SignView', query: { mode } })
}

onMounted(async () => {
  if (userStore.userId != 0) {
  const teamList = userStore.userInfo.teamList || []
  for (const teamId of teamList) {
    await teamStore.fetchTeamById(teamId)
    }
  }
})
</script>

<style scoped>
.home-container { 
  margin: 0px auto;
  width: 100%;
  position: relative;
}

.before-scroll {
  height: 100vh;
  position: relative;
  overflow: hidden;
  background: url('@/assets/img/background.webp') no-repeat center center;
  background-size: cover;
  background-attachment: fixed;
}

.upper {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin: 50px auto;
  padding: 0px 0px;
  /* WWW 글자 오른쪽으로 밀기*/
}

.main-title {
  font-size: 3.5rem;
  font-weight: bolder;
  opacity: 0;
  transform: translateX(-50px);
  animation: slideInFromLeft 1s forwards;
  animation-delay: 1s;
  /* 1초 후에 애니메이션 시작 */
  margin: 0px 100px;
  /* 간격 조정 */
}

.main-image {
  position: absolute;
  bottom: 7%;
  right: 0;
  margin: 20px;
}

.main-image img {
  width: 220px;
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
  font-size: 5.5rem;
  display: inline-block;
  transition: transform 0.2s ease-in-out;
}

.title .w:hover {
  animation: shake 0.5s;
  animation-iteration-count: infinite;
}

@keyframes shake {
  0% {
    transform: translate(1px, 1px) rotate(0deg);
  }
  10% {
    transform: translate(-1px, -2px) rotate(-1deg);
  }
  20% {
    transform: translate(-3px, 0px) rotate(1deg);
  }
  30% {
    transform: translate(3px, 2px) rotate(0deg);
  }
  40% {
    transform: translate(1px, -1px) rotate(1deg);
  }
  50% {
    transform: translate(-1px, 2px) rotate(-1deg);
  }
  60% {
    transform: translate(-3px, 1px) rotate(0deg);
  }
  70% {
    transform: translate(3px, 1px) rotate(-1deg);
  }
  80% {
    transform: translate(-1px, -1px) rotate(1deg);
  }
  90% {
    transform: translate(1px, 2px) rotate(0deg);
  }
  100% {
    transform: translate(1px, -2px) rotate(-1deg);
  }
}

/* 로그인/회원가입 */
.main-btn {
  position: absolute;
  bottom: 15%;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
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
  padding: 12px 35px;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: transform 80ms ease-in;
}

button:hover {
  background-color: #b380bc;
}

/* scroll */
.scroll {
  position: absolute;
  left: 50%;
  bottom: 20%;
  transform: translateX(-50%);
  text-align: center;
  bottom: 10px;
}

.scroll .txt {
  font-size: 0.65rem;
  font-weight: bold;
  margin-bottom: 35px;
}

.scroll .circle {
  position: absolute;
  left: 50%;
  top: 90%;
  transform: translateX(-50%);
  width: 8px;
  height: 8px;
  background-color: black;
  border-radius: 50%;
  animation: bounce 0.65s infinite alternate cubic-bezier(0.1, 0.49, 0.42, 0.99);
}

@keyframes bounce {
  0% {
    transform: translate(-50%, 0);
  }

  100% {
    transform: translate(-50%, -20px);
  }
}

/* middle */
.middle {
  min-height: 85vh;
  padding: 0px;
  margin: 0px;
  text-align: center;
}

/* 로그인 전 */
.sub-discription {
  display: flex;
  justify-content: space-around;
  margin: 0px auto;
  padding: 100px 30px;
}

.discription {
  width: 30%;
  min-height: 400px;
  background-color: rgba(250, 235, 240, 0.5);
  padding: 40px 30px 10px 30px;
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
  cursor: pointer;
}

.discription .plus:hover {
  text-decoration-line: underline;
  text-decoration-style: line;
  text-decoration-color: rgba(154, 130, 253, 0.2);
  text-decoration-thickness: 3px;
}

.discription .plus-image {
  margin: 0px;
  width: 100px;
}

/* 세부사항 */
.plus-detail {
  margin: 40px auto 20px auto;
  width: 90%;
  text-align: left;
  font-size: 0.85rem;
  font-weight: 600;
}

.language-table {
  border: 1px solid lightgray;
  width: 100%;
  margin: 0px auto;
  text-align: center;
}

.language-table td {
  border: 1px solid lightgray;
  width: 48%;
  padding: 10px 0px;
}

.language-table td:hover {
  background-color: #fef7ff;
  cursor: pointer;
}

/* 로그인 후 */
.lower {
  width: 100%;
  text-align: center;
  margin: 0px auto;
  background: url('@/assets/img/background2.webp') no-repeat center center;
  background-size: cover;
  background-attachment: fixed;
}

.my-meeting {
  width: 100%;
  text-align: center;
  margin: 0px auto;
  padding: 100px 30px;
}

/* carousel */
.carousel {
  padding: auto;
  margin: 50px auto;
  padding-right: 0.15%;
  overflow: hidden;
  width: 100%;
}

.carousel__slide {
  padding: 5px;
  background-color: rgb(222, 222, 222); /* 기본 배경색 설정 */
  /* max-width: 32.8%; */
  height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0.9;
  transform: rotateY(-20deg) scale(0.9);
}

.carousel__viewport {
  perspective: 2000px;
  overflow: hidden;
  width: 100%;
}

.carousel__track {
  transform-style: preserve-3d;
}

.carousel__slide--sliding {
  transition: 0.5s ease-in-out;
}

.carousel__slide--active ~ .carousel__slide {
  transform: rotateY(20deg) scale(0.95);
  /* margin-right: 5px; */
  /* margin-right: 3%; */
}

.carousel__slide--prev {
  opacity: 1;
  transform: rotateY(-10deg) scale(0.95);
  /* margin-right: 1%; */
  /* margin-left: 2%; */
  /* padding-left: 0; */
}

.carousel__slide--next {
  opacity: 1;
  transform: rotateY(10deg) scale(0.95);
  /* margin-right: 1%; */
  /* margin-left: 1%; */
}

.carousel__slide--active {
  opacity: 1;
  transform: rotateY(0) scale(1.05);
  margin-left: 1%;
  /* margin-right: 1%; */
}

/* .slide {
  width: 30%;
} */

.slide-today {
  background-color: rgb(253, 218, 223);
}

.meeting-type {
  text-align: center;
  font-weight: bolder;
  margin: auto;
  margin-bottom: 10px;
}

.meeting-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.meeting-table-container {
  display: flex;
  justify-content: center;
  width: 100%;
  height: 230px;
}

.meeting-table {
  width: 90%; /* 테이블을 가운데로 정렬하기 위해 90%로 설정 */
  font-size: 0.6rem;
  border-collapse: collapse;
  margin: 10px 0;
}

.meeting-table tbody {
  max-height: 100px;
  overflow-y: scroll;
  /* width: 100%; */
}

.meeting-table tbody::-webkit-scrollbar {
  width: 0;
  /* 스크롤바의 너비를 0으로 설정 */
  background: transparent;
  /* 스크롤바 배경을 투명하게 설정 */
}

.meeting-table tr {
  display: table;
  /* width: calc(100% - 1rem); */
  width: 100%;
  /* 테이블 너비를 100%에서 약간 줄임 */
  table-layout: fixed;
}

.meeting-table th,
.meeting-table td {
  border: 1px solid #000000;
  padding: 8px;
  text-align: center;
  
}

.meeting-table th:nth-child(1),
.meeting-table td:nth-child(1) {
  width: 25%;
}

.meeting-table th:nth-child(2),
.meeting-table td:nth-child(2) {
  width: 25%;
}

.meeting-table th:nth-child(3),
.meeting-table td:nth-child(3) {
  width: auto;
}

/* footer */
.footer {
  margin: 0 auto;
  background-color: rgba(211, 211, 211, 0.2);
  text-align: center;
  color: #8a8f95;
  font-size: 0.7rem;
}

.sub-footer {
  padding: 17px;
}

.sub-footer p {
  margin: 5px;
}

.www,
.team-name {
  font-weight: bolder;
}

@media (max-width: 992px) {

  .main-image,
  .main-character {
    bottom: 45%;
  }

  .main-image img {
    width: 180px;
  }

  .main-title {
    width: 100%;
  }

  .plus-detail {
    display: none;
  }

  .slide-prev,
  .slide-next {
    display: none;
  }

  .carousel__slide {
    display: none;
  }

  .slide-today {
    display: flex !important; /* Today Meetings 슬라이드를 flex로 표시 */
    transform: rotateY(0) scale(1); /* 3D 효과 제거 */
  }
}
</style>
