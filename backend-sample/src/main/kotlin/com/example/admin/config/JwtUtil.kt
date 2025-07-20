package com.example.admin.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil(
    private val jwtProperties: JwtProperties
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
    
    fun generateToken(username: String, isRefresh: Boolean = false): String {
        val expiration = if (isRefresh) jwtProperties.refreshExpiration else jwtProperties.expiration
        
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .claim("type", if (isRefresh) "refresh" else "access")
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }
    
    fun validateToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            !claims.expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }
    
    fun getUsernameFromToken(token: String): String {
        return getClaims(token).subject
    }
    
    fun isRefreshToken(token: String): Boolean {
        return getClaims(token)["type"] == "refresh"
    }
    
    private fun getClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
}