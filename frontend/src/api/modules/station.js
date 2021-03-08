import ApiService from '@/api'

const BASE_URL = '/stations'

const StationService = {
  get(stationId) {
    return ApiService.get(`${BASE_URL}/${stationId}`)
  },
  getAll() {
    return ApiService.get(`${BASE_URL}`)
  },
  create(newStationName) {
    return ApiService.post(`${BASE_URL}`, newStationName)
  },
  update(station) {
    return ApiService.put(`${BASE_URL}/${station.id}`, station)
  },
  delete(stationId) {
    return ApiService.delete(`${BASE_URL}/${stationId}`)
  }
}

export default StationService
