import ApiService from '@/api'

const AuthService = {
  login(userInfo) {
<<<<<<< HEAD
    return ApiService.post(`/login/token`, userInfo)
=======
    const { email, password } = userInfo
    const username = email
    return ApiService.login(`/login/token`, {
      auth: {
        username,
        password
      }
    })
>>>>>>> e5750e6ee6465a0a3ea638f28658c1dd6f6fd8c4
  }
}

export default AuthService
