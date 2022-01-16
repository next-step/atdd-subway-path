import ApiService from '@/api'

const BASE_URL = '/maps'

const MapService = {
  get() {
    return ApiService.get(`${BASE_URL}`)
  }
}

export default MapService
