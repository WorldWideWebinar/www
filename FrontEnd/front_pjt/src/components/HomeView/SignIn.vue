<template>
  <div class="form-container sign-in-container">
    <form action="#">
      <h1 class="form-title">Sign in</h1>
      <input type="email" placeholder="Email" required />
      <input type="password" placeholder="Password" required />
      <a href="#">Forgot your password?</a>
      <button class="submit-btn">Sign In</button>
    </form>
  </div>
</template>

<script setup>
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
  flex-direction: column;
  padding: 20px;
  height: 100%;
  width: 100%;
  text-align: center;
  justify-content: space-between;
}

.form-title {
  margin-top: 10%;
}

input {
  background-color: #eee;
  border: none;
  padding: 12px 15px;
  margin: 8px 0;
  width: 100%;
  border-radius: 5px;
  font-size: 14px;
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
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.sign-in-container {
  left: 0;
  z-index: 2;
}

.submit-btn {
  margin-top: 5%;
}
</style>
