import axios from './index'

const config = {
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
}

const prefix = '/api/'

class Request {
  get = <T = any> (url: string, params?: object): Promise<T> => {
    return axios.get(prefix + url, { params: params })
  }

  post = <T = any, R=any> (url: string, params?: T): Promise<R> => {
    return axios.post(prefix + url, params, config)
  }

  put = <T = any> (url: string, params?: T) => {
    return axios.put(prefix + url, params, config)
  }

  patch = <T = any> (url: string, params?: T) => {
    return axios.patch(prefix + url, params, config)
  }

  delete = (url: string) => {
    return axios.delete(prefix + url, config)
  }
}

export default new Request()
