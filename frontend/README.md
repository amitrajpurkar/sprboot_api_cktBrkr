# Product Management Frontend

A modern, responsive React-based Single Page Application (SPA) for managing products through a RESTful API.

## 🎯 Features

- **Product List View**: Display all products in a clean, tabular layout
- **Add New Product**: Create new products with validation
- **Edit Product**: Update existing product information
- **Delete Product**: Remove products with confirmation dialog
- **Responsive Design**: Works seamlessly on desktop, tablet, and mobile devices
- **Real-time Updates**: Automatically refreshes product list after operations
- **Error Handling**: User-friendly error messages and retry options
- **Form Validation**: Client-side validation for required fields

## 🛠️ Technology Stack

- **React 18.2.0**: Modern UI library with hooks
- **Axios**: HTTP client for API communication
- **CSS3**: Custom styling with responsive design
- **React Scripts**: Build tooling and development server

## 📋 Prerequisites

Before running the frontend, ensure you have:

- **Node.js**: Version 14.x or higher
- **npm**: Version 6.x or higher (comes with Node.js)
- **Backend API**: Spring Boot application running on `http://localhost:8080`

## 🚀 Getting Started

### 1. Install Dependencies

```bash
cd frontend
npm install
```

### 2. Start the Development Server

```bash
npm start
```

The application will automatically open in your browser at `http://localhost:3000`.

### 3. Ensure Backend is Running

Make sure the Spring Boot backend is running on port 8080:

```bash
# From the project root directory
./gradlew bootRun
```

## 📁 Project Structure

```
frontend/
├── public/
│   └── index.html              # HTML template
├── src/
│   ├── components/
│   │   ├── ProductForm.js      # Form component for add/edit
│   │   ├── ProductForm.css     # Form styling
│   │   ├── ProductList.js      # Table component for listing products
│   │   └── ProductList.css     # Table styling
│   ├── services/
│   │   └── productService.js   # API service layer
│   ├── App.js                  # Main application component
│   ├── App.css                 # Main application styling
│   ├── index.js                # Application entry point
│   └── index.css               # Global styles
├── package.json                # Dependencies and scripts
└── README.md                   # This file
```

## 🎨 User Interface

### Main Features

#### 1. Product List Table
- Displays all products with columns: ID, Name, Description, Price
- Each row has Edit and Delete action buttons
- Shows product count in the header
- Empty state message when no products exist
- Loading indicator during data fetch

#### 2. Add New Product
- Click "➕ Add New Product" button to open the form
- Fill in required fields: ID, Name, Price
- Optional description field
- Form validation with error messages
- Cancel button to close form without saving

#### 3. Edit Product
- Click "✏️ Edit" button on any product row
- Form pre-populates with existing product data
- Product ID is disabled (cannot be changed)
- Update button saves changes
- Cancel button discards changes

#### 4. Delete Product
- Click "🗑️ Delete" button on any product row
- Confirmation dialog prevents accidental deletion
- Product is removed immediately upon confirmation

## 🔌 API Integration

The frontend communicates with the backend through the following endpoints:

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/products` | Fetch all products |
| GET | `/api/v1/products/{id}` | Fetch single product |
| POST | `/api/v1/products` | Create new product |
| PUT | `/api/v1/products/{id}` | Update existing product |
| DELETE | `/api/v1/products/{id}` | Delete product |

### API Service (`productService.js`)

```javascript
import productService from './services/productService';

// Get all products
productService.getAllProducts();

// Create product
productService.createProduct(product);

// Update product
productService.updateProduct(id, product);

// Delete product
productService.deleteProduct(id);
```

## 📱 Responsive Design

The application is fully responsive and optimized for:

- **Desktop**: Full-width table with all features
- **Tablet**: Adjusted spacing and button sizes
- **Mobile**: Stacked layout, condensed table, vertical action buttons

## 🎨 Styling

### Color Scheme
- **Primary**: Purple gradient (#667eea to #764ba2)
- **Success**: Green (#4CAF50)
- **Edit**: Blue (#2196F3)
- **Delete**: Red (#f44336)
- **Background**: White with subtle shadows

### Design Principles
- Clean, modern interface
- Consistent spacing and typography
- Smooth transitions and hover effects
- Clear visual hierarchy
- Accessible color contrasts

## 🧪 Available Scripts

### `npm start`
Runs the app in development mode at [http://localhost:3000](http://localhost:3000).
- Hot reload enabled
- Proxies API requests to `http://localhost:8080`

### `npm build`
Builds the app for production to the `build` folder.
- Optimized and minified
- Ready for deployment

### `npm test`
Launches the test runner in interactive watch mode.

## 🔧 Configuration

### Proxy Configuration
The `package.json` includes a proxy setting to forward API requests to the backend:

```json
"proxy": "http://localhost:8080"
```

This allows the frontend to make requests to `/api/v1/products` which are automatically forwarded to `http://localhost:8080/api/v1/products`.

### Changing Backend URL
If your backend runs on a different port, update the proxy in `package.json`:

```json
"proxy": "http://localhost:YOUR_PORT"
```

## 🐛 Troubleshooting

### Issue: "Failed to load products"
**Solution**: Ensure the Spring Boot backend is running on port 8080.

```bash
# Check if backend is running
curl http://localhost:8080/api/v1/products

# Start backend if not running
./gradlew bootRun
```

### Issue: CORS errors
**Solution**: The proxy configuration should handle this. If issues persist, ensure Spring Boot has CORS configured properly.

### Issue: Port 3000 already in use
**Solution**: Either stop the process using port 3000 or run on a different port:

```bash
PORT=3001 npm start
```

## 🚀 Deployment

### Build for Production

```bash
npm run build
```

This creates an optimized production build in the `build` folder.

### Deployment Options

1. **Static Hosting** (Netlify, Vercel, GitHub Pages)
   - Upload the `build` folder
   - Configure API proxy/CORS on backend

2. **Serve with Backend**
   - Copy `build` folder contents to Spring Boot's `static` folder
   - Access at `http://localhost:8080`

3. **Docker**
   - Create Dockerfile for React app
   - Use nginx to serve static files

## 📝 Best Practices Implemented

✅ **Component-based architecture**: Reusable, maintainable components  
✅ **Separation of concerns**: Services layer for API calls  
✅ **State management**: React hooks (useState, useEffect)  
✅ **Error handling**: Try-catch blocks with user feedback  
✅ **Form validation**: Client-side validation before submission  
✅ **User confirmation**: Confirmation dialogs for destructive actions  
✅ **Loading states**: Visual feedback during async operations  
✅ **Responsive design**: Mobile-first approach  
✅ **Clean code**: Consistent formatting and naming conventions  

## 🔮 Future Enhancements

Potential features to add:

- [ ] Search and filter products
- [ ] Pagination for large product lists
- [ ] Sort by columns (name, price, etc.)
- [ ] Bulk operations (delete multiple products)
- [ ] Product categories/tags
- [ ] Image upload for products
- [ ] Export to CSV/Excel
- [ ] Dark mode toggle
- [ ] User authentication
- [ ] Advanced form validation (regex patterns)

## 📄 License

This project is part of the Spring Boot API with Circuit Breaker application.

## 👥 Contributing

1. Follow the existing code style
2. Test all changes thoroughly
3. Update documentation as needed
4. Ensure responsive design is maintained

---

**Last Updated**: October 22, 2025  
**Version**: 1.0.0  
**Status**: ✅ Production Ready
