<template>
  <Dialog :close="close">
    <template slot="trigger">
      <v-btn @click="initLineView" class="mx-2 absolute right-30 z-1 section-create-button" fab color="amber">
        <v-icon>mdi-plus</v-icon>
      </v-btn>
    </template>
    <template slot="title">
      <div class="width-100 text-center mt-6">구간 추가</div>
    </template>
    <template slot="text">
      <v-form ref="sectionForm" v-model="valid" @submit.prevent>
        <v-select
          v-model="sectionForm.lineId"
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
            v-model="sectionForm.upStationId"
            class="pr-5"
            :items="allStationsView"
            label="상행역"
            width="400"
            color="grey darken-1"
            item-color="amber darken-3"
            outlined
            dense
          ></v-select>
          <v-select
            v-model="sectionForm.downStationId"
            class="pl-5"
            :items="allStationsView"
            label="하행역"
            width="400"
            color="grey darken-1"
            item-color="amber darken-3"
            outlined
            dense
          ></v-select>
        </div>
        <div class="d-flex">
          <v-text-field
              v-model="sectionForm.distance"
              :rules="rules.section.distance"
              color="grey darken-1"
              label="거리"
              placeholder="거리"
              outlined
          ></v-text-field>
        </div>
      </v-form>
    </template>
    <template slot="action">
      <v-btn :disabled="!valid" @click.prevent="onCreateSection" color="amber">확인</v-btn>
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
import { CREATE_SECTION, CREATE_LINE, DELETE_LINE, EDIT_LINE, FETCH_LINE, FETCH_LINES, FETCH_STATIONS } from '@/store/shared/actionTypes'

export default {
  name: 'SectionCreateButton',
  mixins: [dialog],
  components: { Dialog },
  computed: {
    ...mapGetters(['lines', 'stations'])
  },
  methods: {
    ...mapMutations([SHOW_SNACKBAR]),
    ...mapActions([FETCH_LINES, CREATE_LINE, DELETE_LINE, EDIT_LINE, FETCH_LINE, FETCH_STATIONS, CREATE_SECTION]),
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
        this.selectedLine = await this.fetchLine(this.sectionForm.lineId)
        if (this.selectedLine.stations && this.selectedLine.stations.length > 0) {
          this.lineStationsNameViews = this.selectedLine.stations.map((station) => {
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
    isValid() {
      return this.$refs.sectionForm.validate()
    },
    onChangeLine() {
      this.initLineStationsView()
      this.initAllStationsView()
    },
    async onCreateSection() {
      if (!this.isValid()) {
        return
      }
      try {
        await this.createSection({
          lineId: this.selectedLine.id,
          section: this.sectionForm
        })
        this.closeDialog()
        this.fetchLines()
        this.fetchLine(this.selectedLine.id)
        this.$refs.sectionForm.resetValidation()
        this.initSectionForm()
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.SUCCESS)
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.FAIL)
      }
    },
    initSectionForm() {
      this.sectionForm = {
        lineId: '',
        upStationId: '',
        downStationId: '',
        distance: ''
      }
    }
  },
  data() {
    return {
      rules: { ...validator },
      sectionForm: {
        lineId: '',
        upStationId: '',
        downStationId: '',
        distance: ''
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
.section-create-button {
  bottom: -25px;
}
</style>
