import { BaseService } from './baseService'

const modelName = 'products'

class ProductService extends BaseService {
    constructor() {
        super(modelName)
    }
}

export default new ProductService()
