import Vue from 'vue'
import Vuex from 'vuex'
import station from '@/store/modules/station'
import line from '@/store/modules/line'
import snackbar from '@/store/modules/snackbar'
import map from '@/store/modules/map'
import path from '@/store/modules/path'
import member from '@/store/modules/member'
import auth from '@/store/modules/auth'
import favorite from '@/store/modules/favorite'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    station,
    line,
    snackbar,
    map,
    path,
    member,
    favorite,
    auth
  }
})
