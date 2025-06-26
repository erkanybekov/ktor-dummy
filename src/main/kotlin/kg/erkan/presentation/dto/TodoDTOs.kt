package kg.erkan.presentation.dto

import kotlinx.serialization.Serializable
import kg.erkan.domain.entities.Todo

/**
 * Presentation Layer Todo DTOs
 * Clean API contracts
 */

@Serializable
data class CreateTodoRequest(
    val title: String,
    val description: String? = null
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        
        if (title.isBlank()) errors.add("Title is required")
        if (title.length > 200) errors.add("Title cannot exceed 200 characters")
        
        return errors
    }
}

@Serializable
data class UpdateTodoRequest(
    val title: String? = null,
    val description: String? = null,
    val isCompleted: Boolean? = null
) {
    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        
        if (title != null && title.isBlank()) errors.add("Title cannot be blank")
        if (title != null && title.length > 200) errors.add("Title cannot exceed 200 characters")
        
        return errors
    }
    
    fun hasUpdates(): Boolean = title != null || description != null || isCompleted != null
}

@Serializable
data class TodoResponse(
    val id: String,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val userId: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun from(todo: Todo): TodoResponse {
            return TodoResponse(
                id = todo.id.value,
                title = todo.title,
                description = todo.description,
                isCompleted = todo.isCompleted,
                userId = todo.userId.value,
                createdAt = todo.createdAt,
                updatedAt = todo.updatedAt
            )
        }
        
        fun fromList(todos: List<Todo>): List<TodoResponse> {
            return todos.map { from(it) }
        }
    }
}

@Serializable
data class TodoListResponse(
    val todos: List<TodoResponse>,
    val count: Int
) {
    companion object {
        fun from(todos: List<Todo>): TodoListResponse {
            return TodoListResponse(
                todos = TodoResponse.fromList(todos),
                count = todos.size
            )
        }
    }
} 