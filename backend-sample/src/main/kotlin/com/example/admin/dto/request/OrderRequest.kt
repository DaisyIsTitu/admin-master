package com.example.admin.dto.request

import com.example.admin.entity.OrderStatus
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

@Schema(description = "Create order request")
data class CreateOrderRequest(
    @field:NotNull(message = "User ID is required")
    @Schema(description = "User ID", example = "1")
    val userId: Long,
    
    @field:NotBlank(message = "Shipping address is required")
    @Schema(description = "Shipping address", example = "123 Main St, New York, NY 10001")
    val shippingAddress: String,
    
    @Schema(description = "Order notes", example = "Please deliver between 9 AM - 5 PM")
    val notes: String?,
    
    @field:NotEmpty(message = "Order must have at least one item")
    @Schema(description = "Order items")
    val items: List<OrderItemRequest>
)

@Schema(description = "Order item request")
data class OrderItemRequest(
    @field:NotNull(message = "Product ID is required")
    @Schema(description = "Product ID", example = "1")
    val productId: Long,
    
    @field:NotNull(message = "Quantity is required")
    @field:Min(value = 1, message = "Quantity must be at least 1")
    @Schema(description = "Quantity", example = "2")
    val quantity: Int
)

@Schema(description = "Update order status request")
data class UpdateOrderStatusRequest(
    @field:NotNull(message = "Status is required")
    @Schema(description = "New order status", example = "PROCESSING")
    val status: OrderStatus
)