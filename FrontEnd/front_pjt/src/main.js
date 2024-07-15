import './assets/main.css'
import 'flatpickr/dist/flatpickr.css';
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniapluginPersistedstate from 'pinia-plugin-persistedstate'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min'
import '@/assets/styles/main.css'

import App from './App.vue'
import router from './router'

const pinia = createPinia()
const app = createApp(App)

pinia.use(piniapluginPersistedstate)
app.use(pinia)
app.use(router)

app.mount('#app')
