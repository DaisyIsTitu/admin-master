package com.example.admin.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

@Schema(description = "Create product request")
data class CreateProductRequest(
    @field:NotBlank(message = "Product name is required")
    @Schema(description = "Product name", example = "MacBook Pro 16")
    val name: String,
    
    @Schema(description = "Product description", example = "High-performance laptop for professionals")
    val description: String?,
    
    @field:NotBlank(message = "SKU is required")
    @Schema(description = "Stock Keeping Unit", example = "MBP-16-2023")
    val sku: String,
    
    @field:NotNull(message = "Price is required")
    @field:DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Schema(description = "Product price", example = "2499.99")
    val price: BigDecimal,
    
    @field:NotNull(message = "Stock is required")
    @field:Min(value = 0, message = "Stock cannot be negative")
    @Schema(description = "Available stock", example = "50")
    val stock: Int,
    
    @field:NotBlank(message = "Category is required")
    @Schema(description = "Product category", example = "Electronics")
    val category: String
)

@Schema(description = "Update product request")
data class UpdateProductRequest(
    @Schema(description = "Product name", example = "MacBook Pro 16")
    val name: String?,
    
    @Schema(description = "Product description", example = "High-performance laptop for professionals")
    val description: String?,
    
    @field:DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Schema(description = "Product price", example = "2499.99")
    val price: BigDecimal?,
    
    @field:Min(value = 0, message = "Stock cannot be negative")
    @Schema(description = "Available stock", example = "50")
    val stock: Int?,
    
    @Schema(description = "Product category", example = "Electronics")
    val category: String?,
    
    @Schema(description = "Product active status", example = "true")
    val active: Boolean?
)