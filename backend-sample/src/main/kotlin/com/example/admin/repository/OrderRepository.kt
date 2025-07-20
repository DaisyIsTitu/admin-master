package com.example.admin.repository

import com.example.admin.entity.Order
import com.example.admin.entity.OrderStatus
import com.example.admin.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    fun findByOrderNumber(orderNumber: String): Optional<Order>
    fun findByUser(user: User, pageable: Pageable): Page<Order>
    fun findByStatus(status: OrderStatus, pageable: Pageable): Page<Order>
    
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    fun findOrdersBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): List<Order>
}