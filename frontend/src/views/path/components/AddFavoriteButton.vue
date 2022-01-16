<template>
  <v-tooltip bottom>
    <template v-slot:activator="{ on, attrs }">
      <v-btn @click="onAddFavorite" v-bind="attrs" v-on="on" fab color="amber" class="absolute right-20 bottom-20">
        <v-icon>mdi-star-outline</v-icon>
      </v-btn>
    </template>
    <span>즐겨 찾기</span>
  </v-tooltip>
</template>

<script>
import { mapActions, mapMutations } from 'vuex'
import { SHOW_SNACKBAR } from '@/store/shared/mutationTypes'
import { SNACKBAR_MESSAGES } from '@/utils/constants'
import { CREATE_FAVORITE } from '@/store/shared/actionTypes'

export default {
  name: 'AddFavoriteButton',
  props: {
    path: {
      type: Object,
      required: true
    }
  },
  methods: {
    ...mapMutations([SHOW_SNACKBAR]),
    ...mapActions([CREATE_FAVORITE]),
    async onAddFavorite() {
      try {
        const { source, target } = this.path
        await this.createFavorite({ source, target })
        this.showSnackbar(SNACKBAR_MESSAGES.FAVORITE.ADD.SUCCESS)
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.FAVORITE.ADD.FAIL)
      }
    }
  }
}
</script>
