import Vue from 'vue'
import vuetify from '@/utils/plugin/vuetify'
import router from '@/router'
import store from '@/store'
import App from './App.vue'
import '@/api'
import '@/styles/index.scss'

new Vue({
  el: '#app',
  vuetify,
  router,
  store,
  render: h => h(App)
})
