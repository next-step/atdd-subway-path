import { SET_STATIONS } from '@/store/shared/mutationTypes'
import { CREATE_STATION, DELETE_STATION, FETCH_STATIONS } from '@/store/shared/actionTypes'
import StationService from '@/api/modules/station'

const state = {
  stations: []
}

const getters = {
  stations(state) {
    return state.stations
  }
}

const mutations = {
  [SET_STATIONS](state, stations) {
    state.stations = stations
  }
}

const actions = {
  async [CREATE_STATION]({ commit }, newStationName) {
    return StationService.create(newStationName)
  },
  async [FETCH_STATIONS]({ commit }) {
    return StationService.getAll().then(({ data }) => {
      commit(SET_STATIONS, data)
    })
  },
  async [DELETE_STATION]({ commit }, stationId) {
    return StationService.delete(stationId)
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
