package com.example.admin.service

import com.example.admin.dto.response.*
import com.example.admin.entity.OrderStatus
import com.example.admin.repository.OrderRepository
import com.example.admin.repository.ProductRepository
import com.example.admin.repository.UserRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class DashboardService(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository
) {
    
    fun getStats(): DashboardStatsResponse {
        val totalUsers = userRepository.count()
        val totalProducts = productRepository.count()
        val totalOrders = orderRepository.count()
        
        val orders = orderRepository.findAll()
        val totalRevenue = orders
            .filter { it.status != OrderStatus.CANCELLED }
            .sumOf { it.totalAmount }
        
        val pendingOrders = orders.count { it.status == OrderStatus.PENDING }.toLong()
        val activeProducts = productRepository.findAll().count { it.active }.toLong()
        val lowStockProducts = productRepository.findAll().count { it.stock < 10 }.toLong()
        
        return DashboardStatsResponse(
            totalUsers = totalUsers,
            totalProducts = totalProducts,
            totalOrders = totalOrders,
            totalRevenue = totalRevenue,
            pendingOrders = pendingOrders,
            activeProducts = activeProducts,
            lowStockProducts = lowStockProducts
        )
    }
    
    fun getRevenueAnalytics(): RevenueAnalyticsResponse {
        val now = LocalDateTime.now()
        val thirtyDaysAgo = now.minusDays(30)
        val twelveMonthsAgo = now.minusMonths(12)
        
        val recentOrders = orderRepository.findOrdersBetweenDates(thirtyDaysAgo, now)
            .filter { it.status != OrderStatus.CANCELLED }
        
        val yearlyOrders = orderRepository.findOrdersBetweenDates(twelveMonthsAgo, now)
            .filter { it.status != OrderStatus.CANCELLED }
        
        // Daily revenue (last 30 days)
        val dailyRevenue = recentOrders
            .groupBy { it.createdAt.toLocalDate() }
            .map { (date, orders) ->
                RevenueData(
                    date = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    revenue = orders.sumOf { it.totalAmount },
                    orderCount = orders.size
                )
            }
            .sortedBy { it.date }
        
        // Monthly revenue (last 12 months)
        val monthlyRevenue = yearlyOrders
            .groupBy { it.createdAt.toLocalDate().withDayOfMonth(1) }
            .map { (date, orders) ->
                RevenueData(
                    date = date.format(DateTimeFormatter.ofPattern("yyyy-MM")),
                    revenue = orders.sumOf { it.totalAmount },
                    orderCount = orders.size
                )
            }
            .sortedBy { it.date }
        
        // Revenue by category
        val totalCategoryRevenue = yearlyOrders
            .flatMap { order -> order.items }
            .groupBy { it.product.category }
            .mapValues { (_, items) -> 
                items.sumOf { it.price.multiply(BigDecimal(it.quantity)) }
            }
        
        val totalRevenue = totalCategoryRevenue.values.sumOf { it }
        
        val categoryRevenue = totalCategoryRevenue.map { (category, revenue) ->
            CategoryRevenue(
                category = category,
                revenue = revenue,
                percentage = if (totalRevenue > BigDecimal.ZERO) {
                    revenue.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal(100))
                        .toDouble()
                } else 0.0
            )
        }.sortedByDescending { it.revenue }
        
        return RevenueAnalyticsResponse(
            daily = dailyRevenue,
            monthly = monthlyRevenue,
            byCategory = categoryRevenue
        )
    }
    
    fun getTopProducts(limit: Int = 10): TopProductsResponse {
        val orders = orderRepository.findAll()
            .filter { it.status != OrderStatus.CANCELLED }
        
        val productStats = orders
            .flatMap { it.items }
            .groupBy { it.product }
            .map { (product, items) ->
                TopProductData(
                    product = ProductResponse.from(product),
                    soldQuantity = items.sumOf { it.quantity },
                    revenue = items.sumOf { it.price.multiply(BigDecimal(it.quantity)) }
                )
            }
            .sortedByDescending { it.revenue }
            .take(limit)
        
        return TopProductsResponse(products = productStats)
    }
}