<template>
  <div class="d-flex flex-column justify-center height-100vh-65px">
    <div class="d-flex justify-center relative">
      <v-card width="500" max-width="600" max-height="200" class="card-border">
        <v-card-title class="font-weight-bold justify-center">
          지하철 역 관리
        </v-card-title>
        <v-card-text>
          <v-form ref="stationForm" v-model="valid" @submit.prevent>
            <div class="d-flex">
              <v-text-field
                color="grey darken-1"
                class="mr-4"
                @keydown.enter="onCreateStation"
                label="지하철 역 이름을 입력해주세요."
                v-model="stationName"
                prepend-inner-icon="mdi-subway"
                dense
                outlined
                :rules="rules.stationName"
                autofocus
              ></v-text-field>
              <v-btn :disabled="!valid" color="amber" @click.prevent="onCreateStation">추가</v-btn>
            </div>
          </v-form>
        </v-card-text>
      </v-card>
    </div>
    <div class="d-flex justify-center relative mt-4">
      <v-card width="500" height="500px" class="overflow-y-auto pl-3">
        <v-list>
          <template v-for="station in stations">
            <v-list-item :key="station.name">
              <v-list-item-content>
                <v-list-item-title v-text="station.name"></v-list-item-title>
              </v-list-item-content>
              <v-list-item-action>
                <v-btn @click="onDeleteStation(station.id)" icon>
                  <v-icon color="grey lighten-1">mdi-delete</v-icon>
                </v-btn>
              </v-list-item-action>
            </v-list-item>
          </template>
        </v-list>
      </v-card>
    </div>
  </div>
</template>

<script>
import validator from '@/utils/validator'
import { CREATE_STATION, DELETE_STATION, FETCH_STATIONS } from '@/store/shared/actionTypes'
import { mapActions, mapGetters, mapMutations } from 'vuex'
import { SHOW_SNACKBAR } from '@/store/shared/mutationTypes'
import { SNACKBAR_MESSAGES } from '@/utils/constants'

export default {
  name: 'StationPage',
  created() {
    this.fetchStations()
  },
  computed: {
    ...mapGetters(['stations'])
  },
  methods: {
    ...mapMutations([SHOW_SNACKBAR]),
    ...mapActions([CREATE_STATION, FETCH_STATIONS, DELETE_STATION]),
    isValid() {
      return this.$refs.stationForm.validate()
    },
    async onCreateStation() {
      if (!this.isValid()) {
        return
      }
      try {
        await this.createStation({ name: this.stationName })
        await this.fetchStations()
        this.stationName = ''
        this.$refs.stationForm.resetValidation()
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.SUCCESS)
      } catch (e) {
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.FAIL)
      }
    },
    async onDeleteStation(stationId) {
      try {
        await this.deleteStation(stationId)
        await this.fetchStations()
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.SUCCESS)
      } catch (e) {
        console.log(e);
        this.showSnackbar(SNACKBAR_MESSAGES.COMMON.FAIL)
      }
    }
  },
  data() {
    return {
      rules: { ...validator },
      valid: false,
      stationName: ''
    }
  }
}
</script>

<style lang="scss">
.card-border {
  border-top: 8px solid #ffc107 !important;
}
</style>
