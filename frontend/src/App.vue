<template>
  <v-app v-cloak>
    <Header />
    <v-main>
      <router-view />
    </v-main>
    <Snackbar />
  </v-app>
</template>

<script>
import Header from '@/views/base/header/Header'
import Snackbar from '@/components/snackbars/Snackbar'
import { FETCH_MEMBER } from '@/store/shared/actionTypes'
import { mapActions } from 'vuex'

export default {
  name: 'App',
  components: {
    Header,
    Snackbar
  },
  created() {
    const accessToken = localStorage.getItem('token')
    if (!accessToken) {
      return
    }
    this.fetchMember()
  },
  methods: {
    ...mapActions([FETCH_MEMBER])
  }
}
</script>
