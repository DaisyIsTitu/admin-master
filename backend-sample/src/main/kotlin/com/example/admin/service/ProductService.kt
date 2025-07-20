package com.example.admin.service

import com.example.admin.dto.request.CreateProductRequest
import com.example.admin.dto.request.UpdateProductRequest
import com.example.admin.dto.response.ProductResponse
import com.example.admin.entity.Product
import com.example.admin.exception.ConflictException
import com.example.admin.exception.ResourceNotFoundException
import com.example.admin.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository
) {
    
    fun createProduct(request: CreateProductRequest): ProductResponse {
        if (productRepository.existsBySku(request.sku)) {
            throw ConflictException("Product with SKU ${request.sku} already exists")
        }
        
        val product = Product(
            name = request.name,
            description = request.description,
            sku = request.sku,
            price = request.price,
            stock = request.stock,
            category = request.category
        )
        
        return ProductResponse.from(productRepository.save(product))
    }
    
    fun getProductById(id: Long): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found with id: $id") }
        return ProductResponse.from(product)
    }
    
    fun getAllProducts(pageable: Pageable): Page<ProductResponse> {
        return productRepository.findAll(pageable).map { ProductResponse.from(it) }
    }
    
    fun getActiveProducts(pageable: Pageable): Page<ProductResponse> {
        return productRepository.findByActiveTrue(pageable).map { ProductResponse.from(it) }
    }
    
    fun getProductsByCategory(category: String, pageable: Pageable): Page<ProductResponse> {
        return productRepository.findByCategory(category, pageable).map { ProductResponse.from(it) }
    }
    
    fun updateProduct(id: Long, request: UpdateProductRequest): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found with id: $id") }
        
        request.name?.let { product.name = it }
        request.description?.let { product.description = it }
        request.price?.let { product.price = it }
        request.stock?.let { product.stock = it }
        request.category?.let { product.category = it }
        request.active?.let { product.active = it }
        
        product.updatedAt = LocalDateTime.now()
        
        return ProductResponse.from(productRepository.save(product))
    }
    
    fun deleteProduct(id: Long) {
        if (!productRepository.existsById(id)) {
            throw ResourceNotFoundException("Product not found with id: $id")
        }
        productRepository.deleteById(id)
    }
    
    fun findProductById(id: Long): Product {
        return productRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product not found with id: $id") }
    }
}