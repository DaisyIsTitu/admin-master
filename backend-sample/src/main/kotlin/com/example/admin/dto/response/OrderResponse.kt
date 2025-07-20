package com.example.admin.dto.response

import com.example.admin.entity.Order
import com.example.admin.entity.OrderItem
import com.example.admin.entity.OrderStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDateTime

@Schema(description = "Order response")
data class OrderResponse(
    @Schema(description = "Order ID", example = "1")
    val id: Long,
    
    @Schema(description = "Order number", example = "ORD-2024-00001")
    val orderNumber: String,
    
    @Schema(description = "User information")
    val user: UserResponse,
    
    @Schema(description = "Order status", example = "PENDING")
    val status: OrderStatus,
    
    @Schema(description = "Total amount", example = "4999.98")
    val totalAmount: BigDecimal,
    
    @Schema(description = "Shipping address", example = "123 Main St, New York, NY 10001")
    val shippingAddress: String,
    
    @Schema(description = "Order notes", example = "Please deliver between 9 AM - 5 PM")
    val notes: String?,
    
    @Schema(description = "Order items")
    val items: List<OrderItemResponse>,
    
    @Schema(description = "Order creation date")
    val createdAt: LocalDateTime,
    
    @Schema(description = "Last update date")
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(order: Order): OrderResponse {
            return OrderResponse(
                id = order.id!!,
                orderNumber = order.orderNumber,
                user = UserResponse.from(order.user),
                status = order.status,
                totalAmount = order.totalAmount,
                shippingAddress = order.shippingAddress,
                notes = order.notes,
                items = order.items.map { OrderItemResponse.from(it) },
                createdAt = order.createdAt,
                updatedAt = order.updatedAt
            )
        }
    }
}

@Schema(description = "Order item response")
data class OrderItemResponse(
    @Schema(description = "Item ID", example = "1")
    val id: Long,
    
    @Schema(description = "Product information")
    val product: ProductResponse,
    
    @Schema(description = "Quantity", example = "2")
    val quantity: Int,
    
    @Schema(description = "Price at time of order", example = "2499.99")
    val price: BigDecimal
) {
    companion object {
        fun from(item: OrderItem): OrderItemResponse {
            return OrderItemResponse(
                id = item.id!!,
                product = ProductResponse.from(item.product),
                quantity = item.quantity,
                price = item.price
            )
        }
    }
}