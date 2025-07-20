# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This project generates React admin dashboards from OpenAPI/Swagger specifications and serves them from the same Spring Boot backend server. It provides a complete full-stack solution with:

1. **backend-sample/**: Kotlin Spring Boot API with comprehensive CRUD operations, JWT auth, and OpenAPI docs
2. **frontend-generator/**: Node.js application that parses OpenAPI specs and generates React admin dashboard code
3. **generated-frontend/**: Generated React TypeScript admin dashboard (created during build)
4. **Docker configurations**: Production and development containerization

## Commands

### Quick Start (Integrated Build)
```bash
cd backend-sample
./gradlew buildFullstack  # Build generator + frontend + backend
./gradlew bootRun         # Run complete application on http://localhost:8080
```

### Development Commands
```bash
# Backend only
cd backend-sample
./gradlew bootRun         # Backend API only

# Frontend generator
cd frontend-generator
npm install && npm run build
npm run generate -- --url http://localhost:8080/api-docs --output ../generated-frontend

# Generated frontend development
cd generated-frontend
npm install && npm run dev  # Frontend dev server on http://localhost:3000
```

### Docker Commands
```bash
# Production deployment
docker-compose up --build

# Development environment
docker-compose -f docker-compose.dev.yml up --build
```

### Build System Commands
```bash
cd backend-sample
./gradlew installFrontendGenerator  # Install generator dependencies
./gradlew buildFrontendGenerator     # Build generator
./gradlew generateFrontend          # Generate frontend code
./gradlew buildFrontend             # Build frontend for production
./gradlew copyFrontendToResources   # Copy to Spring Boot static resources
./gradlew buildFullstack            # Complete build process
```

## Architecture

### Backend (Spring Boot + Kotlin)
- **Spring Boot 3.2.0** with Kotlin 1.9.20
- **Authentication**: JWT with refresh tokens, role-based access (ADMIN, MANAGER, USER)
- **Entities**: User, Product, Order with OrderItem relationships
- **Features**: CRUD operations, pagination, filtering, dashboard analytics
- **Security**: BCrypt passwords, JWT tokens, role-based endpoints
- **Documentation**: Complete OpenAPI/Swagger documentation
- **Static Serving**: Configured to serve generated frontend from `/static`

### Frontend Generator (Node.js + TypeScript)
- **OpenAPI Parsing**: Uses swagger-parser to process API specifications
- **Code Generation**: Handlebars templates for React components
- **Output**: Complete TypeScript React application with:
  - Type-safe API interfaces
  - CRUD components (tables, forms, modals)
  - Authentication flow with JWT handling
  - Routing with React Router
  - Material-UI/Ant Design/Chakra UI support
  - Role-based access control

### Generated Frontend (React + TypeScript)
- **Stack**: React 18, TypeScript, Vite, React Router
- **State Management**: React Query for server state
- **Forms**: React Hook Form with validation
- **UI Libraries**: Material-UI (default), Ant Design, or Chakra UI
- **Authentication**: JWT token management with automatic refresh
- **Features**: Data tables, forms, dashboard, responsive design

### Build Integration
- **Gradle Tasks**: Frontend generation integrated into backend build
- **Static Resources**: Frontend build copied to Spring Boot static directory
- **Single Server**: Frontend served from backend (no CORS issues)
- **Docker**: Multi-stage builds for production deployment

## Key Files & Configurations

### Backend Configuration
- `application.yml`: JWT settings, database config, server settings
- `SecurityConfig.kt`: Spring Security with JWT and static resource handling
- `WebConfig.kt`: Static resource serving and SPA routing support
- `build.gradle.kts`: Frontend build integration with Node.js tasks

### Frontend Generator
- `cli.ts`: Command-line interface for generation
- `openapi-parser.ts`: OpenAPI specification parsing logic
- `frontend-generator.ts`: Main generation orchestrator
- `templates/`: Handlebars templates for all generated code

### Docker & Deployment
- `Dockerfile`: Multi-stage production build
- `Dockerfile.dev`: Development environment
- `docker-compose.yml`: Production deployment
- `docker-compose.dev.yml`: Development with hot reloading

## Default Users & Access

| Username | Password | Role | Access |
|----------|----------|------|--------|
| admin | password123 | ADMIN | All endpoints |
| manager | password123 | MANAGER | Dashboard + Products/Orders |
| john.doe | password123 | USER | Limited access |
| jane.smith | password123 | USER | Limited access |

## Development Workflow

### Adding New Entities
1. Create entity, repository, service, and controller in backend
2. Add proper OpenAPI annotations
3. Regenerate frontend: `./gradlew generateFrontend`
4. Rebuild: `./gradlew buildFullstack`

### Customizing Generated Code
- Modify templates in `frontend-generator/templates/`
- Update generation logic in `frontend-generator/src/`
- Rebuild generator: `npm run build`

### Testing
```bash
# Backend tests
cd backend-sample && ./gradlew test

# Frontend tests (if generated with test support)
cd generated-frontend && npm test
```

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Refresh token
- `POST /api/auth/logout` - Logout

### Management (Role-based)
- `GET/POST/PUT/DELETE /api/users` - User management (ADMIN)
- `GET/POST/PUT/DELETE /api/products` - Product management
- `GET/POST/PUT/DELETE /api/orders` - Order management
- `GET /api/dashboard/*` - Analytics (ADMIN/MANAGER)

### Documentation
- `/swagger-ui.html` - Interactive API documentation
- `/api-docs` - OpenAPI JSON specification

## Production Deployment

### Docker Deployment
```bash
# Single command deployment
docker-compose up --build -d

# View logs
docker-compose logs -f admin-app

# Scale application
docker-compose up --scale admin-app=3 -d
```

### Manual JAR Deployment
```bash
cd backend-sample
./gradlew buildFullstack
java -jar build/libs/admin-api-0.0.1-SNAPSHOT.jar
```

## Environment Configuration

Set these environment variables for production:
- `SPRING_PROFILES_ACTIVE`: Active Spring profile
- `JWT_SECRET`: JWT signing secret (should be long and secure)
- `JWT_EXPIRATION`: Token expiration time in milliseconds
- `DATABASE_URL`: External database URL (if not using H2)

## Troubleshooting

### Common Issues
- **Port conflicts**: Ensure 8080 (backend) and 3000 (frontend dev) are available
- **Node.js version**: Requires Node.js 18+ for frontend generation
- **Memory**: Increase JVM heap for large projects: `-Xmx2g`
- **Permissions**: Make gradlew executable: `chmod +x gradlew`

### Build Problems
- Clean build: `./gradlew clean buildFullstack`
- Check generated frontend: `ls -la generated-frontend/`
- Verify static resources: `ls -la backend-sample/src/main/resources/static/`