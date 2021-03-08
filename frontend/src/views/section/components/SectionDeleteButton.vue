<template>
  <v-btn @click="onDeleteLine" icon>
    <v-icon color="grey lighten-1">mdi-delete</v-icon>
  </v-btn>
</template>

<script>
import { mapActions, mapMutations } from 'vuex'
import { DELETE_SECTION, FETCH_LINE } from '@/store/shared/actionTypes'
import { SHOW_SNACKBAR } from '@/store/shared/mutationTypes'
import { SNACKBAR_MESSAGES } from '@/utils/constants'

export default {
  name: 'SectionDeleteButton',
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
    ...mapActions([DELETE_SECTION, FETCH_LINE]),
    async onDeleteLine() {
      try {
        await this.deleteSection({
          lineId: this.lineId,
          stationId: this.stationId
        })
        await this.fetchLine(this.lineId)
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.SUCCESS)
      } catch (e) {
        console.log(e);
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.FAIL)
      }
    }
  }
}
</script>
