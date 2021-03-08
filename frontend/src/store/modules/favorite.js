import { SET_FAVORITES } from '@/store/shared/mutationTypes'
import { CREATE_FAVORITE, DELETE_FAVORITE, FETCH_FAVORITES } from '@/store/shared/actionTypes'
import FavoriteService from '@/api/modules/favorite'

const state = {
  favorites: []
}

const getters = {
  favorites(state) {
    return state.favorites
  }
}

const mutations = {
  [SET_FAVORITES](state, favorites) {
    state.favorites = favorites
  }
}

const actions = {
  async [FETCH_FAVORITES]({ commit }) {
    return FavoriteService.get().then(({ data }) => {
      commit(SET_FAVORITES, data)
    })
  },
  async [CREATE_FAVORITE]({ commit }, newFavorite) {
    return FavoriteService.create(newFavorite)
  },
  async [DELETE_FAVORITE]({ commit }, favoriteId) {
    return FavoriteService.delete(favoriteId)
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
