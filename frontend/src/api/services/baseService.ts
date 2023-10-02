import Request from '../request'

export type PageResult<T> = {
    pageNum: number, pageSize: number, total: number, list: Array<T>
}

export class BaseService {
    modelName: string

    constructor(modelName: string) {
        this.modelName = modelName
    }

    list = <T>(params = {}) => Request.get<PageResult<T>>(`${this.modelName}`, params)

    detail = <T>(id: number | string) => Request.get<T>(`${this.modelName}/${id}`)

    create = <T>(params: T) => Request.post<T>(`${this.modelName}`, params)

    update = <T>(id: number | string, params: T) => Request.put<T>(`${this.modelName}/${id}`, params)

    updatePartially = <T>(id: number | string, params: T) => Request.patch<T>(`${this.modelName}/${id}`, params)

    delete = (id: number | string) => Request.delete(`${this.modelName}/${id}`)
}
