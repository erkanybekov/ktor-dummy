package kg.erkan.infrastructure.repositories

import kg.erkan.domain.entities.Todo
import kg.erkan.domain.entities.TodoId
import kg.erkan.domain.entities.UserId
import kg.erkan.domain.repositories.TodoRepository
import kg.erkan.infrastructure.database.DatabaseMapper
import kg.erkan.database.Todos
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Infrastructure PostgreSQL Todo Repository Implementation
 * Implements domain TodoRepository interface
 */
class PostgreSQLTodoRepository : TodoRepository {
    
    override suspend fun save(todo: Todo): Todo {
        return transaction {
            val rowData = DatabaseMapper.todoToRowData(todo)
            
            Todos.insert {
                it[Todos.id] = rowData.id
                it[Todos.title] = rowData.title
                it[Todos.description] = rowData.description
                it[Todos.isCompleted] = rowData.isCompleted
                it[Todos.userId] = rowData.userId
                it[Todos.createdAt] = rowData.createdAt
                it[Todos.updatedAt] = rowData.updatedAt
            }
            
            todo
        }
    }
    
    override suspend fun findById(id: TodoId): Todo? {
        return transaction {
            Todos.selectAll().where { Todos.id eq id.value }
                .singleOrNull()
                ?.let { DatabaseMapper.toDomainTodo(it) }
        }
    }
    
    override suspend fun findAllByUserId(userId: UserId): List<Todo> {
        return transaction {
            Todos.selectAll().where { Todos.userId eq userId.value }
                .orderBy(Todos.createdAt to SortOrder.DESC)
                .map { DatabaseMapper.toDomainTodo(it) }
        }
    }
    
    override suspend fun findAll(): List<Todo> {
        return transaction {
            Todos.selectAll()
                .orderBy(Todos.createdAt to SortOrder.DESC)
                .map { DatabaseMapper.toDomainTodo(it) }
        }
    }
    
    override suspend fun update(todo: Todo): Todo? {
        return transaction {
            val updated = Todos.update({ Todos.id eq todo.id.value }) {
                it[Todos.title] = todo.title
                it[Todos.description] = todo.description
                it[Todos.isCompleted] = todo.isCompleted
                it[Todos.updatedAt] = todo.updatedAt
            }
            
            if (updated > 0) todo else null
        }
    }
    
    override suspend fun delete(id: TodoId): Boolean {
        return transaction {
            Todos.deleteWhere { Todos.id eq id.value } > 0
        }
    }
    
    override suspend fun deleteAllByUserId(userId: UserId): Int {
        return transaction {
            Todos.deleteWhere { Todos.userId eq userId.value }
        }
    }
    
    override suspend fun existsByIdAndUserId(id: TodoId, userId: UserId): Boolean {
        return transaction {
            Todos.selectAll().where { (Todos.id eq id.value) and (Todos.userId eq userId.value) }
                .count() > 0
        }
    }
    
    override suspend fun count(): Long {
        return transaction {
            Todos.selectAll().count()
        }
    }
} 