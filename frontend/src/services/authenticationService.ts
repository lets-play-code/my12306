import session, { CurrentUser, TokenResponse } from '@/api/services/session'
import { LoginUser } from '@/model/LoginUser'

const TOKEN = 'token'
const USER_NAME = 'userName'

class AuthenticationService {
    currentUser: CurrentUser | undefined

    login = async (user: LoginUser): Promise<void> => {
        const response = await session.login(user) as TokenResponse
        localStorage.setItem(TOKEN, response.token)
        localStorage.setItem(USER_NAME, response.fullName)
    }

    getCurrentUser = async (): Promise<CurrentUser | undefined> => {
        if (!this.currentUser) {
            const token = this.getToken()
            if (token) {
                // 有 token 但无 currentUser 时，从本地获取姓名
                const fullName = localStorage.getItem(USER_NAME)
                if (fullName) {
                    this.currentUser = { fullName }
                }
            }
        }
        return this.currentUser
    }

    getToken = (): string | null => {
        return localStorage.getItem(TOKEN)
    }

    clearToken = () => {
        localStorage.removeItem(TOKEN)
        localStorage.removeItem(USER_NAME)
        this.currentUser = undefined
    }

    isLoggedIn = (): boolean => {
        return !!this.getToken()
    }
}

export default new AuthenticationService()
