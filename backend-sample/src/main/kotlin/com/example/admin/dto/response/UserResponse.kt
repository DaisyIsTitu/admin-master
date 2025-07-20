package com.example.admin.dto.response

import com.example.admin.entity.User
import com.example.admin.entity.UserRole
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "User response")
data class UserResponse(
    @Schema(description = "User ID", example = "1")
    val id: Long,
    
    @Schema(description = "Username", example = "johndoe")
    val username: String,
    
    @Schema(description = "Email address", example = "john.doe@example.com")
    val email: String,
    
    @Schema(description = "First name", example = "John")
    val firstName: String,
    
    @Schema(description = "Last name", example = "Doe")
    val lastName: String,
    
    @Schema(description = "User role", example = "USER")
    val role: UserRole,
    
    @Schema(description = "Account active status", example = "true")
    val active: Boolean,
    
    @Schema(description = "Account creation date")
    val createdAt: LocalDateTime,
    
    @Schema(description = "Last update date")
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                id = user.id!!,
                username = user.username,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                role = user.role,
                active = user.active,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
        }
    }
}