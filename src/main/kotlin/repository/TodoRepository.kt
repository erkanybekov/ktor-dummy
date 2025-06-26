package kg.erkan.repository

import kg.erkan.models.Todo
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object TodoRepository {
    private val todos = ConcurrentHashMap<String, Todo>()
    
    fun findAll(): List<Todo> = todos.values.toList()
    
    fun findById(id: String): Todo? = todos[id]
    
    fun findByUserId(userId: String): List<Todo> = 
        todos.values.filter { it.userId == userId }.sortedByDescending { it.createdAt }
    
    fun create(todo: Todo): Todo {
        val todoId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val newTodo = todo.copy(id = todoId, createdAt = now, updatedAt = now)
        todos[todoId] = newTodo
        return newTodo
    }
    
    fun update(id: String, updatedTodo: Todo): Todo? {
        return if (todos.containsKey(id)) {
            val updated = updatedTodo.copy(
                id = id,
                updatedAt = System.currentTimeMillis()
            )
            todos[id] = updated
            updated
        } else {
            null
        }
    }
    
    fun delete(id: String): Boolean {
        return todos.remove(id) != null
    }
    
    fun deleteByUserId(userId: String): Int {
        val userTodos = findByUserId(userId)
        userTodos.forEach { todos.remove(it.id) }
        return userTodos.size
    }
    
    fun existsByIdAndUserId(id: String, userId: String): Boolean {
        val todo = todos[id]
        return todo != null && todo.userId == userId
    }
    
    fun count(): Long = todos.size.toLong()
} 