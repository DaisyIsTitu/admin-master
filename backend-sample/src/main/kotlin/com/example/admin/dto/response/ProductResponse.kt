package com.example.admin.dto.response

import com.example.admin.entity.Product
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime

@Schema(description = "Product response")
data class ProductResponse(
    @Schema(description = "Product ID", example = "1")
    val id: Long,
    
    @Schema(description = "Product name", example = "MacBook Pro 16")
    val name: String,
    
    @Schema(description = "Product description", example = "High-performance laptop for professionals")
    val description: String?,
    
    @Schema(description = "Stock Keeping Unit", example = "MBP-16-2023")
    val sku: String,
    
    @Schema(description = "Product price", example = "2499.99")
    val price: BigDecimal,
    
    @Schema(description = "Available stock", example = "50")
    val stock: Int,
    
    @Schema(description = "Product category", example = "Electronics")
    val category: String,
    
    @Schema(description = "Product active status", example = "true")
    val active: Boolean,
    
    @Schema(description = "Creation date")
    val createdAt: LocalDateTime,
    
    @Schema(description = "Last update date")
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(product: Product): ProductResponse {
            return ProductResponse(
                id = product.id!!,
                name = product.name,
                description = product.description,
                sku = product.sku,
                price = product.price,
                stock = product.stock,
                category = product.category,
                active = product.active,
                createdAt = product.createdAt,
                updatedAt = product.updatedAt
            )
        }
    }
}