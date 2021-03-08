<template>
  <MenuListItem>
    <router-link to="mypage" class="text-dark">
      <v-icon class="mr-1 button">mdi-account</v-icon>
      <span class="relative top-1">나의 페이지</span>
    </router-link>
  </MenuListItem>
</template>

<script>
import MenuListItem from '@/components/menus/MenuListItem'

export default {
  name: 'MyPageButton',
  components: { MenuListItem }
}
</script>
