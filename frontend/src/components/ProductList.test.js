import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import ProductList from './ProductList';
import { mockProducts } from '../testUtils';

describe('ProductList Component', () => {
  const mockOnEdit = jest.fn();
  const mockOnDelete = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    // Mock window.confirm
    global.confirm = jest.fn(() => true);
  });

  describe('Rendering', () => {
    it('should render loading state', () => {
      render(
        <ProductList
          products={[]}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={true}
        />
      );

      expect(screen.getByText(/loading products/i)).toBeInTheDocument();
    });

    it('should render empty state when no products', () => {
      render(
        <ProductList
          products={[]}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={false}
        />
      );

      expect(screen.getByText(/no products found/i)).toBeInTheDocument();
      expect(screen.getByText(/add your first product/i)).toBeInTheDocument();
    });

    it('should render product list with correct data', () => {
      render(
        <ProductList
          products={mockProducts}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={false}
        />
      );

      expect(screen.getByText(/product list/i)).toBeInTheDocument();
      expect(screen.getByText(/\(3\)/)).toBeInTheDocument(); // Product count
      
      // Check table headers
      expect(screen.getByText('ID')).toBeInTheDocument();
      expect(screen.getByText('Name')).toBeInTheDocument();
      expect(screen.getByText('Description')).toBeInTheDocument();
      expect(screen.getByText('Price')).toBeInTheDocument();
      expect(screen.getByText('Actions')).toBeInTheDocument();
    });

    it('should render all products in the table', () => {
      render(
        <ProductList
          products={mockProducts}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={false}
        />
      );

      // Check first product
      expect(screen.getByText('P001')).toBeInTheDocument();
      expect(screen.getByText('Laptop')).toBeInTheDocument();
      expect(screen.getByText('High-performance laptop')).toBeInTheDocument();
      expect(screen.getByText('$1299.99')).toBeInTheDocument();

      // Check second product
      expect(screen.getByText('P002')).toBeInTheDocument();
      expect(screen.getByText('Mouse')).toBeInTheDocument();
      expect(screen.getByText('Wireless mouse')).toBeInTheDocument();
      expect(screen.getByText('$29.99')).toBeInTheDocument();

      // Check third product
      expect(screen.getByText('P003')).toBeInTheDocument();
      expect(screen.getByText('Keyboard')).toBeInTheDocument();
      expect(screen.getByText('Mechanical keyboard')).toBeInTheDocument();
      expect(screen.getByText('$89.99')).toBeInTheDocument();
    });

    it('should render "No description" for products without description', () => {
      const productsWithoutDesc = [
        { id: 'P001', name: 'Test Product', description: null, price: '99.99' }
      ];

      render(
        <ProductList
          products={productsWithoutDesc}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={false}
        />
      );

      expect(screen.getByText('No description')).toBeInTheDocument();
    });

    it('should render Edit and Delete buttons for each product', () => {
      render(
        <ProductList
          products={mockProducts}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={false}
        />
      );

      const editButtons = screen.getAllByText(/edit/i);
      const deleteButtons = screen.getAllByText(/delete/i);

      expect(editButtons).toHaveLength(3);
      expect(deleteButtons).toHaveLength(3);
    });
  });

  describe('Edit Functionality', () => {
    it('should call onEdit with correct product when Edit button clicked', () => {
      render(
        <ProductList
          products={mockProducts}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={false}
        />
      );

      const editButtons = screen.getAllByText(/edit/i);
      fireEvent.click(editButtons[0]); // Click first Edit button

      expect(mockOnEdit).toHaveBeenCalledTimes(1);
      expect(mockOnEdit).toHaveBeenCalledWith(mockProducts[0]);
    });

    it('should call onEdit with correct product for each row', () => {
      render(
        <ProductList
          products={mockProducts}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={false}
        />
      );

      const editButtons = screen.getAllByText(/edit/i);
      
      fireEvent.click(editButtons[0]);
      expect(mockOnEdit).toHaveBeenCalledWith(mockProducts[0]);

      fireEvent.click(editButtons[1]);
      expect(mockOnEdit).toHaveBeenCalledWith(mockProducts[1]);

      fireEvent.click(editButtons[2]);
      expect(mockOnEdit).toHaveBeenCalledWith(mockProducts[2]);

      expect(mockOnEdit).toHaveBeenCalledTimes(3);
    });
  });

  describe('Delete Functionality', () => {
    it('should show confirmation dialog when Delete button clicked', () => {
      render(
        <ProductList
          products={mockProducts}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={false}
        />
      );

      const deleteButtons = screen.getAllByText(/delete/i);
      fireEvent.click(deleteButtons[0]);

      expect(global.confirm).toHaveBeenCalledTimes(1);
      expect(global.confirm).toHaveBeenCalledWith(
        expect.stringContaining('Laptop')
      );
    });

    it('should call onDelete when user confirms deletion', () => {
      global.confirm = jest.fn(() => true);

      render(
        <ProductList
          products={mockProducts}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={false}
        />
      );

      const deleteButtons = screen.getAllByText(/delete/i);
      fireEvent.click(deleteButtons[0]);

      expect(mockOnDelete).toHaveBeenCalledTimes(1);
      expect(mockOnDelete).toHaveBeenCalledWith('P001');
    });

    it('should NOT call onDelete when user cancels deletion', () => {
      global.confirm = jest.fn(() => false);

      render(
        <ProductList
          products={mockProducts}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={false}
        />
      );

      const deleteButtons = screen.getAllByText(/delete/i);
      fireEvent.click(deleteButtons[0]);

      expect(global.confirm).toHaveBeenCalledTimes(1);
      expect(mockOnDelete).not.toHaveBeenCalled();
    });

    it('should delete correct product when multiple products exist', () => {
      global.confirm = jest.fn(() => true);

      render(
        <ProductList
          products={mockProducts}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={false}
        />
      );

      const deleteButtons = screen.getAllByText(/delete/i);
      
      fireEvent.click(deleteButtons[1]); // Delete second product
      expect(mockOnDelete).toHaveBeenCalledWith('P002');

      fireEvent.click(deleteButtons[2]); // Delete third product
      expect(mockOnDelete).toHaveBeenCalledWith('P003');
    });
  });

  describe('Product Count Display', () => {
    it('should display correct product count', () => {
      render(
        <ProductList
          products={mockProducts}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={false}
        />
      );

      expect(screen.getByText(/\(3\)/)).toBeInTheDocument();
    });

    it('should update count when products change', () => {
      const { rerender } = render(
        <ProductList
          products={mockProducts}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={false}
        />
      );

      expect(screen.getByText(/\(3\)/)).toBeInTheDocument();

      rerender(
        <ProductList
          products={[mockProducts[0]]}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={false}
        />
      );

      expect(screen.getByText(/\(1\)/)).toBeInTheDocument();
    });
  });

  describe('Price Formatting', () => {
    it('should display prices with dollar sign', () => {
      render(
        <ProductList
          products={mockProducts}
          onEdit={mockOnEdit}
          onDelete={mockOnDelete}
          loading={false}
        />
      );

      expect(screen.getByText('$1299.99')).toBeInTheDocument();
      expect(screen.getByText('$29.99')).toBeInTheDocument();
      expect(screen.getByText('$89.99')).toBeInTheDocument();
    });
  });
});
