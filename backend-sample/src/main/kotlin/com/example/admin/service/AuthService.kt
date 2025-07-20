package com.example.admin.service

import com.example.admin.config.JwtUtil
import com.example.admin.dto.request.LoginRequest
import com.example.admin.dto.response.AuthResponse
import com.example.admin.dto.response.UserResponse
import com.example.admin.exception.UnauthorizedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {
    
    fun login(request: LoginRequest): AuthResponse {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.username, request.password)
            )
        } catch (e: BadCredentialsException) {
            throw UnauthorizedException("Invalid username or password")
        }
        
        val user = userService.getUserByUsername(request.username)
        val accessToken = jwtUtil.generateToken(user.username)
        val refreshToken = jwtUtil.generateToken(user.username, true)
        
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = 86400, // 24 hours in seconds
            user = UserResponse.from(user)
        )
    }
    
    fun refreshToken(refreshToken: String): AuthResponse {
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw UnauthorizedException("Invalid refresh token")
        }
        
        val username = jwtUtil.getUsernameFromToken(refreshToken)
        val user = userService.getUserByUsername(username)
        val newAccessToken = jwtUtil.generateToken(username)
        
        return AuthResponse(
            accessToken = newAccessToken,
            refreshToken = refreshToken,
            expiresIn = 86400,
            user = UserResponse.from(user)
        )
    }
}