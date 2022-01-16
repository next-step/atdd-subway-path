import ApiService from '@/api'

const BASE_URL = '/members'

const MemberService = {
  get() {
    return ApiService.get(`${BASE_URL}/me`)
  },
  create(newMember) {
    return ApiService.post(`${BASE_URL}`, newMember)
  },
  update(updateMemberView) {
    return ApiService.update(`${BASE_URL}/me`, updateMemberView)
  },
  delete() {
    return ApiService.delete(`${BASE_URL}/me`)
  }
}

export default MemberService