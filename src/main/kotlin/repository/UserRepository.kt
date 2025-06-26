package kg.erkan.repository

import kg.erkan.models.UserEntity
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object UserRepository {
    private val users = ConcurrentHashMap<String, UserEntity>()
    private val emailIndex = ConcurrentHashMap<String, String>() // email -> userId
    
    fun create(user: UserEntity): UserEntity {
        val userId = UUID.randomUUID().toString()
        val userWithId = user.copy(id = userId, createdAt = System.currentTimeMillis())
        
        users[userId] = userWithId
        emailIndex[user.email.lowercase()] = userId
        
        return userWithId
    }
    
    fun findById(id: String): UserEntity? = users[id]
    
    fun findByEmail(email: String): UserEntity? {
        val userId = emailIndex[email.lowercase()]
        return userId?.let { users[it] }
    }
    
    fun existsByEmail(email: String): Boolean = emailIndex.containsKey(email.lowercase())
    
    fun update(user: UserEntity): UserEntity? {
        return if (users.containsKey(user.id)) {
            users[user.id] = user
            // Update email index if email changed
            emailIndex[user.email.lowercase()] = user.id
            user
        } else {
            null
        }
    }
    
    fun delete(id: String): Boolean {
        val user = users[id]
        return if (user != null) {
            users.remove(id)
            emailIndex.remove(user.email.lowercase())
            true
        } else {
            false
        }
    }
    
    fun count(): Long = users.size.toLong()
} 