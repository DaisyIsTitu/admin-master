package com.example.admin.dto.request

import com.example.admin.entity.UserRole
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Create user request")
data class CreateUserRequest(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "Username", example = "johndoe")
    val username: String,
    
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "john.doe@example.com")
    val email: String,
    
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(description = "Password", example = "password123")
    val password: String,
    
    @field:NotBlank(message = "First name is required")
    @Schema(description = "First name", example = "John")
    val firstName: String,
    
    @field:NotBlank(message = "Last name is required")
    @Schema(description = "Last name", example = "Doe")
    val lastName: String,
    
    @Schema(description = "User role", example = "USER")
    val role: UserRole = UserRole.USER
)

@Schema(description = "Update user request")
data class UpdateUserRequest(
    @field:Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "john.doe@example.com")
    val email: String?,
    
    @Schema(description = "First name", example = "John")
    val firstName: String?,
    
    @Schema(description = "Last name", example = "Doe")
    val lastName: String?,
    
    @Schema(description = "User role", example = "USER")
    val role: UserRole?,
    
    @Schema(description = "Account active status", example = "true")
    val active: Boolean?
)