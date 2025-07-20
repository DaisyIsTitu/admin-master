# Admin Dashboard Backend API

A comprehensive backend API for an admin dashboard built with Kotlin and Spring Boot, featuring full CRUD operations, JWT authentication, and OpenAPI documentation.

## Features

- **Authentication & Authorization**
  - JWT-based authentication
  - Role-based access control (ADMIN, MANAGER, USER)
  - Token refresh mechanism

- **User Management**
  - Full CRUD operations for users
  - Role assignment
  - Account activation/deactivation

- **Product Management**
  - Product CRUD operations
  - Stock management
  - Category filtering
  - Active/inactive status

- **Order Management**
  - Order creation with multiple items
  - Status tracking (PENDING → PROCESSING → SHIPPED → DELIVERED)
  - Order cancellation with stock restoration

- **Dashboard Analytics**
  - Real-time statistics
  - Revenue analytics (daily, monthly, by category)
  - Top selling products

## Tech Stack

- Kotlin 1.9.20
- Spring Boot 3.2.0
- Spring Security with JWT
- Spring Data JPA
- H2 Database (in-memory)
- springdoc-openapi (Swagger UI)

## Getting Started

### Prerequisites

- JDK 17 or later
- Gradle 7.x or later

### Running the Application

1. Clone the repository:
```bash
cd backend-sample
```

2. Run the application:
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### Accessing the API

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **H2 Console**: http://localhost:8080/h2-console (username: sa, no password)

### Default Users

The application comes with pre-configured users:

| Username | Password | Role |
|----------|----------|------|
| admin | password123 | ADMIN |
| manager | password123 | MANAGER |
| john.doe | password123 | USER |
| jane.smith | password123 | USER |

### Authentication

1. Login at `/api/auth/login` with username and password
2. Use the returned `accessToken` in the Authorization header: `Bearer <token>`
3. Refresh tokens at `/api/auth/refresh` when needed

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh token
- `POST /api/auth/logout` - Logout

### Users (Admin only)
- `GET /api/users` - List all users (paginated)
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Products
- `GET /api/products` - List all products (paginated, filterable)
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Create new product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product

### Orders
- `GET /api/orders` - List all orders (paginated, filterable by status)
- `GET /api/orders/{id}` - Get order by ID
- `POST /api/orders` - Create new order
- `PUT /api/orders/{id}/status` - Update order status
- `DELETE /api/orders/{id}` - Cancel order

### Dashboard (Admin/Manager only)
- `GET /api/dashboard/stats` - Get dashboard statistics
- `GET /api/dashboard/revenue` - Get revenue analytics
- `GET /api/dashboard/top-products` - Get top selling products

## Building for Production

```bash
./gradlew build
```

The JAR file will be created in `build/libs/`

## Configuration

Key configuration properties in `application.yml`:
- JWT secret and expiration times
- Database settings
- Server port
- Logging levels

## Security Features

- BCrypt password encoding
- JWT token validation
- CORS configuration
- Role-based endpoint access
- SQL injection prevention through parameterized queries

## Error Handling

The API uses consistent error responses:
```json
{
  "status": "error",
  "message": "Error description",
  "data": null
}
```

HTTP status codes:
- 200: Success
- 201: Created
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 409: Conflict
- 500: Internal Server Error