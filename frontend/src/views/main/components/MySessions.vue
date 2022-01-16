<template>
  <div v-if="mySessions.length > 0" class="max-width-md mx-auto mb-12">
    <div class="desktop-view">
      <v-card elevation="2" class="px-5 pt-3 pb-2 my-session-cards-container">
        <MySessionsView :login-user="loginUser" :sessions="mySessions" />
      </v-card>
    </div>
    <div class="mobile-view">
      <MySessionsView :login-user="loginUser" :sessions="mySessions" />
    </div>
  </div>
</template>

<script>
import MySessionsView from '@/views/main/components/MySessionsView'
import { FETCH_MY_SESSIONS } from '@/store/shared/actionTypes'
import { mapActions, mapGetters } from 'vuex'

export default {
  name: 'MySessions',
  props: {
    loginUser: {
      type: Object,
      required: true
    }
  },
  components: {
    MySessionsView
  },
  computed: {
    ...mapGetters(['mySessions'])
  },
  created() {
    if (this.loginUser) {
      this.fetchMySessions()
    }
  },
  methods: {
    ...mapActions([FETCH_MY_SESSIONS])
  }
}
</script>
<style lang="scss" scoped>
.my-session-cards-container {
  border-top: 8px solid #333;
}
</style>
