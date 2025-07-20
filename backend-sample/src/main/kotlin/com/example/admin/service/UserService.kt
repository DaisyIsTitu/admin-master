package com.example.admin.service

import com.example.admin.dto.request.CreateUserRequest
import com.example.admin.dto.request.UpdateUserRequest
import com.example.admin.dto.response.UserResponse
import com.example.admin.entity.User
import com.example.admin.exception.ConflictException
import com.example.admin.exception.ResourceNotFoundException
import com.example.admin.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {
    
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found: $username") }
        
        return org.springframework.security.core.userdetails.User(
            user.username,
            user.password,
            user.active,
            true,
            true,
            true,
            listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
        )
    }
    
    fun createUser(request: CreateUserRequest): UserResponse {
        if (userRepository.existsByUsername(request.username)) {
            throw ConflictException("Username already exists")
        }
        if (userRepository.existsByEmail(request.email)) {
            throw ConflictException("Email already exists")
        }
        
        val user = User(
            username = request.username,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            firstName = request.firstName,
            lastName = request.lastName,
            role = request.role
        )
        
        return UserResponse.from(userRepository.save(user))
    }
    
    fun getUserById(id: Long): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }
        return UserResponse.from(user)
    }
    
    fun getUserByUsername(username: String): User {
        return userRepository.findByUsername(username)
            .orElseThrow { ResourceNotFoundException("User not found: $username") }
    }
    
    fun getUserByUserId(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }
    }
    
    fun getAllUsers(pageable: Pageable): Page<UserResponse> {
        return userRepository.findAll(pageable).map { UserResponse.from(it) }
    }
    
    fun updateUser(id: Long, request: UpdateUserRequest): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User not found with id: $id") }
        
        request.email?.let { email ->
            if (email != user.email && userRepository.existsByEmail(email)) {
                throw ConflictException("Email already exists")
            }
            user.email = email
        }
        
        request.firstName?.let { user.firstName = it }
        request.lastName?.let { user.lastName = it }
        request.role?.let { user.role = it }
        request.active?.let { user.active = it }
        
        user.updatedAt = LocalDateTime.now()
        
        return UserResponse.from(userRepository.save(user))
    }
    
    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException("User not found with id: $id")
        }
        userRepository.deleteById(id)
    }
}