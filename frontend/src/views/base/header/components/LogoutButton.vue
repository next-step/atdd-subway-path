<template>
  <MenuListItem>
    <div @click="logout">
      <v-icon class="mr-1 button">mdi-logout-variant</v-icon>
      <span class="relative top-1">로그아웃</span>
    </div>
  </MenuListItem>
</template>

<script>
import MenuListItem from '@/components/menus/MenuListItem'
import { SET_ACCESS_TOKEN, SET_MEMBER, SHOW_SNACKBAR } from '@/store/shared/mutationTypes'
import { mapMutations } from 'vuex'
import { SNACKBAR_MESSAGES } from '@/utils/constants'

export default {
  name: 'LogoutButton',
  components: { MenuListItem },
  methods: {
    ...mapMutations([SHOW_SNACKBAR, SET_ACCESS_TOKEN, SET_MEMBER]),
    logout() {
      try {
        localStorage.setItem('token', '')
        this.setAccessToken(null)
        this.setMember(null)
        this.$router.replace('/')
        this.showSnackbar(SNACKBAR_MESSAGES.LOGOUT.SUCCESS)
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.LOGOUT.FAIL)
        console.error(e)
      }
    }
  }
}
</script>
