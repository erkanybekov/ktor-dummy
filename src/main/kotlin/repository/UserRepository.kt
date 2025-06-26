package kg.erkan.repository

import kg.erkan.database.Users
import kg.erkan.models.UserEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * PostgreSQL-backed User Repository
 * Production-ready with proper database operations
 */
object UserRepository {
    
    fun create(user: UserEntity): UserEntity {
        val userId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        
        return transaction {
            Users.insert {
                it[id] = userId
                it[email] = user.email.lowercase()
                it[name] = user.name
                it[passwordHash] = user.passwordHash
                it[isEmailVerified] = user.isEmailVerified
                it[createdAt] = now
                it[updatedAt] = now
            }
            
            user.copy(id = userId, createdAt = now)
        }
    }
    
    fun findById(id: String): UserEntity? {
        return transaction {
            Users.selectAll().where { Users.id eq id }
                .singleOrNull()
                ?.toUserEntity()
        }
    }
    
    fun findByEmail(email: String): UserEntity? {
        return transaction {
            Users.selectAll().where { Users.email eq email.lowercase() }
                .singleOrNull()
                ?.toUserEntity()
        }
    }
    
    fun existsByEmail(email: String): Boolean {
        return transaction {
            Users.selectAll().where { Users.email eq email.lowercase() }
                .count() > 0
        }
    }
    
    fun update(user: UserEntity): UserEntity? {
        return transaction {
            val updated = Users.update({ Users.id eq user.id }) {
                it[email] = user.email.lowercase()
                it[name] = user.name
                it[passwordHash] = user.passwordHash
                it[isEmailVerified] = user.isEmailVerified
                it[updatedAt] = System.currentTimeMillis()
            }
            
            if (updated > 0) findById(user.id) else null
        }
    }
    
    fun delete(id: String): Boolean {
        return transaction {
            Users.deleteWhere { Users.id eq id } > 0
        }
    }
    
    fun count(): Long {
        return transaction {
            Users.selectAll().count()
        }
    }
    
    private fun ResultRow.toUserEntity(): UserEntity {
        return UserEntity(
            id = this[Users.id],
            email = this[Users.email],
            name = this[Users.name],
            passwordHash = this[Users.passwordHash],
            isEmailVerified = this[Users.isEmailVerified],
            createdAt = this[Users.createdAt]
        )
    }
} 