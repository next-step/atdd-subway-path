<template>
  <v-btn @click="onDeleteFavorite" icon>
    <v-icon color="grey lighten-1">mdi-delete</v-icon>
  </v-btn>
</template>

<script>
import { mapActions, mapMutations } from 'vuex'
import { DELETE_FAVORITE, FETCH_FAVORITES } from '@/store/shared/actionTypes'
import { SHOW_SNACKBAR } from '@/store/shared/mutationTypes'
import { SNACKBAR_MESSAGES } from '@/utils/constants'

export default {
  name: 'FavoriteDeleteButton',
  props: {
    favorite: {
      type: Object,
      required: true
    }
  },
  methods: {
    ...mapActions([DELETE_FAVORITE, FETCH_FAVORITES]),
    ...mapMutations([SHOW_SNACKBAR]),
    async onDeleteFavorite() {
      try {
        await this.deleteFavorite(this.favorite.id)
        await this.fetchFavorites()
        this.showSnackbar(SNACKBAR_MESSAGES.FAVORITE.DELETE.SUCCESS)
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.FAVORITE.DELETE.FAIL)
      }
    }
  }
}
</script>
