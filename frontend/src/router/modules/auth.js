import LoginPage from '@/views/auth/LoginPage'
import JoinPage from '@/views/auth/JoinPage'
import Mypage from '@/views/auth/Mypage'
import MypageEdit from '@/views/auth/MypageEdit'

const authRoutes = [
  {
    path: '/login',
    component: LoginPage
  },
  {
    path: '/join',
    component: JoinPage
  },
  {
    path: '/mypage',
    component: Mypage
  },
  {
    path: '/mypage/edit',
    component: MypageEdit
  }
]
export default authRoutes
