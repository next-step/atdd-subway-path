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
    <v-btn v-if="!member" to="/login" text>로그인</v-btn>
    <ButtonMenu v-else>
      <template slot="button">
        <div class="text-normal cursor-pointer mx-2 my-thumbnail-button">
          <v-avatar dark width="35" height="35">
            <img src="https://avatars3.githubusercontent.com/u/4353846?v&amp;#x3D;4" />
          </v-avatar>
          <div class="desktop-view d-inline-block">
            <span>{{ member.email.split('@')[0] }}</span>
            <v-icon class="font-size-10" right>ti-angle-down</v-icon>
          </div>
        </div>
      </template>
      <template slot="items">
        <v-list class="py-0">
          <MyPageButton />
          <FavoritesButton />
          <v-divider class="ma-0" />
          <LogoutButton />
        </v-list>
      </template>
    </ButtonMenu>
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
        },
        {
          _id: 4,
          link: '/path',
          text: '경로 검색'
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
