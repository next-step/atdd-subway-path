import { SET_ACCESS_TOKEN } from '@/store/shared/mutationTypes'
import { FETCH_MEMBER, LOGIN } from '@/store/shared/actionTypes'
import AuthService from '@/api/modules/auth'

const state = {
  accessToken: null
}

const getters = {
  accessToken(state) {
    return state.accessToken
  }
}

const mutations = {
  [SET_ACCESS_TOKEN](state, accessToken) {
    state.accessToken = accessToken
  }
}

const actions = {
  async [LOGIN]({ commit, dispatch }, loginInfo) {
    return AuthService.login(loginInfo).then(({ data }) => {
      commit(SET_ACCESS_TOKEN, data.accessToken)
      localStorage.setItem('token', data.accessToken)
      dispatch(FETCH_MEMBER)
    })
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
