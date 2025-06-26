package kg.erkan.utils

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

/**
 * Extension function to get the current authenticated user ID from JWT token
 */
fun ApplicationCall.getCurrentUserId(): String? {
    val principal = principal<JWTPrincipal>()
    return principal?.payload?.getClaim("user_id")?.asString()
}

/**
 * Extension function to get the current authenticated user email from JWT token
 */
fun ApplicationCall.getCurrentUserEmail(): String? {
    val principal = principal<JWTPrincipal>()
    return principal?.payload?.getClaim("email")?.asString()
}

/**
 * Extension function to get the current authenticated user name from JWT token
 */
fun ApplicationCall.getCurrentUserName(): String? {
    val principal = principal<JWTPrincipal>()
    return principal?.payload?.getClaim("name")?.asString()
} 