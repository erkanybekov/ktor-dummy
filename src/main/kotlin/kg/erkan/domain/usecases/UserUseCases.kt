package kg.erkan.domain.usecases

import kg.erkan.domain.entities.User
import kg.erkan.domain.entities.UserId
import kg.erkan.domain.entities.Email
import kg.erkan.domain.repositories.UserRepository

/**
 * Domain User Use Cases - Business Rules
 * No external dependencies, pure business logic
 */
class CreateUserUseCase(private val userRepository: UserRepository) {
    
    suspend fun execute(email: String, name: String): Result<User> {
        return try {
            val emailValue = Email.create(email)
            
            // Business rule: Email must be unique
            if (userRepository.existsByEmail(emailValue)) {
                return Result.failure(IllegalArgumentException("Email already exists"))
            }
            
            val user = User.create(email, name)
            val savedUser = userRepository.save(user)
            
            Result.success(savedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetUserUseCase(private val userRepository: UserRepository) {
    
    suspend fun byId(userId: String): Result<User> {
        return try {
            val id = UserId.from(userId)
            val user = userRepository.findById(id)
                ?: return Result.failure(NoSuchElementException("User not found"))
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun byEmail(email: String): Result<User> {
        return try {
            val emailValue = Email.create(email)
            val user = userRepository.findByEmail(emailValue)
                ?: return Result.failure(NoSuchElementException("User not found"))
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class UpdateUserUseCase(private val userRepository: UserRepository) {
    
    suspend fun updateName(userId: String, newName: String): Result<User> {
        return try {
            val id = UserId.from(userId)
            val user = userRepository.findById(id)
                ?: return Result.failure(NoSuchElementException("User not found"))
            
            val updatedUser = user.updateName(newName)
            val savedUser = userRepository.update(updatedUser)
                ?: return Result.failure(IllegalStateException("Failed to update user"))
            
            Result.success(savedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun verifyEmail(userId: String): Result<User> {
        return try {
            val id = UserId.from(userId)
            val user = userRepository.findById(id)
                ?: return Result.failure(NoSuchElementException("User not found"))
            
            val verifiedUser = user.verify()
            val savedUser = userRepository.update(verifiedUser)
                ?: return Result.failure(IllegalStateException("Failed to verify user"))
            
            Result.success(savedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteUserUseCase(
    private val userRepository: UserRepository,
    private val todoRepository: kg.erkan.domain.repositories.TodoRepository
) {
    
    suspend fun execute(userId: String): Result<Unit> {
        return try {
            val id = UserId.from(userId)
            
            // Business rule: Delete user's todos first
            todoRepository.deleteAllByUserId(id)
            
            val deleted = userRepository.delete(id)
            if (!deleted) {
                return Result.failure(NoSuchElementException("User not found"))
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 