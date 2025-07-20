# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This project generates frontend code for admin dashboards based on OpenAPI/Swagger specifications from backend APIs. It consists of:

1. **backend-sample/**: A Kotlin Spring Boot API serving as an example backend with comprehensive CRUD operations, JWT auth, and OpenAPI docs
2. **frontend-generator/**: (To be created) Node.js application that parses OpenAPI specs and generates React admin dashboard code
3. **generated-frontend/**: (Output directory) Where the generated frontend code will be placed

## Commands

### Backend Sample
```bash
cd backend-sample
./gradlew bootRun  # Run the backend API (requires Gradle wrapper initialization)
./gradlew build    # Build the project
./gradlew test     # Run tests
```

The backend runs on http://localhost:8080 with Swagger UI at http://localhost:8080/swagger-ui.html

### Frontend Generator (To be implemented)
```bash
cd frontend-generator
npm install        # Install dependencies
npm run generate   # Generate frontend from Swagger URL
npm test          # Run tests
```

## Architecture

### Backend Sample Structure
- Spring Boot 3.2.0 with Kotlin
- JWT authentication with role-based access (ADMIN, MANAGER, USER)
- Entities: User, Product, Order (with OrderItem)
- Full CRUD operations with pagination and filtering
- OpenAPI documentation via springdoc-openapi

### Frontend Generator Design (Planned)
The generator will:
1. Fetch and parse OpenAPI/Swagger JSON from a URL
2. Generate TypeScript interfaces from schemas
3. Create API client services using Axios
4. Generate React components for each entity (tables, forms, modals)
5. Set up routing with React Router
6. Include Material-UI or Ant Design components
7. Provide basic state management

Key features to generate:
- Authentication flow with JWT token management
- CRUD interfaces for each entity
- Data tables with pagination, sorting, and filtering
- Forms with validation based on OpenAPI constraints
- Dashboard with statistics visualization
- Role-based access control in UI

## Development Guidelines

When implementing the frontend generator:
1. Use TypeScript for type safety
2. Follow OpenAPI specification strictly for data types and validation
3. Generate clean, maintainable code with proper formatting
4. Include error handling and loading states
5. Make generated code customizable through templates
6. Ensure generated code follows React best practices