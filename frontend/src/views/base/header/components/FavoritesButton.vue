<template>
  <router-link to="favorites" class="text-dark">
    <MenuListItem>
      <v-icon class="mr-1 button">mdi-star</v-icon>
      <span class="relative top-1">즐겨찾기</span>
    </MenuListItem>
  </router-link>
</template>

<script>
import MenuListItem from '@/components/menus/MenuListItem'

export default {
  name: 'FavoritesButton',
  components: { MenuListItem }
}
</script>
