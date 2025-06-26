package kg.erkan.infrastructure.repositories

import kg.erkan.domain.entities.User
import kg.erkan.domain.entities.UserId
import kg.erkan.domain.entities.Email
import kg.erkan.domain.repositories.UserRepository
import kg.erkan.infrastructure.database.DatabaseMapper
import kg.erkan.infrastructure.database.UserEntity
import kg.erkan.database.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Infrastructure PostgreSQL User Repository Implementation
 * Implements domain UserRepository interface
 */
class PostgreSQLUserRepository : UserRepository {
    
    override suspend fun save(user: User): User {
        // This will be properly handled by the auth service layer
        return saveWithPassword(user, "temp_hash")
    }
    
    override suspend fun findById(id: UserId): User? {
        return transaction {
            Users.selectAll().where { Users.id eq id.value }
                .singleOrNull()
                ?.let { DatabaseMapper.toDomainUser(it) }
        }
    }
    
    override suspend fun findByEmail(email: Email): User? {
        return transaction {
            Users.selectAll().where { Users.email eq email.value }
                .singleOrNull()
                ?.let { DatabaseMapper.toDomainUser(it) }
        }
    }
    
    override suspend fun existsByEmail(email: Email): Boolean {
        return transaction {
            Users.selectAll().where { Users.email eq email.value }
                .count() > 0
        }
    }
    
    override suspend fun update(user: User): User? {
        return transaction {
            val updated = Users.update({ Users.id eq user.id.value }) {
                it[Users.email] = user.email.value
                it[Users.name] = user.name
                it[Users.isEmailVerified] = user.isEmailVerified
                it[Users.updatedAt] = System.currentTimeMillis()
            }
            
            if (updated > 0) user else null
        }
    }
    
    override suspend fun delete(id: UserId): Boolean {
        return transaction {
            Users.deleteWhere { Users.id eq id.value } > 0
        }
    }
    
    override suspend fun count(): Long {
        return transaction {
            Users.selectAll().count()
        }
    }
    
    // Infrastructure-specific methods for authentication
    suspend fun findByEmailWithPassword(email: Email): UserEntity? {
        return transaction {
            Users.selectAll().where { Users.email eq email.value }
                .singleOrNull()
                ?.let { row ->
                    UserEntity(
                        id = row[Users.id],
                        email = row[Users.email],
                        name = row[Users.name],
                        passwordHash = row[Users.passwordHash],
                        createdAt = row[Users.createdAt],
                        isEmailVerified = row[Users.isEmailVerified]
                    )
                }
        }
    }
    
    suspend fun saveWithPassword(user: User, passwordHash: String): User {
        return transaction {
            Users.insert {
                it[Users.id] = user.id.value
                it[Users.email] = user.email.value
                it[Users.name] = user.name
                it[Users.passwordHash] = passwordHash
                it[Users.isEmailVerified] = user.isEmailVerified
                it[Users.createdAt] = user.createdAt
                it[Users.updatedAt] = user.createdAt
            }
            
            user
        }
    }
} 