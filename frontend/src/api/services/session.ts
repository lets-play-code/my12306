import { LoginUser } from '../../model/LoginUser'
import Request from '../request'

const modelName = 'sessions'
export interface TokenResponse {
    token: string
    fullName: string
}

export interface CurrentUser {
    fullName: string
}


class Session {
    login = (user: LoginUser): Promise<TokenResponse> => Request.post(`${modelName}`, user) as Promise<TokenResponse>

    // 获取当前用户信息
    currentUser = (): Promise<CurrentUser> => Request.get(`${modelName}/current`)
}

export default new Session()
