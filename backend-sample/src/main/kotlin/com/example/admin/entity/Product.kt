package com.example.admin.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    var name: String,
    
    @Column(columnDefinition = "TEXT")
    var description: String? = null,
    
    @Column(unique = true, nullable = false)
    var sku: String,
    
    @Column(nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,
    
    @Column(nullable = false)
    var stock: Int = 0,
    
    @Column(nullable = false)
    var category: String,
    
    @Column(nullable = false)
    var active: Boolean = true,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)