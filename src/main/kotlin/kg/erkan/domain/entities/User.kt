package kg.erkan.domain.entities

/**
 * Domain User Entity - Core business logic
 * No external dependencies, pure business rules
 */
data class User(
    val id: UserId,
    val email: Email,
    val name: String,
    val isEmailVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun create(email: String, name: String): User {
            require(name.isNotBlank()) { "Name cannot be blank" }
            return User(
                id = UserId.generate(),
                email = Email.create(email),
                name = name.trim()
            )
        }
    }
    
    fun verify(): User = copy(isEmailVerified = true)
    fun updateName(newName: String): User {
        require(newName.isNotBlank()) { "Name cannot be blank" }
        return copy(name = newName.trim())
    }
}

/**
 * Value Objects for type safety
 */
@JvmInline
value class UserId(val value: String) {
    companion object {
        fun generate(): UserId = UserId(java.util.UUID.randomUUID().toString())
        fun from(value: String): UserId {
            require(value.isNotBlank()) { "UserId cannot be blank" }
            return UserId(value)
        }
    }
}

@JvmInline  
value class Email(val value: String) {
    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        
        fun create(email: String): Email {
            val normalized = email.lowercase().trim()
            require(normalized.matches(EMAIL_REGEX)) { "Invalid email format: $email" }
            return Email(normalized)
        }
    }
} 