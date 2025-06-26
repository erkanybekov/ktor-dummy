package kg.erkan.repository

import kg.erkan.database.Todos
import kg.erkan.models.Todo
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * PostgreSQL-backed Todo Repository
 * Production-ready with proper database operations
 */
object TodoRepository {
    
    fun findAll(): List<Todo> {
        return transaction {
            Todos.selectAll()
                .orderBy(Todos.createdAt to SortOrder.DESC)
                .map { it.toTodo() }
        }
    }
    
    fun findById(id: String): Todo? {
        return transaction {
            Todos.selectAll().where { Todos.id eq id }
                .singleOrNull()
                ?.toTodo()
        }
    }
    
    fun findByUserId(userId: String): List<Todo> {
        return transaction {
            Todos.selectAll().where { Todos.userId eq userId }
                .orderBy(Todos.createdAt to SortOrder.DESC)
                .map { it.toTodo() }
        }
    }
    
    fun create(todo: Todo): Todo {
        val todoId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        
        return transaction {
            Todos.insert {
                it[id] = todoId
                it[title] = todo.title
                it[description] = todo.description
                it[isCompleted] = todo.isCompleted
                it[userId] = todo.userId
                it[createdAt] = now
                it[updatedAt] = now
            }
            
            todo.copy(
                id = todoId,
                createdAt = now,
                updatedAt = now
            )
        }
    }
    
    fun update(id: String, updatedTodo: Todo): Todo? {
        return transaction {
            val updated = Todos.update({ Todos.id eq id }) {
                it[title] = updatedTodo.title
                it[description] = updatedTodo.description
                it[isCompleted] = updatedTodo.isCompleted
                it[updatedAt] = System.currentTimeMillis()
            }
            
            if (updated > 0) findById(id) else null
        }
    }
    
    fun delete(id: String): Boolean {
        return transaction {
            Todos.deleteWhere { Todos.id eq id } > 0
        }
    }
    
    fun deleteByUserId(userId: String): Int {
        return transaction {
            Todos.deleteWhere { Todos.userId eq userId }
        }
    }
    
    fun existsByIdAndUserId(id: String, userId: String): Boolean {
        return transaction {
            Todos.selectAll().where { (Todos.id eq id) and (Todos.userId eq userId) }
                .count() > 0
        }
    }
    
    fun count(): Long {
        return transaction {
            Todos.selectAll().count()
        }
    }
    
    private fun ResultRow.toTodo(): Todo {
        return Todo(
            id = this[Todos.id],
            title = this[Todos.title],
            description = this[Todos.description],
            isCompleted = this[Todos.isCompleted],
            userId = this[Todos.userId],
            createdAt = this[Todos.createdAt],
            updatedAt = this[Todos.updatedAt]
        )
    }
} 