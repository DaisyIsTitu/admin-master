package com.example.admin.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page

@Schema(description = "Generic API response wrapper")
data class ApiResponse<T>(
    @Schema(description = "Response status", example = "success")
    val status: String,
    
    @Schema(description = "Response message", example = "Operation completed successfully")
    val message: String,
    
    @Schema(description = "Response data")
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T, message: String = "Success"): ApiResponse<T> {
            return ApiResponse("success", message, data)
        }
        
        fun <T> error(message: String): ApiResponse<T> {
            return ApiResponse("error", message, null)
        }
    }
}

@Schema(description = "Paginated response wrapper")
data class PageResponse<T>(
    @Schema(description = "List of items")
    val content: List<T>,
    
    @Schema(description = "Current page number", example = "0")
    val page: Int,
    
    @Schema(description = "Page size", example = "20")
    val size: Int,
    
    @Schema(description = "Total number of elements", example = "150")
    val totalElements: Long,
    
    @Schema(description = "Total number of pages", example = "8")
    val totalPages: Int,
    
    @Schema(description = "Is first page", example = "true")
    val first: Boolean,
    
    @Schema(description = "Is last page", example = "false")
    val last: Boolean
) {
    companion object {
        fun <T> from(page: Page<T>): PageResponse<T> {
            return PageResponse(
                content = page.content,
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                first = page.isFirst,
                last = page.isLast
            )
        }
    }
}