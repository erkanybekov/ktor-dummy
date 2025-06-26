package kg.erkan

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.csrf.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kg.erkan.models.toSuccessResponse
import kg.erkan.repository.TodoRepository
import kg.erkan.repository.UserRepository
import kg.erkan.routes.authRoutes
import kg.erkan.routes.todoRoutes

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World! 🚀 Your Ktor JWT API is running!")
        }
        
        get("/health") {
            val databaseHealthy = try {
                kg.erkan.database.DatabaseService.healthCheck()
            } catch (e: Exception) {
                false
            }
            
            val healthData = mapOf(
                "status" to if (databaseHealthy) "healthy" else "unhealthy",
                "message" to "Todo API with PostgreSQL & JWT Authentication",
                "timestamp" to System.currentTimeMillis(),
                "version" to "1.0.0",
                "database" to mapOf(
                    "connected" to databaseHealthy,
                    "type" to "PostgreSQL",
                    "url" to "localhost:5432/ktor_todo"
                ),
                "stats" to if (databaseHealthy) mapOf(
                    "users" to UserRepository.count(),
                    "todos" to TodoRepository.count()
                ) else mapOf(
                    "users" to "unavailable",
                    "todos" to "unavailable"
                )
            )
            
            val statusCode = if (databaseHealthy) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable
            call.respond(statusCode, healthData.toSuccessResponse())
        }
        
        // Authentication routes (register, login, verify)
        authRoutes()
        
        // Protected todo routes
        todoRoutes()
    }
}
