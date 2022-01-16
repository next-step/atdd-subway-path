<template>
  <div class="d-flex flex-column justify-center height-100vh-65px">
    <div class="d-flex justify-center relative">
      <v-card width="500" max-width="600" max-height="600" class="card-border">
        <v-card-title class="font-weight-bold justify-center relative">
          즐겨 찾기
        </v-card-title>
        <v-card-text class="relative px-0 pb-0 line-list-container d-flex flex-column overflow-y-auto">
          <div class="mt-4 overflow-y-auto">
            <v-list-item-group>
              <template v-for="(favorite, index) in favorites">
                <v-list-item :key="index">
                  <v-list-item-content>
                    <v-list-item-title>
                      <v-chip class="ma-2" color="amber">
                        <v-avatar left>
                          <v-icon>mdi-subway</v-icon>
                        </v-avatar>
                        {{ favorite.source.name }}
                      </v-chip>
                      <v-icon>mdi-arrow-right-bold</v-icon>
                      <v-chip class="ma-2" color="amber">
                        <v-avatar left>
                          <v-icon>mdi-subway</v-icon>
                        </v-avatar>
                        {{ favorite.target.name }}
                      </v-chip>
                    </v-list-item-title>
                  </v-list-item-content>
                  <v-list-item-action class="flex-row">
                    <FavoriteDeleteButton :favorite="favorite" />
                  </v-list-item-action>
                </v-list-item>
                <v-divider v-if="index !== favorites.length - 1" />
              </template>
            </v-list-item-group>
          </div>
        </v-card-text>
      </v-card>
    </div>
  </div>
</template>

<script>
import FavoriteDeleteButton from '@/views/favorite/components/FavoriteDeleteButton'
import { FETCH_FAVORITES } from '@/store/shared/actionTypes'
import { SHOW_SNACKBAR } from '@/store/shared/mutationTypes'
import { mapActions, mapGetters, mapMutations } from 'vuex'
import { SNACKBAR_MESSAGES } from '@/utils/constants'

export default {
  name: 'Favorites',
  components: { FavoriteDeleteButton },
  computed: {
    ...mapGetters(['member', 'favorites'])
  },
  async created() {
    if (!this.member) {
      await this.$router.replace('/')
      return
    }
    try {
      await this.fetchFavorites()
    } catch (e) {
      this.showSnackbar(SNACKBAR_MESSAGES.FAVORITE.FETCH.FAIL)
    }
  },
  methods: {
    ...mapMutations([SHOW_SNACKBAR]),
    ...mapActions([FETCH_FAVORITES])
  }
}
</script>
<style lang="scss" scoped>
.line-list-container {
  max-height: calc(100% - 80px);
}
</style>
