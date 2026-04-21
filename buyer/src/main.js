import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import Vant from 'vant'
import 'vant/lib/index.css'
import _axios, { axiosPlugin } from './plugins/axios'
import router from './router'
import store from './store'

const app = createApp(App)

app.use(Vant)
app.use(axiosPlugin)
app.use(router)
app.use(store)

app.mount('#app')
