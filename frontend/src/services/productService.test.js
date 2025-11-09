import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import productService from './productService';
import { mockProducts, mockProduct, mockNewProduct } from '../testUtils';

describe('ProductService', () => {
  let mock;

  beforeEach(() => {
    mock = new MockAdapter(axios);
  });

  afterEach(() => {
    mock.restore();
  });

  describe('getAllProducts', () => {
    it('should fetch all products successfully', async () => {
      mock.onGet('/api/v1/products').reply(200, mockProducts);

      const response = await productService.getAllProducts();

      expect(response.data).toEqual(mockProducts);
      expect(response.data).toHaveLength(3);
      expect(mock.history.get.length).toBe(1);
      expect(mock.history.get[0].url).toBe('/api/v1/products');
    });

    it('should handle error when fetching products fails', async () => {
      mock.onGet('/api/v1/products').reply(500, { message: 'Server error' });

      await expect(productService.getAllProducts()).rejects.toThrow();
      expect(mock.history.get.length).toBe(1);
    });

    it('should return empty array when no products exist', async () => {
      mock.onGet('/api/v1/products').reply(200, []);

      const response = await productService.getAllProducts();

      expect(response.data).toEqual([]);
      expect(response.data).toHaveLength(0);
    });
  });

  describe('getProductById', () => {
    it('should fetch a single product by ID successfully', async () => {
      mock.onGet('/api/v1/products/P001').reply(200, mockProduct);

      const response = await productService.getProductById('P001');

      expect(response.data).toEqual(mockProduct);
      expect(response.data.id).toBe('P001');
      expect(mock.history.get.length).toBe(1);
      expect(mock.history.get[0].url).toBe('/api/v1/products/P001');
    });

    it('should handle 404 when product not found', async () => {
      mock.onGet('/api/v1/products/P999').reply(404);

      await expect(productService.getProductById('P999')).rejects.toThrow();
      expect(mock.history.get.length).toBe(1);
    });
  });

  describe('createProduct', () => {
    it('should create a new product successfully', async () => {
      mock.onPost('/api/v1/products').reply(201, mockNewProduct);

      const response = await productService.createProduct(mockNewProduct);

      expect(response.data).toEqual(mockNewProduct);
      expect(response.status).toBe(201);
      expect(mock.history.post.length).toBe(1);
      expect(mock.history.post[0].url).toBe('/api/v1/products');
      expect(JSON.parse(mock.history.post[0].data)).toEqual(mockNewProduct);
    });

    it('should handle validation error when creating product', async () => {
      const invalidProduct = { name: 'Invalid', price: '99.99' }; // Missing ID
      mock.onPost('/api/v1/products').reply(400, { message: 'Invalid product data' });

      await expect(productService.createProduct(invalidProduct)).rejects.toThrow();
      expect(mock.history.post.length).toBe(1);
    });

    it('should send correct request body when creating product', async () => {
      mock.onPost('/api/v1/products').reply(201, mockNewProduct);

      await productService.createProduct(mockNewProduct);

      const requestData = JSON.parse(mock.history.post[0].data);
      expect(requestData).toHaveProperty('id');
      expect(requestData).toHaveProperty('name');
      expect(requestData).toHaveProperty('description');
      expect(requestData).toHaveProperty('price');
    });
  });

  describe('updateProduct', () => {
    it('should update an existing product successfully', async () => {
      const updatedProduct = { ...mockProduct, name: 'Updated Laptop' };
      mock.onPut('/api/v1/products/P001').reply(200, updatedProduct);

      const response = await productService.updateProduct('P001', updatedProduct);

      expect(response.data).toEqual(updatedProduct);
      expect(response.data.name).toBe('Updated Laptop');
      expect(mock.history.put.length).toBe(1);
      expect(mock.history.put[0].url).toBe('/api/v1/products/P001');
    });

    it('should handle 404 when updating non-existent product', async () => {
      mock.onPut('/api/v1/products/P999').reply(404);

      await expect(productService.updateProduct('P999', mockProduct)).rejects.toThrow();
      expect(mock.history.put.length).toBe(1);
    });

    it('should send correct request body when updating product', async () => {
      mock.onPut('/api/v1/products/P001').reply(200, mockProduct);

      await productService.updateProduct('P001', mockProduct);

      const requestData = JSON.parse(mock.history.put[0].data);
      expect(requestData).toEqual(mockProduct);
    });
  });

  describe('deleteProduct', () => {
    it('should delete a product successfully', async () => {
      mock.onDelete('/api/v1/products/P001').reply(204);

      const response = await productService.deleteProduct('P001');

      expect(response.status).toBe(204);
      expect(mock.history.delete.length).toBe(1);
      expect(mock.history.delete[0].url).toBe('/api/v1/products/P001');
    });

    it('should handle 404 when deleting non-existent product', async () => {
      mock.onDelete('/api/v1/products/P999').reply(404);

      await expect(productService.deleteProduct('P999')).rejects.toThrow();
      expect(mock.history.delete.length).toBe(1);
    });

    it('should not send request body when deleting', async () => {
      mock.onDelete('/api/v1/products/P001').reply(204);

      await productService.deleteProduct('P001');

      expect(mock.history.delete[0].data).toBeUndefined();
    });
  });

  describe('API endpoint verification', () => {
    it('should use correct base URL for all requests', async () => {
      mock.onGet('/api/v1/products').reply(200, []);
      mock.onPost('/api/v1/products').reply(201, mockNewProduct);
      mock.onPut('/api/v1/products/P001').reply(200, mockProduct);
      mock.onDelete('/api/v1/products/P001').reply(204);

      await productService.getAllProducts();
      await productService.createProduct(mockNewProduct);
      await productService.updateProduct('P001', mockProduct);
      await productService.deleteProduct('P001');

      expect(mock.history.get[0].url).toContain('/api/v1/products');
      expect(mock.history.post[0].url).toContain('/api/v1/products');
      expect(mock.history.put[0].url).toContain('/api/v1/products');
      expect(mock.history.delete[0].url).toContain('/api/v1/products');
    });
  });
});
