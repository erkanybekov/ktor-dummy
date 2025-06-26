package kg.erkan.domain.usecases

import kg.erkan.domain.entities.Todo
import kg.erkan.domain.entities.TodoId
import kg.erkan.domain.entities.UserId
import kg.erkan.domain.repositories.TodoRepository
import kg.erkan.domain.repositories.UserRepository

/**
 * Domain Todo Use Cases - Business Rules
 * No external dependencies, pure business logic
 */
class CreateTodoUseCase(
    private val todoRepository: TodoRepository,
    private val userRepository: UserRepository
) {
    
    suspend fun execute(title: String, description: String?, userId: String): Result<Todo> {
        return try {
            val userIdValue = UserId.from(userId)
            
            // Business rule: User must exist
            userRepository.findById(userIdValue)
                ?: return Result.failure(IllegalArgumentException("User not found"))
            
            val todo = Todo.create(title, description, userIdValue)
            val savedTodo = todoRepository.save(todo)
            
            Result.success(savedTodo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetTodoUseCase(private val todoRepository: TodoRepository) {
    
    suspend fun byId(todoId: String, userId: String): Result<Todo> {
        return try {
            val id = TodoId.from(todoId)
            val userIdValue = UserId.from(userId)
            
            val todo = todoRepository.findById(id)
                ?: return Result.failure(NoSuchElementException("Todo not found"))
            
            // Business rule: User can only access their own todos
            if (!todo.belongsTo(userIdValue)) {
                return Result.failure(IllegalAccessException("Access denied"))
            }
            
            Result.success(todo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun allByUser(userId: String): Result<List<Todo>> {
        return try {
            val userIdValue = UserId.from(userId)
            val todos = todoRepository.findAllByUserId(userIdValue)
            
            Result.success(todos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class UpdateTodoUseCase(private val todoRepository: TodoRepository) {
    
    suspend fun updateTitle(todoId: String, newTitle: String, userId: String): Result<Todo> {
        return try {
            val id = TodoId.from(todoId)
            val userIdValue = UserId.from(userId)
            
            val todo = todoRepository.findById(id)
                ?: return Result.failure(NoSuchElementException("Todo not found"))
            
            // Business rule: User can only update their own todos
            if (!todo.belongsTo(userIdValue)) {
                return Result.failure(IllegalAccessException("Access denied"))
            }
            
            val updatedTodo = todo.updateTitle(newTitle)
            val savedTodo = todoRepository.update(updatedTodo)
                ?: return Result.failure(IllegalStateException("Failed to update todo"))
            
            Result.success(savedTodo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateDescription(todoId: String, newDescription: String?, userId: String): Result<Todo> {
        return try {
            val id = TodoId.from(todoId)
            val userIdValue = UserId.from(userId)
            
            val todo = todoRepository.findById(id)
                ?: return Result.failure(NoSuchElementException("Todo not found"))
            
            if (!todo.belongsTo(userIdValue)) {
                return Result.failure(IllegalAccessException("Access denied"))
            }
            
            val updatedTodo = todo.updateDescription(newDescription)
            val savedTodo = todoRepository.update(updatedTodo)
                ?: return Result.failure(IllegalStateException("Failed to update todo"))
            
            Result.success(savedTodo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun complete(todoId: String, userId: String): Result<Todo> {
        return try {
            val id = TodoId.from(todoId)
            val userIdValue = UserId.from(userId)
            
            val todo = todoRepository.findById(id)
                ?: return Result.failure(NoSuchElementException("Todo not found"))
            
            if (!todo.belongsTo(userIdValue)) {
                return Result.failure(IllegalAccessException("Access denied"))
            }
            
            val completedTodo = todo.complete()
            val savedTodo = todoRepository.update(completedTodo)
                ?: return Result.failure(IllegalStateException("Failed to complete todo"))
            
            Result.success(savedTodo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun uncomplete(todoId: String, userId: String): Result<Todo> {
        return try {
            val id = TodoId.from(todoId)
            val userIdValue = UserId.from(userId)
            
            val todo = todoRepository.findById(id)
                ?: return Result.failure(NoSuchElementException("Todo not found"))
            
            if (!todo.belongsTo(userIdValue)) {
                return Result.failure(IllegalAccessException("Access denied"))
            }
            
            val uncompletedTodo = todo.uncomplete()
            val savedTodo = todoRepository.update(uncompletedTodo)
                ?: return Result.failure(IllegalStateException("Failed to uncomplete todo"))
            
            Result.success(savedTodo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteTodoUseCase(private val todoRepository: TodoRepository) {
    
    suspend fun execute(todoId: String, userId: String): Result<Unit> {
        return try {
            val id = TodoId.from(todoId)
            val userIdValue = UserId.from(userId)
            
            // Business rule: Verify ownership before deletion
            if (!todoRepository.existsByIdAndUserId(id, userIdValue)) {
                return Result.failure(IllegalAccessException("Access denied or todo not found"))
            }
            
            val deleted = todoRepository.delete(id)
            if (!deleted) {
                return Result.failure(IllegalStateException("Failed to delete todo"))
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 