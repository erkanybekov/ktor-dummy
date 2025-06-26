package kg.erkan.domain.repositories

import kg.erkan.domain.entities.User
import kg.erkan.domain.entities.UserId
import kg.erkan.domain.entities.Email

/**
 * Domain User Repository Interface
 * Pure abstraction - no implementation details
 */
interface UserRepository {
    
    suspend fun save(user: User): User
    
    suspend fun findById(id: UserId): User?
    
    suspend fun findByEmail(email: Email): User?
    
    suspend fun existsByEmail(email: Email): Boolean
    
    suspend fun update(user: User): User?
    
    suspend fun delete(id: UserId): Boolean
    
    suspend fun count(): Long
} 