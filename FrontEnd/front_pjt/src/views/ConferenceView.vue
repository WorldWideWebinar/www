<template>
  <div class="conference-container">
    <header class="header">
      <h3>Welcome to {{ departmentName }} Meeting</h3>
    </header>
    <main class="main-content">
      <div class="left-side">
        <div class="participant" v-for="participant in participants" :key="participant.name">
          <div class="participant-video">
            <img :src="participant.avatar" alt="participant avatar" />
          </div>
          <div class="participant-info">
            <span>{{ participant.name }}</span>
          </div>
        </div>
      </div>
      <div class="center">
        <div class="upper-section">
          <div class="presentation">
            <img src="https://via.placeholder.com/450x350" alt="Presentation Screenshot" />
          </div>
          <div class="right-side">
            <div class="participant">
              <div class="participant-video">
                <img src="https://via.placeholder.com/150x110" alt="Your avatar" />
              </div>
              <div class="participant-info">
                <span>나</span>
              </div>
            </div>
          </div>
        </div>
        <div class="translation-container">
          <div class="translation-section">
            <h5>원문</h5>
            <div class="translation-content">
              <p><strong>Robert:</strong> Please brief me on this month’s inventory status.</p>
              <p>
                <strong>Lisa:</strong>
                包括预计明天到益山港的400吨在内，共有5600吨。这个季度的生产没有问题。
              </p>
            </div>
          </div>
          <div class="translation-section">
            <h5>번역</h5>
            <div class="translation-content">
              <p><strong>로버트:</strong> 이번달 재고 현황에 대해 브리핑 부탁해.</p>
              <p>
                <strong>리사:</strong> 내일 부산항에 도착 예정인 400톤을 포함하면 총 5600톤이야.
                이번 분기 생산에는 문제 없을 것으로 예상돼.
              </p>
            </div>
          </div>
        </div>
      </div>
    </main>
    <footer class="footer">
      <div class="footer-left">
        <span style="font-weight: bold;">Duration</span>
        <span>1:27:31 30min left</span>
      </div>
      <div class="footer-center">
        <span style="font-weight: bold;">Attendance</span>
        <span>4 / 6</span>
      </div>
      <div class="footer-right">
        <span>Invite Alex, Joy</span>
      </div>
    </footer>
    <div class="bottom-toolbar">
      <button class="btn-icon">🎤</button>
      <button class="btn-icon">🎥</button>
      <button class="btn-icon">🔇</button>
      <button class="btn-icon">🔄</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ConferenceView',
  data() {
    return {
      participants: [
        { name: 'Robert', avatar: 'https://via.placeholder.com/150x110' },
        { name: 'Lisa', avatar: 'https://via.placeholder.com/150x110' },
        { name: 'Kevin', avatar: 'https://via.placeholder.com/150x110' }
      ]
    }
  },
  computed: {
    departmentName() {
      return this.$route.params.name;
    }
  }
}
</script>

<style scoped>
.conference-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  text-align: center;
  padding: 0.5rem 0;
  background-color: #ffffff;
  border-bottom: 1px solid #e0e0e0;
}

.main-content {
  display: flex;
  flex: 1;
  padding: 1rem 0;
  box-sizing: border-box;
}

.center {
  display: flex;
  flex-direction: column;
  flex: 1;
  width: 100%;
}

.upper-section {
  display: flex;
  justify-content: space-between;
  /* margin-bottom: 1rem; */
}

/* 참석자 */
.left-side,
.right-side {
  width: 250px;
  background-color: #f3e5f5;
  align-items: center;
  border-radius: 8px;
  padding: 2rem;
  display: flex;
  flex-direction: column;
}

.left-side {
  margin: 0 0 0 1rem;
}

.right-side {
  margin: 0 1rem 0 0;
}

.participant {
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #ffffff;
  padding: 1rem;
  margin: 0.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: 220px;
  height: 180px;
}

.participant-video {
  width: 100%;
  height: 80%;
  background-color: #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.participant-info {
  margin-top: 0.5rem;
  text-align: center;
}

/* 발표 */
.presentation {
  flex: 2;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 600px;
  height: 350px;
}

.presentation img {
  max-width: 100%;
  max-height: 100%;
  border-radius: 8px;
}

/* 번역  */
.translation-container {
  display: flex;
  justify-content: space-between;
  padding: 1rem;
  font-size: 90%;
}

.translation-section {
  flex: 1;
  padding: 1rem;
  background-color: #ffffff;
  border: 1px solid #e0e0e0;
  margin-right: 1rem;
  border: 1px dashed lightgray;
}

.translation-section:last-child {
  margin-right: 0;
}

.translation-content {
  background-color: #f5f5f5;
  padding: 1rem;
  border-radius: 8px;
}

.footer {
  display: flex;
  justify-content: space-between;
  padding: 1rem;
  background-color: #e1bee7;
  border-top: 1px solid #e0e0e0;
}

.footer-left,
.footer-center,
.footer-right {
  display: flex;
  flex-direction: column;
}

.bottom-toolbar {
  display: flex;
  justify-content: center;
  padding: 1rem;
  background-color: #ffffff;
  border-top: 1px solid #e0e0e0;
}

.bottom-toolbar .btn-icon {
  margin: 0 0.5rem;
}
</style>
