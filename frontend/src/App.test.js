import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import App from './App';
import productService from './services/productService';
import { mockProducts, mockNewProduct } from './testUtils';

// Mock the productService
jest.mock('./services/productService');

describe('App Component - Integration Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    global.confirm = jest.fn(() => true);
    global.alert = jest.fn();
  });

  describe('Initial Rendering and Data Loading', () => {
    it('should render app header and title', async () => {
      productService.getAllProducts.mockResolvedValue({ data: [] });

      render(<App />);

      expect(screen.getAllByText(/product management system/i).length).toBeGreaterThan(0);
      expect(screen.getByText(/manage your product catalog/i)).toBeInTheDocument();
    });

    it('should load products on mount', async () => {
      productService.getAllProducts.mockResolvedValue({ data: mockProducts });

      render(<App />);

      await waitFor(() => {
        expect(productService.getAllProducts).toHaveBeenCalledTimes(1);
        expect(screen.getByText('Laptop')).toBeInTheDocument();
        expect(screen.getByText('Mouse')).toBeInTheDocument();
        expect(screen.getByText('Keyboard')).toBeInTheDocument();
      });
    });

    it('should show loading state initially', () => {
      productService.getAllProducts.mockImplementation(
        () => new Promise(resolve => setTimeout(() => resolve({ data: [] }), 100))
      );

      render(<App />);

      expect(screen.getByText(/loading products/i)).toBeInTheDocument();
    });

    it('should show error banner when loading fails', async () => {
      productService.getAllProducts.mockRejectedValue(new Error('Network error'));

      render(<App />);

      await waitFor(() => {
        expect(screen.getByText(/failed to load products/i)).toBeInTheDocument();
        expect(screen.getByText(/make sure the backend server is running/i)).toBeInTheDocument();
      });
    });

    it('should have retry button in error banner', async () => {
      productService.getAllProducts.mockRejectedValue(new Error('Network error'));

      render(<App />);

      await waitFor(() => {
        expect(screen.getByText('Retry')).toBeInTheDocument();
      });
    });

    it('should retry loading when retry button clicked', async () => {
      productService.getAllProducts
        .mockRejectedValueOnce(new Error('Network error'))
        .mockResolvedValueOnce({ data: mockProducts });

      render(<App />);

      await waitFor(() => {
        expect(screen.getByText('Retry')).toBeInTheDocument();
      });

      fireEvent.click(screen.getByText('Retry'));

      await waitFor(() => {
        expect(productService.getAllProducts).toHaveBeenCalledTimes(2);
        expect(screen.getByText('Laptop')).toBeInTheDocument();
      });
    });
  });

  describe('Add New Product Flow', () => {
    it('should show Add New Product button initially', async () => {
      productService.getAllProducts.mockResolvedValue({ data: mockProducts });

      render(<App />);

      await waitFor(() => {
        expect(screen.getByText(/add new product/i)).toBeInTheDocument();
      });
    });

    it('should show form when Add New Product button clicked', async () => {
      productService.getAllProducts.mockResolvedValue({ data: mockProducts });

      render(<App />);

      await waitFor(() => {
        expect(screen.getByText(/add new product/i)).toBeInTheDocument();
      });

      fireEvent.click(screen.getByText(/add new product/i));

      expect(screen.getByText('Add New Product')).toBeInTheDocument();
      expect(screen.getByLabelText(/product id/i)).toBeInTheDocument();
    });

    it('should hide Add New Product button when form is shown', async () => {
      productService.getAllProducts.mockResolvedValue({ data: mockProducts });

      render(<App />);

      await waitFor(() => {
        const addButton = screen.getByText(/add new product/i);
        fireEvent.click(addButton);
      });

      expect(screen.queryByText(/➕ add new product/i)).not.toBeInTheDocument();
    });

    it('should create product and refresh list on successful save', async () => {
      const user = userEvent.setup();
      productService.getAllProducts
        .mockResolvedValueOnce({ data: mockProducts })
        .mockResolvedValueOnce({ data: [...mockProducts, mockNewProduct] });
      productService.createProduct.mockResolvedValue({ data: mockNewProduct });

      render(<App />);

      await waitFor(() => {
        expect(screen.getByText(/add new product/i)).toBeInTheDocument();
      });

      fireEvent.click(screen.getByText(/add new product/i));

      // Fill in form
      await user.type(screen.getByLabelText(/product id/i), 'P004');
      await user.type(screen.getByLabelText(/product name/i), 'Monitor');
      await user.type(screen.getByLabelText(/price/i), '499.99');

      // Submit form
      fireEvent.click(screen.getByText('Add Product'));

      await waitFor(() => {
        expect(productService.createProduct).toHaveBeenCalledWith({
          id: 'P004',
          name: 'Monitor',
          description: '',
          price: '499.99'
        });
        expect(productService.getAllProducts).toHaveBeenCalledTimes(2);
      });
    });

    it('should hide form after successful save', async () => {
      const user = userEvent.setup();
      productService.getAllProducts.mockResolvedValue({ data: mockProducts });
      productService.createProduct.mockResolvedValue({ data: mockNewProduct });

      render(<App />);

      await waitFor(() => {
        fireEvent.click(screen.getByText(/add new product/i));
      });

      await user.type(screen.getByLabelText(/product id/i), 'P004');
      await user.type(screen.getByLabelText(/product name/i), 'Monitor');
      await user.type(screen.getByLabelText(/price/i), '499.99');

      fireEvent.click(screen.getByText('Add Product'));

      await waitFor(() => {
        expect(screen.queryByText('Add New Product')).not.toBeInTheDocument();
        expect(screen.getByText(/add new product/i)).toBeInTheDocument(); // Button is back
      });
    });

    it('should hide form when Cancel button clicked', async () => {
      productService.getAllProducts.mockResolvedValue({ data: mockProducts });

      render(<App />);

      await waitFor(() => {
        fireEvent.click(screen.getByText(/add new product/i));
      });

      expect(screen.getByText('Add New Product')).toBeInTheDocument();

      fireEvent.click(screen.getByText('Cancel'));

      expect(screen.queryByText('Add New Product')).not.toBeInTheDocument();
      expect(screen.getByText(/add new product/i)).toBeInTheDocument();
    });
  });

  describe('Edit Product Flow', () => {
    it('should show form with product data when Edit button clicked', async () => {
      productService.getAllProducts.mockResolvedValue({ data: mockProducts });

      render(<App />);

      await waitFor(() => {
        expect(screen.getByText('Laptop')).toBeInTheDocument();
      });

      const editButtons = screen.getAllByText(/edit/i);
      fireEvent.click(editButtons[0]);

      expect(screen.getByText('Edit Product')).toBeInTheDocument();
      expect(screen.getByLabelText(/product id/i)).toHaveValue('P001');
      expect(screen.getByLabelText(/product name/i)).toHaveValue('Laptop');
    });

    it('should update product and refresh list on successful update', async () => {
      const user = userEvent.setup();
      const updatedProduct = { ...mockProducts[0], name: 'Updated Laptop' };
      productService.getAllProducts
        .mockResolvedValueOnce({ data: mockProducts })
        .mockResolvedValueOnce({ data: [updatedProduct, mockProducts[1], mockProducts[2]] });
      productService.updateProduct.mockResolvedValue({ data: updatedProduct });

      render(<App />);

      await waitFor(() => {
        const editButtons = screen.getAllByText(/edit/i);
        fireEvent.click(editButtons[0]);
      });

      const nameInput = screen.getByLabelText(/product name/i);
      await user.clear(nameInput);
      await user.type(nameInput, 'Updated Laptop');

      fireEvent.click(screen.getByText('Update Product'));

      await waitFor(() => {
        expect(productService.updateProduct).toHaveBeenCalledWith(
          'P001',
          expect.objectContaining({ name: 'Updated Laptop' })
        );
        expect(productService.getAllProducts).toHaveBeenCalledTimes(2);
      });
    });

    it('should hide form after successful update', async () => {
      const user = userEvent.setup();
      productService.getAllProducts.mockResolvedValue({ data: mockProducts });
      productService.updateProduct.mockResolvedValue({ data: mockProducts[0] });

      render(<App />);

      await waitFor(() => {
        const editButtons = screen.getAllByText(/edit/i);
        fireEvent.click(editButtons[0]);
      });

      const nameInput = screen.getByLabelText(/product name/i);
      await user.clear(nameInput);
      await user.type(nameInput, 'Updated');

      fireEvent.click(screen.getByText('Update Product'));

      await waitFor(() => {
        expect(screen.queryByText('Edit Product')).not.toBeInTheDocument();
      });
    });
  });

  describe('Delete Product Flow', () => {
    it('should show confirmation dialog when Delete button clicked', async () => {
      productService.getAllProducts.mockResolvedValue({ data: mockProducts });

      render(<App />);

      await waitFor(() => {
        expect(screen.getByText('Laptop')).toBeInTheDocument();
      });

      const deleteButtons = screen.getAllByText(/delete/i);
      fireEvent.click(deleteButtons[0]);

      expect(global.confirm).toHaveBeenCalledWith(
        expect.stringContaining('Laptop')
      );
    });

    it('should delete product and refresh list when confirmed', async () => {
      productService.getAllProducts
        .mockResolvedValueOnce({ data: mockProducts })
        .mockResolvedValueOnce({ data: [mockProducts[1], mockProducts[2]] });
      productService.deleteProduct.mockResolvedValue({ status: 204 });

      render(<App />);

      await waitFor(() => {
        expect(screen.getByText('Laptop')).toBeInTheDocument();
      });

      const deleteButtons = screen.getAllByText(/delete/i);
      fireEvent.click(deleteButtons[0]);

      await waitFor(() => {
        expect(productService.deleteProduct).toHaveBeenCalledWith('P001');
        expect(productService.getAllProducts).toHaveBeenCalledTimes(2);
      });
    });

    it('should not delete product when user cancels', async () => {
      global.confirm = jest.fn(() => false);
      productService.getAllProducts.mockResolvedValue({ data: mockProducts });

      render(<App />);

      await waitFor(() => {
        expect(screen.getByText('Laptop')).toBeInTheDocument();
      });

      const deleteButtons = screen.getAllByText(/delete/i);
      fireEvent.click(deleteButtons[0]);

      expect(productService.deleteProduct).not.toHaveBeenCalled();
      expect(productService.getAllProducts).toHaveBeenCalledTimes(1);
    });

    it('should show alert when delete fails', async () => {
      productService.getAllProducts.mockResolvedValue({ data: mockProducts });
      productService.deleteProduct.mockRejectedValue(new Error('Delete failed'));

      render(<App />);

      await waitFor(() => {
        expect(screen.getByText('Laptop')).toBeInTheDocument();
      });

      const deleteButtons = screen.getAllByText(/delete/i);
      fireEvent.click(deleteButtons[0]);

      await waitFor(() => {
        expect(global.alert).toHaveBeenCalledWith(
          'Failed to delete product. Please try again.'
        );
      });
    });
  });

  describe('Backend Service Integration', () => {
    it('should call getAllProducts on initial load', async () => {
      productService.getAllProducts.mockResolvedValue({ data: mockProducts });

      render(<App />);

      await waitFor(() => {
        expect(productService.getAllProducts).toHaveBeenCalledTimes(1);
      });
    });

    it('should call createProduct with correct data', async () => {
      const user = userEvent.setup();
      productService.getAllProducts.mockResolvedValue({ data: [] });
      productService.createProduct.mockResolvedValue({ data: mockNewProduct });

      render(<App />);

      await waitFor(() => {
        fireEvent.click(screen.getByText(/add new product/i));
      });

      await user.type(screen.getByLabelText(/product id/i), 'P004');
      await user.type(screen.getByLabelText(/product name/i), 'Monitor');
      await user.type(screen.getByLabelText(/description/i), '4K Monitor');
      await user.type(screen.getByLabelText(/price/i), '499.99');

      fireEvent.click(screen.getByText('Add Product'));

      await waitFor(() => {
        expect(productService.createProduct).toHaveBeenCalledWith({
          id: 'P004',
          name: 'Monitor',
          description: '4K Monitor',
          price: '499.99'
        });
      });
    });

    it('should call updateProduct with correct ID and data', async () => {
      const user = userEvent.setup();
      productService.getAllProducts.mockResolvedValue({ data: mockProducts });
      productService.updateProduct.mockResolvedValue({ data: mockProducts[0] });

      render(<App />);

      await waitFor(() => {
        const editButtons = screen.getAllByText(/edit/i);
        fireEvent.click(editButtons[0]);
      });

      const priceInput = screen.getByLabelText(/price/i);
      await user.clear(priceInput);
      await user.type(priceInput, '1499.99');

      fireEvent.click(screen.getByText('Update Product'));

      await waitFor(() => {
        expect(productService.updateProduct).toHaveBeenCalledWith(
          'P001',
          expect.objectContaining({
            id: 'P001',
            price: '1499.99'
          })
        );
      });
    });

    it('should call deleteProduct with correct ID', async () => {
      productService.getAllProducts.mockResolvedValue({ data: mockProducts });
      productService.deleteProduct.mockResolvedValue({ status: 204 });

      render(<App />);

      await waitFor(() => {
        const deleteButtons = screen.getAllByText(/delete/i);
        fireEvent.click(deleteButtons[1]); // Delete second product
      });

      await waitFor(() => {
        expect(productService.deleteProduct).toHaveBeenCalledWith('P002');
      });
    });

    it('should refresh product list after each operation', async () => {
      const user = userEvent.setup();
      productService.getAllProducts.mockResolvedValue({ data: mockProducts });
      productService.createProduct.mockResolvedValue({ data: mockNewProduct });
      productService.updateProduct.mockResolvedValue({ data: mockProducts[0] });
      productService.deleteProduct.mockResolvedValue({ status: 204 });

      render(<App />);

      // Initial load
      await waitFor(() => {
        expect(productService.getAllProducts).toHaveBeenCalledTimes(1);
      });

      // Create product
      fireEvent.click(screen.getByText(/add new product/i));
      await user.type(screen.getByLabelText(/product id/i), 'P004');
      await user.type(screen.getByLabelText(/product name/i), 'Monitor');
      await user.type(screen.getByLabelText(/price/i), '499.99');
      fireEvent.click(screen.getByText('Add Product'));

      await waitFor(() => {
        expect(productService.getAllProducts).toHaveBeenCalledTimes(2);
      });

      // Edit product
      const editButtons = screen.getAllByText(/edit/i);
      fireEvent.click(editButtons[0]);
      fireEvent.click(screen.getByText('Update Product'));

      await waitFor(() => {
        expect(productService.getAllProducts).toHaveBeenCalledTimes(3);
      });

      // Delete product
      const deleteButtons = screen.getAllByText(/delete/i);
      fireEvent.click(deleteButtons[0]);

      await waitFor(() => {
        expect(productService.getAllProducts).toHaveBeenCalledTimes(4);
      });
    });
  });

  describe('Footer', () => {
    it('should render footer with copyright', async () => {
      productService.getAllProducts.mockResolvedValue({ data: [] });

      render(<App />);

      expect(screen.getByText(/© 2025 product management system/i)).toBeInTheDocument();
      expect(screen.getByText(/built with react & spring boot/i)).toBeInTheDocument();
    });
  });
});
