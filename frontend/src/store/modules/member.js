import { CREATE_MEMBER, DELETE_MEMBER, FETCH_MEMBER, UPDATE_MEMBER } from '@/store/shared/actionTypes'
import MemberService from '@/api/modules/member'
import { SET_MEMBER } from '@/store/shared/mutationTypes'

const state = {
  member: null
}

const getters = {
  member(state) {
    return state.member
  }
}

const mutations = {
  [SET_MEMBER](state, member) {
    state.member = member
  }
}

const actions = {
  async [CREATE_MEMBER]({ commit }, newMemberView) {
    return MemberService.create(newMemberView)
  },
  async [FETCH_MEMBER]({ commit }) {
    return MemberService.get().then(({ data }) => {
      commit(SET_MEMBER, data)
    })
  },
  async [DELETE_MEMBER]({ commit }, memberId) {
    return MemberService.delete().then(() => {
      commit(SET_MEMBER, null)
      localStorage.setItem('token', null)
    })
  },
  async [UPDATE_MEMBER]({ commit, dispatch }, updateMemberView) {
    return MemberService.update(updateMemberView).then(() => {
      dispatch(FETCH_MEMBER)
    })
  }
}

export default {
  state,
  getters,
  mutations,
  actions
}
