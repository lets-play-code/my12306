import session, { CurrentUser } from '@/api/services/session'
import { LoginUser } from '@/model/LoginUser'

const TOKEN = 'token'


class AuthenticationService {
    currentUser: CurrentUser | undefined

    login = async (user: LoginUser) => {
        const response = await session.login(user)
        localStorage.setItem(TOKEN, response.token)
    }

    getCurrentUser = async () => {
        await this.fetchCurrentUser()
        return this.currentUser
    }

    fetchCurrentUser = async () => {
        if (!this.currentUser && this.getToken()) {
            this.currentUser = await session.currentUser()
        }
    }

    getToken = () => {
        return localStorage.getItem(TOKEN) 
    }
    
    clearToken = () => {
        localStorage.removeItem(TOKEN)
    }
    
}

export default new AuthenticationService()


