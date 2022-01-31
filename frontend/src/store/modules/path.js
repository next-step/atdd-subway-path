import { SET_PATH } from '@/store/shared/mutationTypes'
import { SEARCH_PATH } from '@/store/shared/actionTypes'
import PathService from '@/api/modules/path'

const state = {
  pathResult: null
}

const getters = {
  pathResult(state) {
    return state.pathResult
  }
}

const mutations = {
<<<<<<< HEAD
  [SET_PATH](state, pathResult) {
=======
  setPath(state, pathResult) {
>>>>>>> e5750e6ee6465a0a3ea638f28658c1dd6f6fd8c4
    state.pathResult = pathResult
  }
}

const actions = {
<<<<<<< HEAD
  async [SEARCH_PATH]({ commit }, { source, target, type }) {
    return PathService.get({ source, target, type }).then(({ data }) => {
      commit(SET_PATH, data)
=======
  async searchPath({ commit }, {}) {
    return PathService.get().then(({ data }) => {
      commit('setPath', data)
>>>>>>> e5750e6ee6465a0a3ea638f28658c1dd6f6fd8c4
    })
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
