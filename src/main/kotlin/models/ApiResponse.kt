package kg.erkan.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val errors: List<String>? = null
)

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String,
    val details: Map<String, String>? = null
)

// Extension functions for easy response creation
fun <T> T.toSuccessResponse(message: String? = null) = ApiResponse(
    success = true,
    data = this,
    message = message
)

fun String.toErrorResponse(details: Map<String, String>? = null) = ErrorResponse(
    error = "BAD_REQUEST",
    message = this,
    details = details
)

fun List<String>.toValidationErrorResponse() = ApiResponse<Nothing>(
    success = false,
    errors = this,
    message = "Validation failed"
) 