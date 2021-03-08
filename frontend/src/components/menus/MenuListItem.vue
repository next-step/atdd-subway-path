<template>
  <v-list-item class="cursor-pointer">
    <v-list-item-title>
      <slot />
    </v-list-item-title>
  </v-list-item>
</template>

<script>
export default {
  name: 'MenuListItem'
}
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}
</style>
