package kg.erkan.domain.repositories

import kg.erkan.domain.entities.Todo
import kg.erkan.domain.entities.TodoId
import kg.erkan.domain.entities.UserId

/**
 * Domain Todo Repository Interface  
 * Pure abstraction - no implementation details
 */
interface TodoRepository {
    
    suspend fun save(todo: Todo): Todo
    
    suspend fun findById(id: TodoId): Todo?
    
    suspend fun findAllByUserId(userId: UserId): List<Todo>
    
    suspend fun findAll(): List<Todo>
    
    suspend fun update(todo: Todo): Todo?
    
    suspend fun delete(id: TodoId): Boolean
    
    suspend fun deleteAllByUserId(userId: UserId): Int
    
    suspend fun existsByIdAndUserId(id: TodoId, userId: UserId): Boolean
    
    suspend fun count(): Long
} 