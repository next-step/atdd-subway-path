<template>
  <Dialog :width="500" :close="close">
    <template slot="trigger">
      <v-btn @click="initAllStationsView" class="mx-2 absolute right-30 z-1 line-create-button z-10" fab color="amber">
        <v-icon>mdi-plus</v-icon>
      </v-btn>
    </template>
    <template slot="title">
      <div class="width-100 text-center mt-6">노선 생성</div>
    </template>
    <template slot="text">
      <v-form ref="lineForm" v-model="valid" @submit.prevent>
        <v-text-field
          v-model="lineForm.name"
          :rules="rules.line.name"
          color="grey darken-1"
          label="노선 이름"
          placeholder="노선 이름"
          outlined
        ></v-text-field>
        <div class="d-flex">
          <v-select
              v-model="lineForm.upStationId"
              class="pr-4"
              :items="allStationsView"
              label="상행 종점"
              width="400"
              color="grey darken-1"
              item-color="amber darken-3"
              outlined
              dense
          ></v-select>
          <v-icon class="relative bottom-15">mdi-arrow-left-right-bold</v-icon>
          <v-select
              v-model="lineForm.downStationId"
              class="pl-4"
              :items="allStationsView"
              label="하행 종점"
              width="400"
              color="grey darken-1"
              item-color="amber darken-3"
              outlined
              dense
          ></v-select>
        </div>
        <div class="d-flex">
          <v-text-field
              v-model="lineForm.distance"
              :rules="rules.section.distance"
              color="grey darken-1"
              label="거리"
              placeholder="거리"
              outlined
          ></v-text-field>
        </div>
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
    <template slot="action">
      <v-btn :disabled="!valid" @click.prevent="onCreateLine" color="amber">확인</v-btn>
    </template>
  </Dialog>
</template>

<script>
import Dialog from '@/components/dialogs/Dialog'
import { LINE_COLORS, SNACKBAR_MESSAGES } from '@/utils/constants'
import dialog from '@/mixins/dialog'
import validator from '@/utils/validator'
import {mapActions, mapGetters, mapMutations} from 'vuex'
import { SHOW_SNACKBAR } from '@/store/shared/mutationTypes'
import { CREATE_LINE, DELETE_LINE, EDIT_LINE, FETCH_LINES, FETCH_STATIONS } from '@/store/shared/actionTypes'

export default {
  name: 'LineCreateButton',
  mixins: [dialog],
  components: { Dialog },
  computed: {
    ...mapGetters(['stations'])
  },
  methods: {
    ...mapMutations([SHOW_SNACKBAR]),
    ...mapActions([FETCH_LINES, CREATE_LINE, DELETE_LINE, EDIT_LINE, FETCH_STATIONS]),
    setLineColor(color) {
      this.lineForm.color = color
    },
    isValid() {
      return this.$refs.lineForm.validate()
    },
    async onCreateLine() {
      if (!this.isValid()) {
        return
      }
      try {
        await this.createLine(this.lineForm)
        await this.fetchLines()
        this.lineForm = {
          name: '',
          color: '',
          upStationId: '',
          downStationId: '',
          distance: ''
        }
        this.$refs.lineForm.resetValidation()
        this.closeDialog()
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.SUCCESS)
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.FAIL)
      }
    },
    async initAllStationsView() {
      try {
        await this.fetchStations()
        if (this.stations.length > 0) {
          console.log(this.stations);
          this.allStationsView = this.stations.map(station => {
            return {
              text: station.name,
              value: station.id
            }
          })
        }
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.FAIL)
      }
    }
  },
  data() {
    return {
      rules: { ...validator },
      isOption: true,
      lineForm: {
        name: '',
        color: '',
        upStationId: '',
        downStationId: '',
        distance: ''
      },
      valid: false,
      lineColors: [...LINE_COLORS],
      allStationsView: []
    }
  }
}
</script>

<style lang="scss" scoped>
.color-button {
  min-width: 30px !important;
}

.line-create-button {
  bottom: -25px;
}
</style>
