import ApiService from '@/api'

<<<<<<< HEAD
const BASE_URL = '/paths'

const PathService = {
  get({ source, target }) {
    return ApiService.get(`${BASE_URL}/?source=${source}&target=${target}`)
=======
const PathService = {
  get() {
    return ApiService.get()
>>>>>>> e5750e6ee6465a0a3ea638f28658c1dd6f6fd8c4
  }
}

export default PathService
