import axios from '../index'

class ProductImporterService {

    async importExcel(formData: FormData) {
        const response = await axios.post('/api/products/excel', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
        console.log(response.data);
    }
}

export default new ProductImporterService()
