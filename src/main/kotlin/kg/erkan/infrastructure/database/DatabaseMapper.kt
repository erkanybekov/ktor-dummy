package kg.erkan.infrastructure.database

import kg.erkan.domain.entities.*
import kg.erkan.database.Users
import kg.erkan.database.Todos
import org.jetbrains.exposed.sql.ResultRow

/**
 * Infrastructure Database Mappers
 * Converts between Domain Entities and Database Models
 */
object DatabaseMapper {
    
    // Domain User -> Database Entity
    fun toUserEntity(user: User, passwordHash: String): UserEntity {
        return UserEntity(
            id = user.id.value,
            email = user.email.value,
            name = user.name,
            passwordHash = passwordHash,
            createdAt = user.createdAt,
            isEmailVerified = user.isEmailVerified
        )
    }
    
    // Database Row -> Domain User (without password)
    fun toDomainUser(row: ResultRow): User {
        return User(
            id = UserId.from(row[Users.id]),
            email = Email.create(row[Users.email]),
            name = row[Users.name],
            isEmailVerified = row[Users.isEmailVerified],
            createdAt = row[Users.createdAt]
        )
    }
    
    // Domain Todo -> Database Row Data
    fun todoToRowData(todo: Todo): TodoRowData {
        return TodoRowData(
            id = todo.id.value,
            title = todo.title,
            description = todo.description,
            isCompleted = todo.isCompleted,
            userId = todo.userId.value,
            createdAt = todo.createdAt,
            updatedAt = todo.updatedAt
        )
    }
    
    // Database Row -> Domain Todo
    fun toDomainTodo(row: ResultRow): Todo {
        return Todo(
            id = TodoId.from(row[Todos.id]),
            title = row[Todos.title],
            description = row[Todos.description],
            isCompleted = row[Todos.isCompleted],
            userId = UserId.from(row[Todos.userId]),
            createdAt = row[Todos.createdAt],
            updatedAt = row[Todos.updatedAt]
        )
    }
}

/**
 * Data classes for database operations
 */
data class UserEntity(
    val id: String,
    val email: String,
    val name: String,
    val passwordHash: String,
    val createdAt: Long,
    val isEmailVerified: Boolean = false
)

data class TodoRowData(
    val id: String,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val userId: String,
    val createdAt: Long,
    val updatedAt: Long
) 