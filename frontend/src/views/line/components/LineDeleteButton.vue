<template>
  <v-btn @click="onDeleteLine" icon>
    <v-icon color="grey lighten-1">mdi-delete</v-icon>
  </v-btn>
</template>

<script>
import { mapActions, mapMutations } from 'vuex'
import { DELETE_LINE, FETCH_LINES } from '@/store/shared/actionTypes'
import { SNACKBAR_MESSAGES } from '@/utils/constants'
import { SHOW_SNACKBAR } from '@/store/shared/mutationTypes'

export default {
  name: 'LineDeleteButton',
  props: {
    line: {
      type: Object,
      required: true
    }
  },
  methods: {
    ...mapActions([DELETE_LINE, FETCH_LINES]),
    ...mapMutations([SHOW_SNACKBAR]),
    async onDeleteLine() {
      try {
        await this.deleteLine(this.line.id)
        await this.fetchLines()
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.SUCCESS)
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.FAIL)
      }
    }
  }
}
</script>

<style scoped></style>
