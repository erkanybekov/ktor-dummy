package kg.erkan.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isEmailVerified: Boolean = false
)

// Internal model (includes password hash) - never serialize to JSON
data class UserEntity(
    val id: String,
    val email: String,
    val name: String,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isEmailVerified: Boolean = false
) {
    fun toUser() = User(
        id = id,
        email = email,
        name = name,
        createdAt = createdAt,
        isEmailVerified = isEmailVerified
    )
}

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val user: User,
    val token: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long // seconds
) 