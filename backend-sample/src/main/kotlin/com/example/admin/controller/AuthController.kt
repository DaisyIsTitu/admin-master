package com.example.admin.controller

import com.example.admin.dto.request.LoginRequest
import com.example.admin.dto.request.RefreshTokenRequest
import com.example.admin.dto.response.ApiResponse
import com.example.admin.dto.response.AuthResponse
import com.example.admin.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
class AuthController(
    private val authService: AuthService
) {
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and get access token")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
    )
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val response = authService.login(request)
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"))
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid refresh token")
    )
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        val response = authService.refreshToken(request.refreshToken)
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"))
    }
    
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user (client should remove tokens)")
    fun logout(): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.ok(ApiResponse.success(null, "Logout successful"))
    }
}