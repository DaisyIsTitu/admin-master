package com.example.admin.controller

import com.example.admin.dto.request.CreateOrderRequest
import com.example.admin.dto.request.UpdateOrderStatusRequest
import com.example.admin.dto.response.ApiResponse
import com.example.admin.dto.response.OrderResponse
import com.example.admin.dto.response.PageResponse
import com.example.admin.entity.OrderStatus
import com.example.admin.service.OrderService
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
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "Order CRUD operations")
@SecurityRequirement(name = "bearerAuth")
class OrderController(
    private val orderService: OrderService
) {
    
    @GetMapping
    @Operation(summary = "Get all orders", description = "Get paginated list of orders")
    fun getAllOrders(
        @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Page size") @RequestParam(defaultValue = "20") size: Int,
        @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") sortBy: String,
        @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") sortDirection: String,
        @Parameter(description = "Filter by status") @RequestParam(required = false) status: OrderStatus?
    ): ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> {
        val pageable = PageRequest.of(
            page, 
            size, 
            Sort.by(Sort.Direction.valueOf(sortDirection), sortBy)
        )
        
        val orders = if (status != null) {
            orderService.getOrdersByStatus(status, pageable)
        } else {
            orderService.getAllOrders(pageable)
        }
        
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(orders)))
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Get a specific order by its ID")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order found"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    )
    fun getOrderById(@PathVariable id: Long): ResponseEntity<ApiResponse<OrderResponse>> {
        val order = orderService.getOrderById(id)
        return ResponseEntity.ok(ApiResponse.success(order))
    }
    
    @PostMapping
    @Operation(summary = "Create new order", description = "Create a new order")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Order created successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid order data or insufficient stock")
    )
    fun createOrder(@Valid @RequestBody request: CreateOrderRequest): ResponseEntity<ApiResponse<OrderResponse>> {
        val order = orderService.createOrder(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(order, "Order created successfully"))
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Update the status of an order")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order status updated successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid status transition"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    )
    fun updateOrderStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateOrderStatusRequest
    ): ResponseEntity<ApiResponse<OrderResponse>> {
        val order = orderService.updateOrderStatus(id, request)
        return ResponseEntity.ok(ApiResponse.success(order, "Order status updated successfully"))
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel order", description = "Cancel an order (only pending or processing orders)")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    )
    fun cancelOrder(@PathVariable id: Long): ResponseEntity<ApiResponse<OrderResponse>> {
        val order = orderService.cancelOrder(id)
        return ResponseEntity.ok(ApiResponse.success(order, "Order cancelled successfully"))
    }
}