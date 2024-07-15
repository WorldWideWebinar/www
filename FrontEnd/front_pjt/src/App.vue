<template>
  <div id="app" class="d-flex">
    <aside class="sidebar d-flex flex-column">
      <button class="btn btn-add">
        <span>+</span>
      </button>
      <ul class="nav flex-column">
        <li class="nav-item">
          <RouterLink class="nav-link" :to="{ name: 'rnd' }" active-class="active">
            <span class="icon">🚀</span>
            <span class="link-text">R&D</span>
          </RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink class="nav-link" :to="{ name: 'dev' }" active-class="active">
            <span class="icon">💻</span>
            <span class="link-text">개발</span>
          </RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink class="nav-link" :to="{ name: 'purchase' }" active-class="active">
            <span class="icon">💼</span>
            <span class="link-text">구매</span>
          </RouterLink>
        </li>
        <li class="nav-item">
          <RouterLink class="nav-link" :to="{ name: 'sales' }" active-class="active">
            <span class="icon">📈</span>
            <span class="link-text">영업</span>
          </RouterLink>
        </li>
      </ul>
      <div class="spacer"></div>
      <ul class="nav flex-column">
        <li class="nav-item">
          <button class="btn btn-icon">
            <span class="icon">📢</span>
          </button>
        </li>
        <li class="nav-item">
          <button class="btn btn-icon">
            <span class="icon">❔</span>
          </button>
        </li>
      </ul>
    </aside>
    <main class="flex-grow-1">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<style scoped>
header {
  line-height: 1.5;
  max-height: 100vh;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

nav {
  width: 100%;
  font-size: 12px;
  text-align: center;
  margin-top: 2rem;
}

nav a.router-link-exact-active {
  color: var(--color-text);
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a {
  display: inline-block;
  padding: 0 1rem;
  border-left: 1px solid var(--color-border);
}

nav a:first-of-type {
  border: 0;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }

  nav {
    text-align: left;
    margin-left: -1rem;
    font-size: 1rem;

    padding: 1rem 0;
    margin-top: 1rem;
  }
}
</style>
