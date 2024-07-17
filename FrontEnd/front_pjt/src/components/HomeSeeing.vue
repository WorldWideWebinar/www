<template>
  <div class="upper">
    <div class="spacer"></div>
    <div class="Main-title"> Project Title</div>
    <div class="Main-Explanation">Explanation</div>
    <div class="main-btn">
      <div v-if="!session">
        <button @click="router.push({name:'SignView'})">login</button> <!-- name을 수정-->
      </div>
      <div v-else>
        <button @click="router.push({name:'ProfileView'})">mypage</button>  <!-- name을 수정-->
      </div>
    </div>
    <div class="spacer"></div>
  </div>
  <div class="lower" >
    <div class="sub-discription carousel my-meeting" v-if="!session">
      <div class="discription">
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
    <div style="text-align: center;">
      주저리 주저리
    </div>
  </div>
</template>

<script setup>
import router from '@/router';
import { onMounted, ref, watch } from 'vue';
import 'vue3-carousel/dist/carousel.css'
import { Carousel, Slide, Pagination, Navigation } from 'vue3-carousel'

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

const discriptionContent = ref([
  '첫 번째 설명 내용입니다. 이 설명은 예시를 위한 것이며, 다양한 정보를 포함할 수 있습니다. 예를 들어, 이 내용에는 사용 방법이나 주요 특징 등이 포함될 수 있습니다. 이를 통해 사용자는 해당 항목에 대해 보다 자세히 이해할 수 있습니다.',
  '두 번째 설명 내용입니다. 이 설명은 사용자에게 유용한 정보를 제공하는 데 목적이 있습니다. 예를 들어, 이 설명에는 제품의 장점이나 혜택, 사용 시 주의사항 등이 포함될 수 있습니다. 이를 통해 사용자는 제품이나 서비스를 더욱 효과적으로 활용할 수 있습니다.',
  '세 번째 설명 내용입니다. 이 설명은 다른 내용과의 조화를 이루며 전체적인 이해를 돕습니다. 예를 들어, 이 설명에는 관련된 추가 정보나 참고 자료, FAQ 등이 포함될 수 있습니다. 이를 통해 사용자는 보다 포괄적인 정보를 얻고 궁금증을 해결할 수 있습니다.'
]);

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
.discription{
  width: 30%;
  min-height: 400px;
  background-color: #fce9ff;
  padding : 20px 10px 10px 10px
}

.sub-discription{
  display: flex;
  justify-content: space-around;
  padding-top: 30px;
  padding-bottom: 40px
}

.spacer{
  flex-grow: 1;
}
.upper {
  /* background-color: #FEF7FF;
   */
   background-image: url(../assets/img/bonobono.jpg);
  height: 500px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start; /* 세로 중앙보다 약간 위에 정렬 */
  align-items: center; /* 가로 중앙 정렬 */
}

.main-content {
  display: flex;
  flex-direction: column;
  align-items: center; /* 가로 중앙 정렬 */
}

.Main-title {
  font-size: 2rem; /* Title에 맞는 글씨 크기 변경 */
  margin-bottom: 20px; /* Title과 Explanation 사이 여백 추가 */
}

.Main-Explanation {
  font-size: 1.2rem; /* Explanation에 맞는 글씨 크기 변경 */
}

.main-btn {
  display: flex;
  justify-content: center; /* 가로 중앙 정렬 */
  align-items: center; /* 세로 중앙 정렬 */
  margin-top: 20px; /* 버튼과 Main-content 사이 여백 추가 */
}

button {
  padding: 5px 10px; /* 버튼 크기 줄이기 */
  font-size: 12px; /* 버튼 글씨 크기 줄이기 */
  background-color: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}

.lower {
  height: 600px;
  /* background-color: #FFF; */
  background-color: #FEF7FF;
  padding: 20px;
}

.my-meeting {
  width: 100%;
  text-align: center;
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

.carousel {
  margin: 20px auto;
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
  justify-content: space-between; /* 여백 간격 조정 */
}

.meeting-detail-left,
.meeting-detail-right {
  width: 33%; /* 왼쪽과 오른쪽의 너비 설정 */
}
.meeting-name,
.meeting-date,
.meeting-time {
  font-size: 14px; /* 글씨 크기 줄이기 */
  margin: 5px 0;
}

.meeting-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>