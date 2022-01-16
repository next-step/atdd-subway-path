<template>
  <v-form ref="lineForm" v-model="isValidLine" @submit.prevent>
    <v-text-field
      v-model="lineForm.name"
      :rules="rules.line.name"
      color="grey darken-1"
      label="노선 이름"
      placeholder="노선 이름"
      outlined
    ></v-text-field>
    <v-row>
      <v-col cols="4">
        <v-text-field
          v-model="lineForm.startTime"
          :rules="rules.line.startTime"
          color="grey darken-1"
          label="첫차 시간"
          placeholder="첫차 시간"
          outlined
        ></v-text-field>
      </v-col>
      <v-col cols="4">
        <v-text-field
          v-model="lineForm.endTime"
          :rules="rules.line.endTime"
          color="grey darken-1"
          label="막차 시간"
          placeholder="막차 시간"
          outlined
        ></v-text-field>
      </v-col>
      <v-col cols="4">
        <v-text-field
          v-model="lineForm.intervalTime"
          :rules="rules.line.intervalTime"
          color="grey darken-1"
          label="간격"
          placeholder="간격"
          outlined
        ></v-text-field>
      </v-col>
    </v-row>
    <div>
      <v-text-field v-model="lineForm.color" :rules="rules.line.color" :value="lineForm.color" label="노선 색상" filled disabled></v-text-field>
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

<script>
import validator from '@/utils/validator'
import { mapActions, mapMutations } from 'vuex'
import { SHOW_SNACKBAR } from '@/store/shared/mutationTypes'
import { CREATE_LINE, DELETE_LINE, EDIT_LINE, FETCH_LINES } from '@/store/shared/actionTypes'
import { LINE_COLORS } from '@/utils/constants'

export default {
  name: 'LineForm',
  props: {
    line: {
      type: Object,
      required: false
    },
    valid: {
      type: Boolean,
      required: true
    }
  },
  watch: {
    valid() {
      this.isValidLine = this.valid
    }
  },
  methods: {
    ...mapMutations([SHOW_SNACKBAR]),
    ...mapActions([FETCH_LINES, CREATE_LINE, DELETE_LINE, EDIT_LINE]),
    setLineColor(color) {
      this.lineForm.color = color
    }
  },
  data() {
    return {
      rules: { ...validator },
      lineForm: {
        name: '',
        color: '',
        startTime: '',
        endTime: '',
        intervalTime: ''
      },
      isValidLine: false,
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
