import React, { useState, useEffect } from 'react';
import productService from '../services/productService';
import './ProductForm.css';

const ProductForm = ({ productToEdit, onSave, onCancel }) => {
  const [product, setProduct] = useState({
    id: '',
    name: '',
    description: '',
    price: ''
  });

  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (productToEdit) {
      setProduct(productToEdit);
    }
  }, [productToEdit]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProduct(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear error for this field
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    if (!product.id.trim()) {
      newErrors.id = 'Product ID is required';
    }
    
    if (!product.name.trim()) {
      newErrors.name = 'Product name is required';
    }
    
    if (!product.price.trim()) {
      newErrors.price = 'Price is required';
    } else if (isNaN(product.price)) {
      newErrors.price = 'Price must be a number';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);

    try {
      if (productToEdit) {
        // Update existing product
        await productService.updateProduct(product.id, product);
      } else {
        // Create new product
        await productService.createProduct(product);
      }
      
      // Reset form
      setProduct({
        id: '',
        name: '',
        description: '',
        price: ''
      });
      
      onSave();
    } catch (error) {
      console.error('Error saving product:', error);
      alert('Failed to save product. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancel = () => {
    setProduct({
      id: '',
      name: '',
      description: '',
      price: ''
    });
    setErrors({});
    onCancel();
  };

  return (
    <div className="product-form-container">
      <h2>{productToEdit ? 'Edit Product' : 'Add New Product'}</h2>
      <form onSubmit={handleSubmit} className="product-form">
        <div className="form-group">
          <label htmlFor="id">Product ID *</label>
          <input
            type="text"
            id="id"
            name="id"
            value={product.id}
            onChange={handleChange}
            disabled={!!productToEdit}
            className={errors.id ? 'error' : ''}
            placeholder="e.g., P001"
          />
          {errors.id && <span className="error-message">{errors.id}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="name">Product Name *</label>
          <input
            type="text"
            id="name"
            name="name"
            value={product.name}
            onChange={handleChange}
            className={errors.name ? 'error' : ''}
            placeholder="e.g., Laptop"
          />
          {errors.name && <span className="error-message">{errors.name}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="description">Description</label>
          <textarea
            id="description"
            name="description"
            value={product.description}
            onChange={handleChange}
            placeholder="Product description"
            rows="3"
          />
        </div>

        <div className="form-group">
          <label htmlFor="price">Price *</label>
          <input
            type="text"
            id="price"
            name="price"
            value={product.price}
            onChange={handleChange}
            className={errors.price ? 'error' : ''}
            placeholder="e.g., 99.99"
          />
          {errors.price && <span className="error-message">{errors.price}</span>}
        </div>

        <div className="form-actions">
          <button 
            type="submit" 
            className="btn btn-primary"
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Saving...' : (productToEdit ? 'Update Product' : 'Add Product')}
          </button>
          <button 
            type="button" 
            className="btn btn-secondary"
            onClick={handleCancel}
            disabled={isSubmitting}
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
};

export default ProductForm;
