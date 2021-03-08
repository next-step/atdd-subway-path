import { SET_MAP } from '@/store/shared/mutationTypes'
import { FETCH_MAP } from '@/store/shared/actionTypes'
import MapService from '@/api/modules/map'

const state = {
  map: {}
}

const getters = {
  map(state) {
    return state.map
  }
}

const mutations = {
  [SET_MAP](state, map) {
    state.map = map
  }
}

const actions = {
  async [FETCH_MAP]({ commit }, lineId) {
    return MapService.get(lineId).then(({ data: { lineResponses } }) => {
      commit(SET_MAP, lineResponses)
      return lineResponses
    })
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
