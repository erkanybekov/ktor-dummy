package kg.erkan.models

import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    val id: String? = null,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val userId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class CreateTodoRequest(
    val title: String,
    val description: String? = null
)

@Serializable
data class UpdateTodoRequest(
    val title: String? = null,
    val description: String? = null,
    val isCompleted: Boolean? = null
) 