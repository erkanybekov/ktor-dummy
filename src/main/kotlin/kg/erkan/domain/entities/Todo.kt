package kg.erkan.domain.entities

/**
 * Domain Todo Entity - Core business logic
 * No external dependencies, pure business rules
 */
data class Todo(
    val id: TodoId,
    val title: String,
    val description: String?,
    val isCompleted: Boolean = false,
    val userId: UserId,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    companion object {
        fun create(title: String, description: String?, userId: UserId): Todo {
            require(title.isNotBlank()) { "Todo title cannot be blank" }
            require(title.length <= 200) { "Todo title cannot exceed 200 characters" }
            
            return Todo(
                id = TodoId.generate(),
                title = title.trim(),
                description = description?.trim()?.takeIf { it.isNotBlank() },
                userId = userId
            )
        }
    }
    
    fun complete(): Todo = copy(
        isCompleted = true,
        updatedAt = System.currentTimeMillis()
    )
    
    fun uncomplete(): Todo = copy(
        isCompleted = false,
        updatedAt = System.currentTimeMillis()
    )
    
    fun updateTitle(newTitle: String): Todo {
        require(newTitle.isNotBlank()) { "Todo title cannot be blank" }
        require(newTitle.length <= 200) { "Todo title cannot exceed 200 characters" }
        
        return copy(
            title = newTitle.trim(),
            updatedAt = System.currentTimeMillis()
        )
    }
    
    fun updateDescription(newDescription: String?): Todo = copy(
        description = newDescription?.trim()?.takeIf { it.isNotBlank() },
        updatedAt = System.currentTimeMillis()
    )
    
    fun belongsTo(userId: UserId): Boolean = this.userId == userId
}

/**
 * Value Object for type safety
 */
@JvmInline
value class TodoId(val value: String) {
    companion object {
        fun generate(): TodoId = TodoId(java.util.UUID.randomUUID().toString())
        fun from(value: String): TodoId {
            require(value.isNotBlank()) { "TodoId cannot be blank" }
            return TodoId(value)
        }
    }
} 