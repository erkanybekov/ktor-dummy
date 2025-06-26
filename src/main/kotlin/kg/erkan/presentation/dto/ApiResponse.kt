package kg.erkan.presentation.dto

import kotlinx.serialization.Serializable

/**
 * Generic API Response Wrapper
 * Provides consistent response format across all endpoints
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val errors: List<String>? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> {
            return ApiResponse(
                success = true,
                data = data,
                message = message
            )
        }
        
        fun <T> error(message: String, errors: List<String>? = null): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = message,
                errors = errors
            )
        }
        
        fun <T> validationError(errors: List<String>): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = "Validation failed",
                errors = errors
            )
        }
    }
} 