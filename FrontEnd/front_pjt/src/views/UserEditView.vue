<template>
  <div class="container-wrapper">
    <div class="user-edit-container">
      <span class="container-title">Edit Profile</span>
      <form @submit.prevent="handleSubmit">
        <div class="form-content">
          <div class="profile-image-container">
            <img :src="image" alt="Profile Image" class="profile-image" />
            <button type="button" class="edit-image-btn" @click="selectImage">Edit</button>
            <input type="file" ref="fileInput" @change="handleImageChange" style="display: none;" />
          </div>
          <div class="user-info-container">
            <table class="user-info-table">
              <tr>
                <td class="label">Name</td>
                <td>{{ userInfo.name }}</td>
              </tr>
              <!-- <tr>
                <td class="label">ID</td>
                <td>{{ userInfo.userId }}</td>
              </tr> -->
              <tr>
                <td class="label">Email</td>
                <td><input v-model="email" type="email" placeholder="Email" required /></td>
              </tr>
              <tr>
                <td class="label">Language</td>
                <td>
                  <select v-model="selectedLanguage" @change="changeLanguage">
                    <option value="en">🇺🇸 English</option>
                    <option value="ko">🇰🇷 한국어</option>
                    <option value="ja">🇯🇵 日本語</option>
                    <option value="zh">🇨🇳 中国语</option>
                    <option value="es">🇪🇸 Español</option>
                  </select>
                </td>
              </tr>
            </table>
            <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
          </div>
        </div>
        <button class="submit-btn" type="submit">Update</button>
      </form>

      <div v-if="showPasswordModal" class="password-modal">
        <div class="modal-content">
          <span class="modal-title">Confirm Password</span>
          <input v-model="passwordInput" type="password" placeholder="Enter your password" />
          <button @click="verifyPassword" class="verify-btn">Verify</button>
          <button @click="cancelPasswordVerification" class="cancel-btn">Cancel</button>
          <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import axiosInstance from '@/axios';
import { useErrorStore } from '@/stores/errorStore';

const router = useRouter();
const userStore = useUserStore();

const email = ref('');
const image = ref('');
const selectedLanguage = ref('en');
const errorMessage = ref('');
const userInfo = ref({});
const fileInput = ref(null);
const passwordInput = ref('')
const showPasswordModal = ref(false);
const isPasswordVerified = ref(false);
onMounted(() => {
  userInfo.value = userStore.userInfo;
  email.value = userInfo.value.email;
  image.value = userInfo.value.profileImageUrl;
  selectedLanguage.value = userInfo.value.language || 'en';
});

function changeLanguage(event) {
  selectedLanguage.value = event.target.value;
}

function selectImage() {
  fileInput.value.click();
}

async function handleImageChange(event) {
  console.log("Image change triggered"); // 파일 선택 시 로그 확인
  const file = event.target.files[0];
  if (file) {
    console.log("File selected:", file.name); // 파일 이름 로그 확인
    const formData = new FormData();
    formData.append('image', file);

    try {
      const response = await axiosInstance.post('/api/images', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      if (response.data) {
        image.value = response.data.result;
        console.log("Image uploaded successfully:", image.value); // 업로드 성공 로그
      } else {
        throw new Error(response.data.message || 'Image upload failed');
      }
    } catch (error) {
      console.error('Image upload failed:', error);
      errorMessage.value = 'Failed to upload image. Please try again.';
    }
  }
}

async function handleSubmit() {
  if (!isPasswordVerified.value) {
    showPasswordModal.value = true;
  } else {
    await handleUserUpdate();
  }
}

async function verifyPassword() {
  const result = await axiosInstance.post(`api/users/checkPassword`, {password: passwordInput.value});
  console.log(result)
  if (result.data.success) {
    isPasswordVerified.value = true;
    showPasswordModal.value = false;
    await handleUserUpdate();
  } else {
    errorMessage.value = 'Password verification failed. Please try again.';
  }
}

async function handleUserUpdate() {
  const updatedInfo = {
    id: userInfo.value.id,
    name: userInfo.value.name,
    password: passwordInput.value,
    email: email.value,
    profileImageUrl: image.value,
    language: selectedLanguage.value
  };
  const errorStore = useErrorStore();
  const result = await userStore.changeUserInfo(userStore.userId, updatedInfo);
  if (result) {
    errorStore.showError('Profile updated successfully');
    router.push({ name: 'ProfileView' });
  } else {
    errorMessage.value = `Update failed: ${result.message}`;
  }
}
</script>

<style scoped>
body {
  background: #f6f5f7;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  margin: 0;
}

.container-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 700px;
  margin: auto;
}

.user-edit-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: auto;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22);
  position: relative;
  overflow: hidden;
  width: 900px;
  max-width: 100%;
  height: 600px;
  padding: 20px;
}

.container-title {
  font-weight: bolder;
  font-size: xx-large;
  margin: 10px auto;
  border-bottom: 2px dashed rgb(220, 193, 246);
}

form {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  text-align: center;
  justify-content: space-between;
  margin: auto;
}

.form-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  width: 100%;
  flex-grow: 1;
  margin: 80px auto 20px auto;
  border-bottom: 2px dashed rgb(220, 193, 246);
}

/* 이미지 */
.profile-image-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 37%;
}

.profile-image {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 10px;
}

.edit-image-btn {
  font-size: 12px;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 20px;
  background-color: #6a1b9a;
  font-weight: bold;
  padding: 8px 20px;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: transform 80ms ease-in;
}

.edit-image-btn:hover {
  background-color: #b380bc;
}

.edit-image-btn:active {
  transform: scale(0.95);
}

/* 기타 정보 */
.user-info-container {
  flex-grow: 1;
}

.user-info-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
  background-color: #eee;
  border-radius: 10px;
}

.user-info-table .label {
  text-align: left;
  padding-left: 30px;
  font-weight: bold;
  width: 25%;
}

.user-info-table td {
  height: 40px;
  padding: 15px;
  padding-right: 30px;
}

.user-info-table td input,
.user-info-table td select {
  width: 100%;
}

input, select {
  background-color: #b3b2b2;
  border: none;
  padding: 12px 15px;
  margin: 0;
  width: 100%;
  border-radius: 5px;
  font-size: 14px;
  height: 40px;
}

select {
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  background: #b3b2b2 url("data:image/svg+xml;utf8,<svg fill='%23000000' height='24' viewBox='0 0 24 24' width='24' xmlns='http://www.w3.org/2000/svg'><path d='M7 10l5 5 5-5z'/></svg>") no-repeat right 10px center;
  padding-right: 40px; /* Adjust for the dropdown icon */
}

.error-message {
  color: red;
  margin: 10px 0;
  font-size: 12px;
}

.submit-btn {
  font-size: 12px;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 20px;
  background-color: #6a1b9a;
  font-weight: bold;
  padding: 12px 45px;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: transform 80ms ease-in;
  width: 40%;
  margin: 0px auto;
}

.submit-btn:hover {
  background-color: #b380bc;
}

.submit-btn:active {
  transform: scale(0.95);
}

.submit-btn:focus {
  outline: none;
}

/* 비밀번호 체크 모달 */
.password-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7); /* 어두운 배경으로 변경 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000; /* 모달을 최상단에 배치 */
}

.modal-content {
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  max-width: 400px;
  width: 100%;
}

.modal-title {
  font-size: 20px;
  font-weight: bold;
  color: #6a1b9a;
  margin-bottom: 20px;
}

input[type="password"] {
  background-color: #f1f1f1;
  border: 1px solid #ccc;
  padding: 10px;
  width: 100%;
  margin-bottom: 20px;
  border-radius: 5px;
}

.verify-btn,
.cancel-btn {
  background-color: #6a1b9a;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  margin: 5px;
  transition: background-color 0.3s ease;
}

.verify-btn:hover,
.cancel-btn:hover {
  background-color: #b380bc;
}

@media (max-width: 992px) {
  .container-wrapper {
    width: 100%;
    margin: auto;
  }

  .user-edit-container {
    width: 80%;
    height: 100%;
    margin: auto;
  }

  .form-content {
    flex-direction: column;
    align-items: center;
    margin: 20px auto;
  }

  .profile-image-container {
    margin-bottom: 20px;
    margin-right: 0;
  }
}
</style>
