# Frontend Quick Start Guide

## 🚀 Get Started in 3 Steps

### Step 1: Start the Backend
```bash
# From project root
./gradlew bootRun
```
Backend will run at: http://localhost:8080

### Step 2: Install Frontend Dependencies
```bash
cd frontend
npm install
```

### Step 3: Start the Frontend
```bash
npm start
```
Frontend will open at: http://localhost:3000

## 🎯 What You'll See

### Main Interface
- **Header**: Product Management System title
- **Add Button**: "➕ Add New Product" button
- **Product Table**: List of all products with Edit/Delete actions
- **Footer**: Application information

### Using the Application

#### Add a New Product
1. Click "➕ Add New Product"
2. Fill in the form:
   - **Product ID** (required): e.g., "P001"
   - **Name** (required): e.g., "Laptop"
   - **Description** (optional): e.g., "High-performance laptop"
   - **Price** (required): e.g., "1299.99"
3. Click "Add Product"
4. Product appears in the table

#### Edit a Product
1. Click "✏️ Edit" button on any product row
2. Modify the fields (ID cannot be changed)
3. Click "Update Product"
4. Changes are saved

#### Delete a Product
1. Click "🗑️ Delete" button on any product row
2. Confirm deletion in the popup
3. Product is removed

## 📱 Features

✅ **Responsive Design** - Works on desktop, tablet, and mobile  
✅ **Real-time Updates** - List refreshes after each operation  
✅ **Form Validation** - Prevents invalid data entry  
✅ **Error Handling** - Clear error messages  
✅ **Confirmation Dialogs** - Prevents accidental deletions  
✅ **Loading States** - Visual feedback during operations  

## 🛠️ Technology

- **Frontend**: React 18.2.0
- **Backend**: Spring Boot 3.2.10
- **Database**: H2 (in-memory)
- **API**: RESTful endpoints

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/          # React components
│   ├── services/            # API integration
│   ├── App.js              # Main app
│   └── index.js            # Entry point
└── package.json            # Dependencies
```

## 🔧 Troubleshooting

### "Failed to load products"
- Ensure backend is running on port 8080
- Check: `curl http://localhost:8080/api/v1/products`

### Port 3000 already in use
```bash
PORT=3001 npm start
```

### CORS errors
- The proxy in package.json should handle this
- Backend must be running on localhost:8080

## 📚 Documentation

- **Frontend README**: `frontend/README.md`
- **Implementation Details**: `src/main/docs/FRONTEND_IMPLEMENTATION.md`
- **API Documentation**: Access Swagger at http://localhost:8080/swagger-ui.html

## 🎨 Screenshots

### Desktop View
- Full-width table with all features
- Large, prominent action buttons
- Clear spacing and typography

### Mobile View
- Responsive table layout
- Stacked action buttons
- Touch-friendly interface

## 🚢 Production Build

```bash
cd frontend
npm run build
```

Creates optimized build in `frontend/build/` directory.

---

**Need Help?** Check the detailed README in the `frontend/` directory.

**Last Updated**: October 22, 2025
