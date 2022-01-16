<template>
  <v-btn @click="onDeleteLine" icon>
    <v-icon color="grey lighten-1">mdi-delete</v-icon>
  </v-btn>
</template>

<script>
import { mapActions, mapMutations } from 'vuex'
import { DELETE_LINE_STATION, FETCH_LINE } from '@/store/shared/actionTypes'
import { SHOW_SNACKBAR } from '@/store/shared/mutationTypes'
import { SNACKBAR_MESSAGES } from '@/utils/constants'

export default {
  name: 'EdgeDeleteButton',
  props: {
    lineId: {
      type: Number,
      required: true
    },
    stationId: {
      type: Number,
      required: true
    }
  },
  methods: {
    ...mapMutations([SHOW_SNACKBAR]),
    ...mapActions([DELETE_LINE_STATION, FETCH_LINE]),
    async onDeleteLine(e) {
      e.preventDefault()
      try {
        await this.deleteLineStation({
          lineId: this.lineId,
          stationId: this.stationId
        })
        await this.fetchLine(this.lineId)
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.SUCCESS)
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.FAIL)
      }
    }
  }
}
</script>
