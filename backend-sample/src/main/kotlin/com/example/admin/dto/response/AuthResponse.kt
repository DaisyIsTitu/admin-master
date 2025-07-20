package com.example.admin.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Authentication response")
data class AuthResponse(
    @Schema(description = "Access token")
    val accessToken: String,
    
    @Schema(description = "Refresh token")
    val refreshToken: String,
    
    @Schema(description = "Token type", example = "Bearer")
    val tokenType: String = "Bearer",
    
    @Schema(description = "Token expiration time in seconds", example = "86400")
    val expiresIn: Long,
    
    @Schema(description = "User information")
    val user: UserResponse
)