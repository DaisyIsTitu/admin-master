package com.example.admin.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Login request")
data class LoginRequest(
    @field:NotBlank(message = "Username is required")
    @Schema(description = "Username", example = "admin")
    val username: String,
    
    @field:NotBlank(message = "Password is required")
    @Schema(description = "Password", example = "password123")
    val password: String
)

@Schema(description = "Token refresh request")
data class RefreshTokenRequest(
    @field:NotBlank(message = "Refresh token is required")
    @Schema(description = "Refresh token")
    val refreshToken: String
)