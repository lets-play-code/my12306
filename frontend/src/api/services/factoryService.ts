import { BaseService } from './baseService'

const modelName = 'demo/factories'

class FactoryService extends BaseService {
    constructor() {
        super(modelName)
    }
}

export default new FactoryService()
