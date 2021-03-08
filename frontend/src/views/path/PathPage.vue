<template>
  <div class="d-flex flex-column justify-center height-100vh-65px">
    <div class="d-flex justify-center relative">
      <v-card width="500" max-width="600" max-height="600" class="card-border">
        <v-card-title class="font-weight-bold justify-center relative">
          경로 검색
        </v-card-title>
        <v-card-text class="relative mt-2 px-0 line-list-container d-flex flex-column">
          <div class="px-4 pb-6">
            <div class="d-flex width-100">
              <v-select
                v-model="path.source"
                class="pr-4 path-station-select"
                :items="allStationsView"
                label="출발역"
                color="grey darken-1"
                item-color="amber darken-3"
                outlined
                dense
              ></v-select>
              <v-icon class="relative bottom-15">mdi-arrow-right-bold</v-icon>
              <v-select
                v-model="path.target"
                class="pl-4 path-station-select"
                :items="allStationsView"
                label="도착역"
                color="grey darken-1"
                item-color="amber darken-3"
                outlined
                dense
              ></v-select>
            </div>
            <div class="d-flex mb-4">
              <v-btn @click="onSearchResult" color="amber" class="width-100" depressed>검색</v-btn>
            </div>
            <v-divider v-if="pathResult" />
            <div v-if="pathResult" class="d-flex justify-center mt-4">
              <v-card width="400" flat>
                <v-tabs v-model="tab" background-color="transparent" color="amber" grow>
                  <v-tab>최단 거리</v-tab>
                </v-tabs>
                <v-tabs-items v-model="tab">
                  <v-tab-item>
                    <v-simple-table>
                      <template v-slot:default>
                        <thead>
                        <tr>
                          <th>거리</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                          <td>{{ pathResult.distance }}km</td>
                        </tr>
                        </tbody>
                      </template>
                    </v-simple-table>
                  </v-tab-item>
                  <v-tab-item>
                    <v-simple-table>
                      <template v-slot:default>
                        <thead>
                        <tr>
                          <th>거리</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                          <td>{{ pathResult.distance }}km</td>
                        </tr>
                        </tbody>
                      </template>
                    </v-simple-table>
                  </v-tab-item>
                </v-tabs-items>
              </v-card>
            </div>
            <v-divider />
            <div v-if="pathResult" class="d-flex justify-center mt-4">
              <v-card width="400" flat>
                <template v-for="(station, index) in pathResult.stations">
                  <span :key="station.id">
                    <v-chip :key="index" class="ma-2" :color="index === 0 || index === pathResult.stations.length - 1 ? 'amber' : ''">
                      <v-avatar v-if="index === 0 || index === pathResult.stations.length - 1" left>
                        <v-icon>mdi-subway</v-icon>
                      </v-avatar>
                      {{ station.name }}
                    </v-chip>
                    <v-icon v-if="index < pathResult.stations.length - 1">mdi-arrow-right-bold</v-icon>
                  </span>
                </template>
              </v-card>
              <AddFavoriteButton :path="path" />
            </div>
          </div>
        </v-card-text>
      </v-card>
    </div>
  </div>
</template>

<script>
  import validator from '@/utils/validator'
  import { mapActions, mapGetters, mapMutations } from 'vuex'
  import { SHOW_SNACKBAR } from '@/store/shared/mutationTypes'
  import { SNACKBAR_MESSAGES } from '@/utils/constants'
  import { FETCH_STATIONS, SEARCH_PATH } from '@/store/shared/actionTypes'
  import AddFavoriteButton from '@/views/path/components/AddFavoriteButton'

  export default {
    name: 'PathPage',
    components: { AddFavoriteButton },
    computed: {
      ...mapGetters(['stations', 'pathResult'])
    },
    created() {
      this.initAllStationsView()
    },
    methods: {
      ...mapMutations([SHOW_SNACKBAR]),
      ...mapActions([SEARCH_PATH, FETCH_STATIONS]),
      async onSearchResult() {
        try {
          await this.searchPath(this.path)
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
      }
    },
    data() {
      return {
        path: {
          source: '',
          target: ''
        },
        allStationsView: [],
        rules: { ...validator },
        tab: null
      }
    }
  }
</script>
<style scoped>
.path-station-select {
  width: 200px;
}
</style>