<template>
  <Dialog :close="close">
    <template slot="trigger">
      <v-btn @click="initLineView" class="mx-2 absolute right-30 z-1 edge-create-button" fab color="amber">
        <v-icon>mdi-plus</v-icon>
      </v-btn>
    </template>
    <template slot="title">
      <div class="width-100 text-center mt-6">구간 추가</div>
    </template>
    <template slot="text">
      <v-form ref="edgeForm" v-model="valid" @submit.prevent>
        <v-select
          v-model="edgeForm.lineId"
          :items="lineNameViews"
          @change="onChangeLine"
          label="노선 선택"
          width="400"
          item-color="amber darken-3"
          color="grey darken-1"
          outlined
          dense
        ></v-select>
        <div class="d-flex">
          <v-select
            v-model="edgeForm.preStationId"
            class="pr-4"
            :items="lineStationsNameViews"
            label="이전역"
            width="400"
            color="grey darken-1"
            item-color="amber darken-3"
            outlined
            dense
          ></v-select>
          <v-icon class="relative bottom-15">mdi-arrow-left-right-bold</v-icon>
          <v-select
            v-model="edgeForm.stationId"
            class="pl-4"
            :items="allStationsView"
            label="대상역"
            width="400"
            color="grey darken-1"
            item-color="amber darken-3"
            outlined
            dense
          ></v-select>
        </div>
        <div class="d-flex">
          <v-text-field
            v-model="edgeForm.distance"
            :rules="rules.edge.distance"
            color="grey darken-1"
            class="pr-7"
            label="거리"
            placeholder="거리"
            outlined
          ></v-text-field>
          <v-text-field
            v-model="edgeForm.duration"
            :rules="rules.edge.duration"
            color="grey darken-1"
            label="연결 시간"
            class="pl-7"
            placeholder="연결 시간"
            outlined
          ></v-text-field>
        </div>
      </v-form>
    </template>
    <template slot="action">
      <v-btn :disabled="!valid" @click.prevent="onCreateEdge" color="amber">확인</v-btn>
    </template>
  </Dialog>
</template>

<script>
import Dialog from '@/components/dialogs/Dialog'
import { SNACKBAR_MESSAGES } from '@/utils/constants'
import dialog from '@/mixins/dialog'
import validator from '@/utils/validator'
import { mapActions, mapGetters, mapMutations } from 'vuex'
import { SHOW_SNACKBAR } from '@/store/shared/mutationTypes'
import { CREATE_EDGE, CREATE_LINE, DELETE_LINE, EDIT_LINE, FETCH_LINE, FETCH_LINES, FETCH_STATIONS } from '@/store/shared/actionTypes'

export default {
  name: 'EdgeCreateButton',
  mixins: [dialog],
  components: { Dialog },
  computed: {
    ...mapGetters(['lines', 'stations'])
  },
  methods: {
    ...mapMutations([SHOW_SNACKBAR]),
    ...mapActions([FETCH_LINES, CREATE_LINE, DELETE_LINE, EDIT_LINE, FETCH_LINE, FETCH_STATIONS, CREATE_EDGE]),
    initLineView() {
      if (this.lines.length > 0) {
        this.lineNameViews = this.lines.map(line => {
          return {
            text: line.name,
            value: line.id
          }
        })
      }
    },
    async initLineStationsView() {
      try {
        this.selectedLine = await this.fetchLine(this.edgeForm.lineId)
        if (this.selectedLine.stations && this.selectedLine.stations.length > 0) {
          this.lineStationsNameViews = this.selectedLine.stations.map(({ station }) => {
            return {
              text: station.name,
              value: station.id
            }
          })
        }
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.FAIL)
      }
    },
    async initAllStationsView() {
      try {
        await this.fetchStations()
        if (this.stations.length > 0) {
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
    },
    setLineColor(color) {
      this.edgeForm.color = color
    },
    isValid() {
      return this.$refs.edgeForm.validate()
    },
    onChangeLine() {
      this.initLineStationsView()
      this.initAllStationsView()
    },
    async onCreateEdge() {
      if (!this.isValid()) {
        return
      }
      try {
        await this.createEdge({
          lineId: this.selectedLine.id,
          edge: this.edgeForm
        })
        this.closeDialog()
        this.fetchLines()
        this.fetchLine(this.selectedLine.id)
        this.$refs.edgeForm.resetValidation()
        this.initEdgeForm()
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.SUCCESS)
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.FAIL)
      }
    },
    initEdgeForm() {
      this.edgeForm = {
        lineId: '',
        preStationId: '',
        stationId: '',
        distance: '',
        duration: ''
      }
    }
  },
  data() {
    return {
      rules: { ...validator },
      edgeForm: {
        lineId: '',
        preStationId: '',
        stationId: '',
        distance: '',
        duration: ''
      },
      selectedLine: {},
      lineStationsNameViews: [],
      allStationsView: [],
      lineNameViews: [],
      valid: false
    }
  }
}
</script>

<style lange="scss" scoped>
.edge-create-button {
  bottom: -25px;
}
</style>
