<template>
  <Dialog :width="500" :close="close">
    <template slot="trigger">
      <v-btn @click="initEditingLine" icon>
        <v-icon color="grey lighten-1">mdi-pencil</v-icon>
      </v-btn>
    </template>
    <template slot="title">
      <div class="width-100 text-center mt-6">노선 수정</div>
    </template>
    <template slot="text">
      <v-form ref="lineEditForm" v-model="valid" @submit.prevent>
        <v-text-field
          v-model="lineEditForm.name"
          :rules="rules.line.name"
          color="grey darken-1"
          label="노선 이름"
          placeholder="노선 이름"
          outlined
        ></v-text-field>
        <div>
          <v-text-field
            v-model="lineEditForm.color"
            :rules="rules.line.color"
            :value="lineEditForm.color"
            label="노선 색상"
            filled
            disabled
          ></v-text-field>
          <p>
            노선의 색상을 아래 팔레트에서 선택해주세요.
          </p>
          <div class="d-flex">
            <div>
              <template v-for="(color, index) in lineColors">
                <v-btn :key="index" small class="color-button ma-1" depressed :color="color" @click="setLineColor(color)"></v-btn>
                <template v-if="index === 0"></template>
                <br v-if="index === 8 || index % 9 === 8" />
              </template>
            </div>
          </div>
        </div>
      </v-form>
    </template>
    <template slot="action">
      <v-btn :disabled="!valid" @click.prevent="onEditLine" color="amber">확인</v-btn>
    </template>
  </Dialog>
</template>

<script>
import { LINE_COLORS, SNACKBAR_MESSAGES } from '@/utils/constants'
import Dialog from '@/components/dialogs/Dialog'
import dialog from '@/mixins/dialog'
import validator from '@/utils/validator'
import { mapActions, mapMutations } from 'vuex'
import { SHOW_SNACKBAR } from '@/store/shared/mutationTypes'
import { EDIT_LINE, FETCH_LINES } from '@/store/shared/actionTypes'

export default {
  name: 'LineEditButton',
  props: {
    line: {
      type: Object,
      required: true
    }
  },
  components: { Dialog },
  mixins: [dialog],
  methods: {
    ...mapMutations([SHOW_SNACKBAR]),
    ...mapActions([EDIT_LINE, FETCH_LINES]),
    setLineColor(color) {
      this.lineEditForm.color = color
    },
    initEditingLine() {
      this.lineEditForm = { ...this.line }
    },
    async onEditLine() {
      try {
        await this.editLine({
          lineId: this.line.id,
          line: this.lineEditForm
        })
        await this.fetchLines()
        this.closeDialog()
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.SUCCESS)
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.FAIL)
      }
    }
  },
  data() {
    return {
      rules: { ...validator },
      lineEditForm: {
        name: '',
        color: '',
        startTime: '',
        endTime: '',
        intervalTime: '',
        extraFare: ''
      },
      valid: false,
      lineColors: [...LINE_COLORS]
    }
  }
}
</script>
<style lang="scss" scoped>
.color-button {
  min-width: 30px !important;
}
</style>
