import ApiService from '@/api'

const AuthService = {
  login(userInfo) {
    return ApiService.post(`/login/token`, userInfo)
  }
}

export default AuthService
