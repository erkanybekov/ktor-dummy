package kg.erkan.services

import java.util.regex.Pattern

object ValidationService {
    
    private val emailPattern = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )
    
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String>
    )
    
    fun validateEmail(email: String): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (email.isBlank()) {
            errors.add("Email is required")
        } else if (!emailPattern.matcher(email).matches()) {
            errors.add("Invalid email format")
        } else if (email.length > 254) {
            errors.add("Email is too long")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
    
    fun validatePassword(password: String): ValidationResult {
        val errors = mutableListOf<String>()
        
        when {
            password.length < 8 -> errors.add("Password must be at least 8 characters long")
            password.length > 128 -> errors.add("Password is too long")
            !password.any { it.isUpperCase() } -> errors.add("Password must contain at least one uppercase letter")
            !password.any { it.isLowerCase() } -> errors.add("Password must contain at least one lowercase letter")
            !password.any { it.isDigit() } -> errors.add("Password must contain at least one number")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
    
    fun validateName(name: String): ValidationResult {
        val errors = mutableListOf<String>()
        
        when {
            name.isBlank() -> errors.add("Name is required")
            name.length < 2 -> errors.add("Name must be at least 2 characters long")
            name.length > 50 -> errors.add("Name is too long")
            !name.all { it.isLetter() || it.isWhitespace() || it == '-' || it == '\'' } -> 
                errors.add("Name contains invalid characters")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
    
    fun validateTodoTitle(title: String): ValidationResult {
        val errors = mutableListOf<String>()
        
        when {
            title.isBlank() -> errors.add("Title is required")
            title.length > 100 -> errors.add("Title is too long")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
    
    fun validateTodoDescription(description: String?): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (description != null && description.length > 500) {
            errors.add("Description is too long")
        }
        
        return ValidationResult(errors.isEmpty(), errors)
    }
} 