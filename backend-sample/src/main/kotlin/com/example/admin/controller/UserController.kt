package com.example.admin.controller

import com.example.admin.dto.request.CreateUserRequest
import com.example.admin.dto.request.UpdateUserRequest
import com.example.admin.dto.response.ApiResponse
import com.example.admin.dto.response.PageResponse
import com.example.admin.dto.response.UserResponse
import com.example.admin.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User CRUD operations")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
class UserController(
    private val userService: UserService
) {
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Get paginated list of users")
    fun getAllUsers(
        @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "Page size") @RequestParam(defaultValue = "20") size: Int,
        @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") sortBy: String,
        @Parameter(description = "Sort direction") @RequestParam(defaultValue = "ASC") sortDirection: String
    ): ResponseEntity<ApiResponse<PageResponse<UserResponse>>> {
        val pageable = PageRequest.of(
            page, 
            size, 
            Sort.by(Sort.Direction.valueOf(sortDirection), sortBy)
        )
        val users = userService.getAllUsers(pageable)
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(users)))
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Get a specific user by their ID")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    )
    fun getUserById(@PathVariable id: Long): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(ApiResponse.success(user))
    }
    
    @PostMapping
    @Operation(summary = "Create new user", description = "Create a new user account")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Username or email already exists")
    )
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.createUser(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(user, "User created successfully"))
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    )
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.updateUser(id, request)
        return ResponseEntity.ok(ApiResponse.success(user, "User updated successfully"))
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user account")
    @ApiResponses(
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully"),
        io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    )
    fun deleteUser(@PathVariable id: Long): ResponseEntity<ApiResponse<Nothing>> {
        userService.deleteUser(id)
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"))
    }
}