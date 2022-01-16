import ApiService from '@/api'

const AuthService = {
  login(userInfo) {
    const { email, password } = userInfo
    const username = email
    return ApiService.login(`/login/token`, {
      auth: {
        username,
        password
      }
    })
  }
}

export default AuthService
