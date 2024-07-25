<template>
  <div class="form-container sign-up-container">
    <form @submit.prevent="handleSignUp">
      <h1>Create Account</h1>
      <input v-model="name" type="text" placeholder="Name" required />
      <input v-model="id" type="text" placeholder="ID" required />
      <input v-model="email" type="email" placeholder="Email" required />
      <input v-model="password" type="password" placeholder="Password" required />
      <input v-model="passwordConfirmation" type="password" placeholder="Password Confirmation" required />
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      <select v-model="selectedLanguage" @change="changeLanguage">
        <option value="en">🇺🇸 English</option>
        <option value="ko">🇰🇷 한국어</option>
        <option value="ja">🇯🇵 日本語</option>
        <option value="zh">🇨🇳 中国语</option>
        <option value="es">🇪🇸 Español</option>
      </select>
      <button type="submit">Sign Up</button>
    </form>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useUserStore } from '@/stores/userStore';

const userStore = useUserStore();

const name = ref('');
const id = ref('');
const email = ref('');
const password = ref('');
const passwordConfirmation = ref('');
const selectedLanguage = ref('en');
const errorMessage = ref('');

function changeLanguage(event) {
  selectedLanguage.value = event.target.value;
}

watch(passwordConfirmation, (newVal) => {
  if (newVal) {
    if (password.value !== newVal) {
      errorMessage.value = 'Passwords do not match';
    } else {
      errorMessage.value = '';
    }
  }
});

async function handleSignUp() {
  if (password.value !== passwordConfirmation.value) {
    errorMessage.value = 'Passwords do not match';
    return;
  }

  const signUpData = {
    name: name.value,
    id: id.value,
    idCheck: true,
    email: email.value,
    password: password.value,
    language: selectedLanguage.value
  };

  const result = await userStore.signUp(signUpData);
  if (result.isSuccess) {
    alert('Sign up successful');
  } else {
    errorMessage.value = `Sign up failed: ${result.message}`;
  }
}
</script>

<style scoped>
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
  margin-bottom: 20px;
}

a {
  color: #333;
  font-size: 14px;
  text-decoration: none;
  margin: 15px 0;
}

button {
  border-radius: 20px;
  border: 1px solid #6a1b9a;
  background-color: #6a1b9a;
  color: #FFFFFF;
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

form {
  background-color: #FFFFFF;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 0 50px;
  height: 100%;
  width: 100%;
  text-align: center;
}

input, select {
  background-color: #eee;
  border: none;
  padding: 12px 15px;
  margin: 8px 0;
  width: 100%;
  border-radius: 5px;
  font-size: 14px;
}

select {
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  background: #eee url("data:image/svg+xml;utf8,<svg fill='%23000000' height='24' viewBox='0 0 24 24' width='24' xmlns='http://www.w3.org/2000/svg'><path d='M7 10l5 5 5-5z'/></svg>") no-repeat right 10px center;
  padding-right: 40px; /* Adjust for the dropdown icon */
}

.container {
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 14px 28px rgba(0,0,0,0.25), 
      0 10px 10px rgba(0,0,0,0.22);
  position: relative;
  width: 1000px; /* Increased width */
  max-width: 100%;
  min-height: 600px; /* Increased height */
}

.form-container {
  position: absolute;
  top: 0;
  height: 100%;
  width: 50%; /* Ensures equal width for both containers */
  transition: all 0.6s ease-in-out;
}

.sign-up-container {
  left: 0;
  z-index: 2;
}

.error-message {
  color: red;
  margin: 10px 0;
  font-size: 12px;
}
</style>
