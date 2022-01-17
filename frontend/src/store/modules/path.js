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
  [SET_PATH](state, pathResult) {
    state.pathResult = pathResult
  }
}

const actions = {
  async [SEARCH_PATH]({ commit }, { source, target, type }) {
    return PathService.get({ source, target, type }).then(({ data }) => {
      commit(SET_PATH, data)
    })
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
