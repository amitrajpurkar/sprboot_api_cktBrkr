import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ProductForm from './ProductForm';
import productService from '../services/productService';
import { mockProduct, mockNewProduct } from '../testUtils';

// Mock the productService
jest.mock('../services/productService');

describe('ProductForm Component', () => {
  const mockOnSave = jest.fn();
  const mockOnCancel = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('Rendering - Add Mode', () => {
    it('should render form in add mode when no productToEdit', () => {
      render(
        <ProductForm
          productToEdit={null}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      expect(screen.getByText('Add New Product')).toBeInTheDocument();
      expect(screen.getByLabelText(/product id/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/product name/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/description/i)).toBeInTheDocument();
      expect(screen.getByLabelText(/price/i)).toBeInTheDocument();
      expect(screen.getByText('Add Product')).toBeInTheDocument();
      expect(screen.getByText('Cancel')).toBeInTheDocument();
    });

    it('should have empty form fields in add mode', () => {
      render(
        <ProductForm
          productToEdit={null}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      expect(screen.getByLabelText(/product id/i)).toHaveValue('');
      expect(screen.getByLabelText(/product name/i)).toHaveValue('');
      expect(screen.getByLabelText(/description/i)).toHaveValue('');
      expect(screen.getByLabelText(/price/i)).toHaveValue('');
    });

    it('should have enabled ID field in add mode', () => {
      render(
        <ProductForm
          productToEdit={null}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      expect(screen.getByLabelText(/product id/i)).not.toBeDisabled();
    });
  });

  describe('Rendering - Edit Mode', () => {
    it('should render form in edit mode when productToEdit provided', () => {
      render(
        <ProductForm
          productToEdit={mockProduct}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      expect(screen.getByText('Edit Product')).toBeInTheDocument();
      expect(screen.getByText('Update Product')).toBeInTheDocument();
    });

    it('should pre-fill form fields with product data in edit mode', () => {
      render(
        <ProductForm
          productToEdit={mockProduct}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      expect(screen.getByLabelText(/product id/i)).toHaveValue('P001');
      expect(screen.getByLabelText(/product name/i)).toHaveValue('Laptop');
      expect(screen.getByLabelText(/description/i)).toHaveValue('High-performance laptop');
      expect(screen.getByLabelText(/price/i)).toHaveValue('1299.99');
    });

    it('should disable ID field in edit mode', () => {
      render(
        <ProductForm
          productToEdit={mockProduct}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      expect(screen.getByLabelText(/product id/i)).toBeDisabled();
    });
  });

  describe('Form Input Handling', () => {
    it('should update form fields when user types', async () => {
      const user = userEvent.setup();
      render(
        <ProductForm
          productToEdit={null}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      const idInput = screen.getByLabelText(/product id/i);
      const nameInput = screen.getByLabelText(/product name/i);
      const descInput = screen.getByLabelText(/description/i);
      const priceInput = screen.getByLabelText(/price/i);

      await user.type(idInput, 'P004');
      await user.type(nameInput, 'Monitor');
      await user.type(descInput, '4K Monitor');
      await user.type(priceInput, '499.99');

      expect(idInput).toHaveValue('P004');
      expect(nameInput).toHaveValue('Monitor');
      expect(descInput).toHaveValue('4K Monitor');
      expect(priceInput).toHaveValue('499.99');
    });

    it('should allow editing all fields except ID in edit mode', async () => {
      const user = userEvent.setup();
      render(
        <ProductForm
          productToEdit={mockProduct}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      const nameInput = screen.getByLabelText(/product name/i);
      const descInput = screen.getByLabelText(/description/i);
      const priceInput = screen.getByLabelText(/price/i);

      await user.clear(nameInput);
      await user.type(nameInput, 'Updated Laptop');
      
      await user.clear(descInput);
      await user.type(descInput, 'Updated description');
      
      await user.clear(priceInput);
      await user.type(priceInput, '1499.99');

      expect(nameInput).toHaveValue('Updated Laptop');
      expect(descInput).toHaveValue('Updated description');
      expect(priceInput).toHaveValue('1499.99');
    });
  });

  describe('Form Validation', () => {
    it('should show error when ID is empty', async () => {
      render(
        <ProductForm
          productToEdit={null}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      const submitButton = screen.getByText('Add Product');
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Product ID is required')).toBeInTheDocument();
      });

      expect(mockOnSave).not.toHaveBeenCalled();
    });

    it('should show error when name is empty', async () => {
      const user = userEvent.setup();
      render(
        <ProductForm
          productToEdit={null}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      const idInput = screen.getByLabelText(/product id/i);
      await user.type(idInput, 'P004');

      const submitButton = screen.getByText('Add Product');
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Product name is required')).toBeInTheDocument();
      });

      expect(mockOnSave).not.toHaveBeenCalled();
    });

    it('should show error when price is empty', async () => {
      const user = userEvent.setup();
      render(
        <ProductForm
          productToEdit={null}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      const idInput = screen.getByLabelText(/product id/i);
      const nameInput = screen.getByLabelText(/product name/i);
      
      await user.type(idInput, 'P004');
      await user.type(nameInput, 'Monitor');

      const submitButton = screen.getByText('Add Product');
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Price is required')).toBeInTheDocument();
      });

      expect(mockOnSave).not.toHaveBeenCalled();
    });

    it('should show error when price is not a number', async () => {
      const user = userEvent.setup();
      render(
        <ProductForm
          productToEdit={null}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      const idInput = screen.getByLabelText(/product id/i);
      const nameInput = screen.getByLabelText(/product name/i);
      const priceInput = screen.getByLabelText(/price/i);
      
      await user.type(idInput, 'P004');
      await user.type(nameInput, 'Monitor');
      await user.type(priceInput, 'invalid');

      const submitButton = screen.getByText('Add Product');
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Price must be a number')).toBeInTheDocument();
      });

      expect(mockOnSave).not.toHaveBeenCalled();
    });

    it('should clear error when user starts typing in field', async () => {
      const user = userEvent.setup();
      render(
        <ProductForm
          productToEdit={null}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      // Trigger validation error
      const submitButton = screen.getByText('Add Product');
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Product ID is required')).toBeInTheDocument();
      });

      // Start typing in ID field
      const idInput = screen.getByLabelText(/product id/i);
      await user.type(idInput, 'P');

      // Error should be cleared
      expect(screen.queryByText('Product ID is required')).not.toBeInTheDocument();
    });
  });

  describe('Form Submission - Create Product', () => {
    it('should call productService.createProduct when creating new product', async () => {
      const user = userEvent.setup();
      productService.createProduct.mockResolvedValue({ data: mockNewProduct });

      render(
        <ProductForm
          productToEdit={null}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      // Fill in form
      await user.type(screen.getByLabelText(/product id/i), 'P004');
      await user.type(screen.getByLabelText(/product name/i), 'Monitor');
      await user.type(screen.getByLabelText(/description/i), '4K Monitor');
      await user.type(screen.getByLabelText(/price/i), '499.99');

      // Submit form
      fireEvent.click(screen.getByText('Add Product'));

      await waitFor(() => {
        expect(productService.createProduct).toHaveBeenCalledTimes(1);
        expect(productService.createProduct).toHaveBeenCalledWith({
          id: 'P004',
          name: 'Monitor',
          description: '4K Monitor',
          price: '499.99'
        });
        expect(mockOnSave).toHaveBeenCalledTimes(1);
      });
    });

    it('should reset form after successful creation', async () => {
      const user = userEvent.setup();
      productService.createProduct.mockResolvedValue({ data: mockNewProduct });

      render(
        <ProductForm
          productToEdit={null}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      await user.type(screen.getByLabelText(/product id/i), 'P004');
      await user.type(screen.getByLabelText(/product name/i), 'Monitor');
      await user.type(screen.getByLabelText(/price/i), '499.99');

      fireEvent.click(screen.getByText('Add Product'));

      await waitFor(() => {
        expect(mockOnSave).toHaveBeenCalled();
      });

      // Form should be reset
      expect(screen.getByLabelText(/product id/i)).toHaveValue('');
      expect(screen.getByLabelText(/product name/i)).toHaveValue('');
      expect(screen.getByLabelText(/price/i)).toHaveValue('');
    });
  });

  describe('Form Submission - Update Product', () => {
    it('should call productService.updateProduct when editing product', async () => {
      const user = userEvent.setup();
      const updatedProduct = { ...mockProduct, name: 'Updated Laptop' };
      productService.updateProduct.mockResolvedValue({ data: updatedProduct });

      render(
        <ProductForm
          productToEdit={mockProduct}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      // Modify name
      const nameInput = screen.getByLabelText(/product name/i);
      await user.clear(nameInput);
      await user.type(nameInput, 'Updated Laptop');

      // Submit form
      fireEvent.click(screen.getByText('Update Product'));

      await waitFor(() => {
        expect(productService.updateProduct).toHaveBeenCalledTimes(1);
        expect(productService.updateProduct).toHaveBeenCalledWith(
          'P001',
          expect.objectContaining({
            id: 'P001',
            name: 'Updated Laptop'
          })
        );
        expect(mockOnSave).toHaveBeenCalledTimes(1);
      });
    });
  });

  describe('Error Handling', () => {
    it('should show alert when create fails', async () => {
      const user = userEvent.setup();
      productService.createProduct.mockRejectedValue(new Error('Network error'));
      global.alert = jest.fn();

      render(
        <ProductForm
          productToEdit={null}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      await user.type(screen.getByLabelText(/product id/i), 'P004');
      await user.type(screen.getByLabelText(/product name/i), 'Monitor');
      await user.type(screen.getByLabelText(/price/i), '499.99');

      fireEvent.click(screen.getByText('Add Product'));

      await waitFor(() => {
        expect(global.alert).toHaveBeenCalledWith(
          'Failed to save product. Please try again.'
        );
      });

      expect(mockOnSave).not.toHaveBeenCalled();
    });
  });

  describe('Cancel Functionality', () => {
    it('should call onCancel when Cancel button clicked', () => {
      render(
        <ProductForm
          productToEdit={null}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      fireEvent.click(screen.getByText('Cancel'));

      expect(mockOnCancel).toHaveBeenCalledTimes(1);
    });

    it('should reset form when Cancel button clicked', async () => {
      const user = userEvent.setup();
      render(
        <ProductForm
          productToEdit={null}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      // Fill in form
      await user.type(screen.getByLabelText(/product id/i), 'P004');
      await user.type(screen.getByLabelText(/product name/i), 'Monitor');

      // Click cancel
      fireEvent.click(screen.getByText('Cancel'));

      expect(mockOnCancel).toHaveBeenCalled();
    });
  });

  describe('Loading State', () => {
    it('should disable buttons during submission', async () => {
      const user = userEvent.setup();
      productService.createProduct.mockImplementation(
        () => new Promise(resolve => setTimeout(() => resolve({ data: mockNewProduct }), 100))
      );

      render(
        <ProductForm
          productToEdit={null}
          onSave={mockOnSave}
          onCancel={mockOnCancel}
        />
      );

      await user.type(screen.getByLabelText(/product id/i), 'P004');
      await user.type(screen.getByLabelText(/product name/i), 'Monitor');
      await user.type(screen.getByLabelText(/price/i), '499.99');

      fireEvent.click(screen.getByText('Add Product'));

      // Buttons should be disabled during submission
      await waitFor(() => {
        expect(screen.getByText('Saving...')).toBeInTheDocument();
      });
    });
  });
});
