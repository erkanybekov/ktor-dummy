package kg.erkan.database

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = varchar("id", 36)
    val email = varchar("email", 254).uniqueIndex()
    val name = varchar("name", 100)
    val passwordHash = text("password_hash")
    val isEmailVerified = bool("is_email_verified").default(false)
    val createdAt = long("created_at").clientDefault { System.currentTimeMillis() }
    val updatedAt = long("updated_at").clientDefault { System.currentTimeMillis() }
    
    override val primaryKey = PrimaryKey(id)
}

object Todos : Table("todos") {
    val id = varchar("id", 36)
    val title = varchar("title", 200)
    val description = text("description").nullable()
    val isCompleted = bool("is_completed").default(false)
    val userId = varchar("user_id", 36)
    val createdAt = long("created_at").clientDefault { System.currentTimeMillis() }
    val updatedAt = long("updated_at").clientDefault { System.currentTimeMillis() }
    
    override val primaryKey = PrimaryKey(id)
    
    init {
        index(false, userId) // Index for faster user queries
        index(false, userId, isCompleted) // Composite index for filtering
    }
} 