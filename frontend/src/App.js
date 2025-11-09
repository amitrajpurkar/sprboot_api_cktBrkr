import React, { useState, useEffect } from 'react';
import ProductForm from './components/ProductForm';
import ProductList from './components/ProductList';
import productService from './services/productService';
import './App.css';

function App() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [productToEdit, setProductToEdit] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await productService.getAllProducts();
      setProducts(response.data);
    } catch (error) {
      console.error('Error loading products:', error);
      setError('Failed to load products. Please make sure the backend server is running.');
    } finally {
      setLoading(false);
    }
  };

  const handleAddNew = () => {
    setProductToEdit(null);
    setShowForm(true);
  };

  const handleEdit = (product) => {
    setProductToEdit(product);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    try {
      await productService.deleteProduct(id);
      await loadProducts();
    } catch (error) {
      console.error('Error deleting product:', error);
      alert('Failed to delete product. Please try again.');
    }
  };

  const handleSave = async () => {
    setShowForm(false);
    setProductToEdit(null);
    await loadProducts();
  };

  const handleCancel = () => {
    setShowForm(false);
    setProductToEdit(null);
  };

  return (
    <div className="App">
      <header className="app-header">
        <h1>🛍️ Product Management System</h1>
        <p>Manage your product catalog with ease</p>
      </header>

      <main className="app-main">
        <div className="container">
          {error && (
            <div className="error-banner">
              <span>⚠️ {error}</span>
              <button onClick={loadProducts} className="retry-btn">Retry</button>
            </div>
          )}

          {!showForm && (
            <div className="add-product-section">
              <button 
                className="btn btn-add-new"
                onClick={handleAddNew}
              >
                ➕ Add New Product
              </button>
            </div>
          )}

          {showForm && (
            <ProductForm
              productToEdit={productToEdit}
              onSave={handleSave}
              onCancel={handleCancel}
            />
          )}

          <ProductList
            products={products}
            onEdit={handleEdit}
            onDelete={handleDelete}
            loading={loading}
          />
        </div>
      </main>

      <footer className="app-footer">
        <p>© 2025 Product Management System | Built with React & Spring Boot</p>
      </footer>
    </div>
  );
}

export default App;
