package com.example.admin.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "Dashboard statistics response")
data class DashboardStatsResponse(
    @Schema(description = "Total number of users", example = "1250")
    val totalUsers: Long,
    
    @Schema(description = "Total number of products", example = "350")
    val totalProducts: Long,
    
    @Schema(description = "Total number of orders", example = "5420")
    val totalOrders: Long,
    
    @Schema(description = "Total revenue", example = "125450.50")
    val totalRevenue: BigDecimal,
    
    @Schema(description = "Number of pending orders", example = "45")
    val pendingOrders: Long,
    
    @Schema(description = "Number of active products", example = "320")
    val activeProducts: Long,
    
    @Schema(description = "Low stock products (stock < 10)", example = "15")
    val lowStockProducts: Long
)

@Schema(description = "Revenue analytics response")
data class RevenueAnalyticsResponse(
    @Schema(description = "Daily revenue data")
    val daily: List<RevenueData>,
    
    @Schema(description = "Monthly revenue data")
    val monthly: List<RevenueData>,
    
    @Schema(description = "Revenue by category")
    val byCategory: List<CategoryRevenue>
)

@Schema(description = "Revenue data point")
data class RevenueData(
    @Schema(description = "Date or period", example = "2024-01-15")
    val date: String,
    
    @Schema(description = "Revenue amount", example = "5420.50")
    val revenue: BigDecimal,
    
    @Schema(description = "Number of orders", example = "42")
    val orderCount: Int
)

@Schema(description = "Category revenue data")
data class CategoryRevenue(
    @Schema(description = "Category name", example = "Electronics")
    val category: String,
    
    @Schema(description = "Revenue amount", example = "45250.00")
    val revenue: BigDecimal,
    
    @Schema(description = "Percentage of total revenue", example = "35.5")
    val percentage: Double
)

@Schema(description = "Top products response")
data class TopProductsResponse(
    @Schema(description = "List of top selling products")
    val products: List<TopProductData>
)

@Schema(description = "Top product data")
data class TopProductData(
    @Schema(description = "Product information")
    val product: ProductResponse,
    
    @Schema(description = "Total quantity sold", example = "125")
    val soldQuantity: Int,
    
    @Schema(description = "Total revenue from this product", example = "312375.00")
    val revenue: BigDecimal
)