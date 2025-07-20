package com.example.admin.repository

import com.example.admin.entity.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findBySku(sku: String): Optional<Product>
    fun findByCategory(category: String, pageable: Pageable): Page<Product>
    fun findByActiveTrue(pageable: Pageable): Page<Product>
    fun existsBySku(sku: String): Boolean
}