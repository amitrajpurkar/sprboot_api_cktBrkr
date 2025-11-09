// Test utilities and mock data for frontend tests

export const mockProducts = [
  {
    id: 'P001',
    name: 'Laptop',
    description: 'High-performance laptop',
    price: '1299.99'
  },
  {
    id: 'P002',
    name: 'Mouse',
    description: 'Wireless mouse',
    price: '29.99'
  },
  {
    id: 'P003',
    name: 'Keyboard',
    description: 'Mechanical keyboard',
    price: '89.99'
  }
];

export const mockProduct = {
  id: 'P001',
  name: 'Laptop',
  description: 'High-performance laptop',
  price: '1299.99'
};

export const mockNewProduct = {
  id: 'P004',
  name: 'Monitor',
  description: '4K Monitor',
  price: '499.99'
};

export const mockUpdatedProduct = {
  id: 'P001',
  name: 'Updated Laptop',
  description: 'Updated description',
  price: '1499.99'
};

// Helper to wait for async updates
export const waitForAsync = () => new Promise(resolve => setTimeout(resolve, 0));
