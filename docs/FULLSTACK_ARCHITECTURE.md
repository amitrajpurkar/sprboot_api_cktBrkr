# Full Stack Architecture - Product Management System

## System Overview

A complete full-stack application with React frontend and Spring Boot backend for managing products.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                         FRONTEND                             │
│                      React SPA (Port 3000)                   │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   App.js     │  │ ProductList  │  │ ProductForm  │      │
│  │  (Main App)  │  │  Component   │  │  Component   │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                  │                  │               │
│         └──────────────────┴──────────────────┘              │
│                            │                                  │
│                  ┌─────────▼─────────┐                       │
│                  │  productService   │                       │
│                  │   (API Client)    │                       │
│                  └─────────┬─────────┘                       │
└────────────────────────────┼─────────────────────────────────┘
                             │
                    HTTP/REST API
                    (JSON over HTTP)
                             │
┌────────────────────────────▼─────────────────────────────────┐
│                         BACKEND                               │
│                Spring Boot API (Port 8080)                    │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │           ProductController                           │   │
│  │  GET    /api/v1/products                             │   │
│  │  GET    /api/v1/products/{id}                        │   │
│  │  POST   /api/v1/products                             │   │
│  │  PUT    /api/v1/products/{id}                        │   │
│  │  DELETE /api/v1/products/{id}                        │   │
│  └──────────────────┬───────────────────────────────────┘   │
│                     │                                         │
│  ┌──────────────────▼───────────────────────────────────┐   │
│  │           ProductService                              │   │
│  │  - findAll()                                          │   │
│  │  - findById(id)                                       │   │
│  │  - saveOne(product)                                   │   │
│  │  - updateProduct(id, product)                         │   │
│  │  - deleteProduct(id)                                  │   │
│  └──────────────────┬───────────────────────────────────┘   │
│                     │                                         │
│  ┌──────────────────▼───────────────────────────────────┐   │
│  │         ProductRepository (JPA)                       │   │
│  │  - JpaRepository<Product, String>                     │   │
│  └──────────────────┬───────────────────────────────────┘   │
│                     │                                         │
│  ┌──────────────────▼───────────────────────────────────┐   │
│  │              H2 Database                              │   │
│  │  (In-Memory, Embedded)                                │   │
│  │  Table: products                                      │   │
│  └───────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## Technology Stack

### Frontend
| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | React | 18.2.0 |
| HTTP Client | Axios | 1.6.0 |
| Build Tool | React Scripts | 5.0.1 |
| Styling | CSS3 | - |
| State Management | React Hooks | - |

### Backend
| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 3.2.10 |
| Language | Java | 21 |
| Build Tool | Gradle | 9.1.0 |
| Database | H2 | 2.2.224 |
| ORM | JPA/Hibernate | - |
| API Docs | OpenAPI/Swagger | 2.3.0 |
| Circuit Breaker | Resilience4j | 2.2.0 |

## Data Flow

### 1. List Products
```
User → ProductList → productService.getAllProducts()
  → GET /api/v1/products → ProductController.getAllProducts()
  → ProductService.findAll() → ProductRepository.findAll()
  → H2 Database → Response → Display in Table
```

### 2. Create Product
```
User → ProductForm → productService.createProduct(product)
  → POST /api/v1/products → ProductController.createProduct()
  → ProductService.saveOne() → ProductRepository.save()
  → H2 Database → Response → Refresh List
```

### 3. Update Product
```
User → ProductForm → productService.updateProduct(id, product)
  → PUT /api/v1/products/{id} → ProductController.updateProduct()
  → ProductService.updateProduct() → ProductRepository.save()
  → H2 Database → Response → Refresh List
```

### 4. Delete Product
```
User → ProductList → productService.deleteProduct(id)
  → DELETE /api/v1/products/{id} → ProductController.deleteProduct()
  → ProductService.deleteProduct() → ProductRepository.deleteById()
  → H2 Database → Response → Refresh List
```

## API Endpoints

| Method | Endpoint | Request Body | Response | Status Codes |
|--------|----------|--------------|----------|--------------|
| GET | `/api/v1/products` | - | Product[] | 200 |
| GET | `/api/v1/products/{id}` | - | Product | 200, 404 |
| POST | `/api/v1/products` | Product | Product | 201, 400 |
| PUT | `/api/v1/products/{id}` | Product | Product | 200, 404 |
| DELETE | `/api/v1/products/{id}` | - | - | 204, 404 |

## Data Model

### Product Entity
```java
{
  "id": "string",          // Primary key
  "name": "string",        // Product name
  "description": "string", // Optional description
  "price": "string"        // Price as string
}
```

## Component Breakdown

### Frontend Components

#### 1. App.js (Main Application)
- **Responsibilities**: 
  - Application state management
  - Component orchestration
  - API error handling
- **State**: products, loading, showForm, productToEdit, error
- **Key Functions**: loadProducts(), handleAddNew(), handleEdit(), handleDelete()

#### 2. ProductList.js (Table Component)
- **Responsibilities**:
  - Display products in table format
  - Provide edit/delete actions
  - Handle loading and empty states
- **Props**: products, onEdit, onDelete, loading
- **Features**: Responsive table, confirmation dialogs, loading indicator

#### 3. ProductForm.js (Form Component)
- **Responsibilities**:
  - Create new products
  - Edit existing products
  - Form validation
- **Props**: productToEdit, onSave, onCancel
- **Features**: Validation, error messages, dual mode (add/edit)

#### 4. productService.js (API Service)
- **Responsibilities**:
  - Centralized API communication
  - HTTP request handling
  - Error propagation
- **Methods**: getAllProducts(), createProduct(), updateProduct(), deleteProduct()

### Backend Components

#### 1. ProductController
- **Responsibilities**: Handle HTTP requests, validate input, return responses
- **Annotations**: @RestController, @RequestMapping, @GetMapping, @PostMapping, @PutMapping, @DeleteMapping
- **Features**: OpenAPI documentation, proper HTTP status codes

#### 2. ProductService
- **Responsibilities**: Business logic, data transformation
- **Annotations**: @Service
- **Features**: Error handling, data validation

#### 3. ProductRepository
- **Responsibilities**: Database operations
- **Type**: JpaRepository<Product, String>
- **Features**: CRUD operations, custom queries

## Security Considerations

### Current Implementation
- Spring Security enabled
- Basic authentication configured
- CORS handled via proxy

### Future Enhancements
- JWT authentication
- Role-based access control
- API rate limiting
- Input sanitization

## Performance Optimization

### Frontend
- Component memoization
- Lazy loading
- Code splitting
- Production build optimization

### Backend
- Database indexing
- Query optimization
- Caching (future)
- Connection pooling

## Testing Strategy

### Frontend Testing
- Component unit tests (Jest)
- Integration tests
- E2E tests (Cypress/Playwright)

### Backend Testing
- Controller tests (MockMvc) ✅
- Service tests (Mockito) ✅
- Repository tests
- Integration tests

## Deployment Options

### Development
```bash
# Backend
./gradlew bootRun

# Frontend
cd frontend && npm start
```

### Production

#### Option 1: Separate Deployment
- Frontend: Netlify/Vercel
- Backend: Cloud platform (AWS/Azure/GCP)

#### Option 2: Bundled Deployment
- Build frontend: `npm run build`
- Copy to Spring Boot static folder
- Deploy as single JAR

#### Option 3: Docker
- Create Docker images for both
- Use docker-compose for orchestration

## Monitoring & Observability

### Backend
- Spring Boot Actuator endpoints
- Health checks: `/probe/readiness`, `/probe/liveness`
- Metrics collection
- Resilience4j circuit breaker

### Frontend
- Error boundary handling
- User-friendly error messages
- Loading states
- Network error detection

## Future Enhancements

### Frontend
- [ ] Search and filter functionality
- [ ] Pagination
- [ ] Sort by columns
- [ ] Bulk operations
- [ ] Image upload
- [ ] Dark mode

### Backend
- [ ] Advanced search API
- [ ] Pagination support
- [ ] File upload endpoints
- [ ] Batch operations API
- [ ] WebSocket for real-time updates

### Infrastructure
- [ ] CI/CD pipeline
- [ ] Automated testing
- [ ] Performance monitoring
- [ ] Log aggregation
- [ ] Container orchestration

---

**Architecture Version**: 1.0.0  
**Last Updated**: October 22, 2025  
**Status**: ✅ Production Ready
