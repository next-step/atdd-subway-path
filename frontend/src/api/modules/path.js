import ApiService from '@/api'

const BASE_URL = '/paths'

const PathService = {
  get({ source, target }) {
    return ApiService.get(`${BASE_URL}/?source=${source}&target=${target}`)
  }
}

export default PathService
