package com.example.admin.controller

import com.example.admin.dto.request.CreateProductRequest
import com.example.admin.dto.request.UpdateProductRequest
import com.example.admin.dto.response.ApiResponse
import com.example.admin.dto.response.PageResponse
import com.example.admin.dto.response.ProductResponse
import com.example.admin.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Management", description = "Product CRUD operations")
@SecurityRequirement(name = "bearerAuth")
class ProductController(
    private val productService: ProductService
) {
    
    @GetMapping
    @Operation(summary = "Get all products", description = "Get paginated list of products")
    fun getAllProducts(
        @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Page size") @RequestParam(defaultValue = "20") size: Int,
        @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") sortBy: String,
        @Parameter(description = "Sort direction") @RequestParam(defaultValue = "ASC") sortDirection: String,
        @Parameter(description = "Filter by category") @RequestParam(required = false) category: String?,
        @Parameter(description = "Show only active products") @RequestParam(defaultValue = "false") activeOnly: Boolean
    ): ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> {
        val pageable = PageRequest.of(
            page, 
            size, 
            Sort.by(Sort.Direction.valueOf(sortDirection), sortBy)
        )
        
        val products = when {
            category != null -> productService.getProductsByCategory(category, pageable)
            activeOnly -> productService.getActiveProducts(pageable)
            else -> productService.getAllProducts(pageable)
        }
        
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(products)))
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Get a specific product by its ID")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product found"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    )
    fun getProductById(@PathVariable id: Long): ResponseEntity<ApiResponse<ProductResponse>> {
        val product = productService.getProductById(id)
        return ResponseEntity.ok(ApiResponse.success(product))
    }
    
    @PostMapping
    @Operation(summary = "Create new product", description = "Create a new product")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Product created successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Product SKU already exists")
    )
    fun createProduct(@Valid @RequestBody request: CreateProductRequest): ResponseEntity<ApiResponse<ProductResponse>> {
        val product = productService.createProduct(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(product, "Product created successfully"))
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product updated successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    )
    fun updateProduct(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateProductRequest
    ): ResponseEntity<ApiResponse<ProductResponse>> {
        val product = productService.updateProduct(id, request)
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"))
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product deleted successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    )
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<ApiResponse<Nothing>> {
        productService.deleteProduct(id)
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"))
    }
}