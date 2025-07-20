package com.example.admin.controller

import com.example.admin.dto.response.*
import com.example.admin.service.DashboardService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard statistics and analytics")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
class DashboardController(
    private val dashboardService: DashboardService
) {
    
    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics", description = "Get overall statistics for the dashboard")
    fun getDashboardStats(): ResponseEntity<ApiResponse<DashboardStatsResponse>> {
        val stats = dashboardService.getStats()
        return ResponseEntity.ok(ApiResponse.success(stats))
    }
    
    @GetMapping("/revenue")
    @Operation(summary = "Get revenue analytics", description = "Get revenue analytics data including daily, monthly, and by category")
    fun getRevenueAnalytics(): ResponseEntity<ApiResponse<RevenueAnalyticsResponse>> {
        val analytics = dashboardService.getRevenueAnalytics()
        return ResponseEntity.ok(ApiResponse.success(analytics))
    }
    
    @GetMapping("/top-products")
    @Operation(summary = "Get top selling products", description = "Get list of best selling products")
    fun getTopProducts(
        @Parameter(description = "Number of products to return") @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<ApiResponse<TopProductsResponse>> {
        val topProducts = dashboardService.getTopProducts(limit)
        return ResponseEntity.ok(ApiResponse.success(topProducts))
    }
}