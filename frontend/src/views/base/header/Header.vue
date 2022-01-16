<template>
  <v-app-bar flat app color="amber" clipped-left :height="65">
    <v-toolbar-title class="pl-0 mr-12 align-center relative bottom-2">
      <v-btn to="/" text>
        <div class="title">
          <img class="header-logo relative top-5" src="/images/logo_small.png" />
          <span>RUNNINGMAP</span>
        </div>
      </v-btn>
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <v-btn v-for="navItem in navItems" :key="navItem._id" :to="navItem.link" text>{{ navItem.text }}</v-btn>
  </v-app-bar>
</template>

<script>
import ButtonMenu from '@/components/menus/ButtonMenu'
import LogoutButton from '@/views/base/header/components/LogoutButton'
import { mapGetters } from 'vuex'
import MyPageButton from '@/views/base/header/components/MyPageButton'
import FavoritesButton from '@/views/base/header/components/FavoritesButton'

export default {
  name: 'Header',
  components: { FavoritesButton, MyPageButton, LogoutButton, ButtonMenu },
  computed: {
    ...mapGetters(['member'])
  },
  data() {
    return {
      navItems: [
        {
          _id: 1,
          link: '/stations',
          text: '역 관리'
        },
        {
          _id: 2,
          link: '/lines',
          text: '노선 관리'
        },
        {
          _id: 3,
          link: '/sections',
          text: '구간 관리'
        }
      ]
    }
  }
}
</script>
<style lang="scss" scoped>
.header-logo {
  width: 25px;
}
</style>
