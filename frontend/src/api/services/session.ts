import { LoginUser } from '../../model/LoginUser'
import Request from '../request'

const modelName = 'sessions'
export interface TokenResponse {
    token: string
}

export interface CurrentUser {
    fullName: string
}


class Session {
    login = (user: LoginUser): Promise<TokenResponse> => Request.post(`${modelName}`, user) as Promise<TokenResponse>

    currentUser = (): Promise<CurrentUser> => Request.get(`${modelName}`)
}

export default new Session()

