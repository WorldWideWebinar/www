import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniapluginPersistedstate from 'pinia-plugin-persistedstate'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min'
import '@/assets/styles/main.css'
import 'flatpickr/dist/flatpickr.css';
import App from './App.vue'
import router from './router'

import { library } from '@fortawesome/fontawesome-svg-core'
import { faSearch, fas } from '@fortawesome/free-solid-svg-icons' // 필요한 아이콘 가져오기
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

// Font Awesome 라이브러리에 아이콘 추가
library.add(fas, faSearch)

const pinia = createPinia()
const app = createApp(App)

pinia.use(piniapluginPersistedstate)

app.component('font-awesome-icon', FontAwesomeIcon)
app.use(pinia)
app.use(router)

app.mount('#app')
