import React from 'react';
import './ProductList.css';

const ProductList = ({ products, onEdit, onDelete, loading }) => {
  const handleDelete = (product) => {
    if (window.confirm(`Are you sure you want to delete "${product.name}"?`)) {
      onDelete(product.id);
    }
  };

  if (loading) {
    return <div className="loading">Loading products...</div>;
  }

  if (products.length === 0) {
    return (
      <div className="no-products">
        <p>No products found. Add your first product above!</p>
      </div>
    );
  }

  return (
    <div className="product-list-container">
      <h2>Product List ({products.length})</h2>
      <div className="table-responsive">
        <table className="product-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Description</th>
              <th>Price</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {products.map((product) => (
              <tr key={product.id}>
                <td className="product-id">{product.id}</td>
                <td className="product-name">{product.name}</td>
                <td className="product-description">
                  {product.description || <span className="no-data">No description</span>}
                </td>
                <td className="product-price">${product.price}</td>
                <td className="product-actions">
                  <button
                    className="btn btn-edit"
                    onClick={() => onEdit(product)}
                    title="Edit product"
                  >
                    ✏️ Edit
                  </button>
                  <button
                    className="btn btn-delete"
                    onClick={() => handleDelete(product)}
                    title="Delete product"
                  >
                    🗑️ Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ProductList;
