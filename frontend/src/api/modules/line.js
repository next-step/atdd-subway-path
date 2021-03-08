import ApiService from '@/api'
import station from "../../store/modules/station";

const BASE_URL = '/lines'

const LineService = {
  get(lineId) {
    return ApiService.get(`${BASE_URL}/${lineId}`)
  },
  getAll() {
    return ApiService.get(`${BASE_URL}`)
  },
  create(newLine) {
    return ApiService.post(`${BASE_URL}`, newLine)
  },
  update(editingLine) {
    return ApiService.update(`${BASE_URL}/${editingLine.lineId}`, editingLine.line)
  },
  delete(lineId) {
    return ApiService.delete(`${BASE_URL}/${lineId}`)
  },
  createSection({ lineId, section }) {
    return ApiService.post(`${BASE_URL}/${lineId}/sections`, section)
  },
  deleteSection({ lineId, stationId }) {
    return ApiService.delete(`${BASE_URL}/${lineId}/sections?stationId=${stationId}`)
  }
}

export default LineService
