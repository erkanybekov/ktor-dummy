package kg.erkan.presentation.dto

import kotlinx.serialization.Serializable
import kg.erkan.domain.entities.User

/**
 * Presentation Layer DTOs
 * Clean separation between API contracts and domain models
 */

@Serializable
data class CreateUserRequest(
    val email: String,
    val password: String,
    val name: String
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        
        if (email.isBlank()) errors.add("Email is required")
        if (password.length < 8) errors.add("Password must be at least 8 characters")
        if (name.isBlank()) errors.add("Name is required")
        
        return errors
    }
}

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        
        if (email.isBlank()) errors.add("Email is required")
        if (password.isBlank()) errors.add("Password is required")
        
        return errors
    }
}

@Serializable
data class UpdateUserRequest(
    val name: String
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        
        if (name.isBlank()) errors.add("Name is required")
        
        return errors
    }
}

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val name: String,
    val isEmailVerified: Boolean,
    val createdAt: Long
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                id = user.id.value,
                email = user.email.value,
                name = user.name,
                isEmailVerified = user.isEmailVerified,
                createdAt = user.createdAt
            )
        }
    }
}

@Serializable
data class AuthResponse(
    val user: UserResponse,
    val token: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long
) 