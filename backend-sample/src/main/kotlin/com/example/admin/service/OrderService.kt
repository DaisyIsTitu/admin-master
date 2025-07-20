package com.example.admin.service

import com.example.admin.dto.request.CreateOrderRequest
import com.example.admin.dto.request.UpdateOrderStatusRequest
import com.example.admin.dto.response.OrderResponse
import com.example.admin.entity.Order
import com.example.admin.entity.OrderItem
import com.example.admin.entity.OrderStatus
import com.example.admin.exception.BadRequestException
import com.example.admin.exception.ResourceNotFoundException
import com.example.admin.repository.OrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
@Transactional
class OrderService(
    private val orderRepository: OrderRepository,
    private val userService: UserService,
    private val productService: ProductService
) {
    
    fun createOrder(request: CreateOrderRequest): OrderResponse {
        val user = userService.getUserByUserId(request.userId)
        
        val order = Order(
            orderNumber = generateOrderNumber(),
            user = user,
            shippingAddress = request.shippingAddress,
            notes = request.notes,
            totalAmount = BigDecimal.ZERO
        )
        
        var totalAmount = BigDecimal.ZERO
        
        request.items.forEach { itemRequest ->
            val product = productService.findProductById(itemRequest.productId)
            
            if (product.stock < itemRequest.quantity) {
                throw BadRequestException("Insufficient stock for product: ${product.name}")
            }
            
            val orderItem = OrderItem(
                order = order,
                product = product,
                quantity = itemRequest.quantity,
                price = product.price
            )
            
            order.items.add(orderItem)
            totalAmount = totalAmount.add(product.price.multiply(BigDecimal(itemRequest.quantity)))
            
            // Update product stock
            product.stock -= itemRequest.quantity
        }
        
        order.totalAmount = totalAmount
        
        return OrderResponse.from(orderRepository.save(order))
    }
    
    fun getOrderById(id: Long): OrderResponse {
        val order = orderRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Order not found with id: $id") }
        return OrderResponse.from(order)
    }
    
    fun getAllOrders(pageable: Pageable): Page<OrderResponse> {
        return orderRepository.findAll(pageable).map { OrderResponse.from(it) }
    }
    
    fun getOrdersByStatus(status: OrderStatus, pageable: Pageable): Page<OrderResponse> {
        return orderRepository.findByStatus(status, pageable).map { OrderResponse.from(it) }
    }
    
    fun updateOrderStatus(id: Long, request: UpdateOrderStatusRequest): OrderResponse {
        val order = orderRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Order not found with id: $id") }
        
        // Validate status transition
        if (!isValidStatusTransition(order.status, request.status)) {
            throw BadRequestException("Invalid status transition from ${order.status} to ${request.status}")
        }
        
        order.status = request.status
        order.updatedAt = LocalDateTime.now()
        
        return OrderResponse.from(orderRepository.save(order))
    }
    
    fun cancelOrder(id: Long): OrderResponse {
        val order = orderRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Order not found with id: $id") }
        
        if (order.status != OrderStatus.PENDING && order.status != OrderStatus.PROCESSING) {
            throw BadRequestException("Order cannot be cancelled in status: ${order.status}")
        }
        
        // Restore product stock
        order.items.forEach { item ->
            val product = item.product
            product.stock += item.quantity
        }
        
        order.status = OrderStatus.CANCELLED
        order.updatedAt = LocalDateTime.now()
        
        return OrderResponse.from(orderRepository.save(order))
    }
    
    private fun generateOrderNumber(): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val random = (1000..9999).random()
        return "ORD-$timestamp-$random"
    }
    
    private fun isValidStatusTransition(current: OrderStatus, new: OrderStatus): Boolean {
        return when (current) {
            OrderStatus.PENDING -> new in listOf(OrderStatus.PROCESSING, OrderStatus.CANCELLED)
            OrderStatus.PROCESSING -> new in listOf(OrderStatus.SHIPPED, OrderStatus.CANCELLED)
            OrderStatus.SHIPPED -> new == OrderStatus.DELIVERED
            OrderStatus.DELIVERED -> false
            OrderStatus.CANCELLED -> false
        }
    }
}