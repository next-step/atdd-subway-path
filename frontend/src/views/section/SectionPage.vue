<template>
  <div class="d-flex flex-column justify-center height-100vh-65px">
    <div class="d-flex justify-center relative">
      <v-card width="500" max-width="600" max-height="600" class="card-border">
        <v-card-title class="font-weight-bold justify-center relative">
          구간 관리
        </v-card-title>
        <v-card-text class="relative px-0 line-list-container d-flex flex-column">
          <div class="relative px-5 pb-5">
            <v-select
              v-model="activeLineId"
              :items="lineNamesViews"
              @change="onChangeLine"
              label="노선 선택"
              width="400"
              color="grey darken-1"
              item-color="amber darken-3"
              outlined
              dense
            ></v-select>
            <SectionCreateButton />
          </div>
          <v-divider />
          <div class="mt-10 overflow-y-auto">
            <v-card v-if="activeLine.id" class="mx-5" outlined>
              <v-toolbar :color="activeLine.color" dense flat>
                <v-toolbar-title>{{ activeLine.name }}</v-toolbar-title>
              </v-toolbar>
              <v-card-text class="overflow-y-auto py-0">
                <v-list dense class="max-height-300px">
                  <template v-if="activeLine.stations.length > 0">
                    <v-list-item v-for="(station, index) in activeLine.stations" :key="index">
                      <v-list-item-content>
                        <v-list-item-title v-text="station.name"></v-list-item-title>
                      </v-list-item-content>
                      <v-list-item-action class="flex-row">
                        <SectionDeleteButton :line-id="activeLine.id" :station-id="station.id" />
                      </v-list-item-action>
                    </v-list-item>
                  </template>
                  <template v-else>
                    <v-list-item>
                      <v-list-item-content>
                        <v-list-item-title>아직 추가된 역이 없습니다.</v-list-item-title>
                      </v-list-item-content>
                    </v-list-item>
                  </template>
                </v-list>
              </v-card-text>
            </v-card>
          </div>
        </v-card-text>
      </v-card>
    </div>
  </div>
</template>

<script>
import SectionCreateButton from '@/views/section/components/SectionCreateButton'
import { mapActions, mapGetters, mapMutations } from 'vuex'
import { FETCH_LINE, FETCH_LINES } from '@/store/shared/actionTypes'
import SectionDeleteButton from '@/views/section/components/SectionDeleteButton'
import { SNACKBAR_MESSAGES } from '@/utils/constants'
import { SHOW_SNACKBAR } from '@/store/shared/mutationTypes'

export default {
  name: 'SectionPage',
  components: { SectionDeleteButton, SectionCreateButton },
  created() {
    this.initLinesView()
  },
  computed: {
    ...mapGetters(['line', 'lines'])
  },
  watch: {
    line() {
      if (this.activeLine.id === this.line.id) {
        this.activeLine = { ...this.line }
      }
    }
  },
  methods: {
    ...mapMutations([SHOW_SNACKBAR]),
    ...mapActions([FETCH_LINES, FETCH_LINE]),
    async initLinesView() {
      try {
        await this.fetchLines()
        if (this.lines.length > 0) {
          this.lineNamesViews = this.lines.map(line => {
            return {
              text: line.name,
              value: line.id
            }
          })
        }
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.FAIL)
      }
    },
    async onChangeLine() {
      try {
        this.activeLine = await this.fetchLine(this.activeLineId)
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.FAIL)
      }
    }
  },
  data() {
    return {
      lineNamesViews: [],
      activeLineId: {},
      activeLine: {}
    }
  }
}
</script>
