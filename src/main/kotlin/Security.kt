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

fun Application.configureSecurity() {
    // CSRF protection - simplified for development
    install(CSRF) {
        // Allow requests from our frontend
        allowOrigin("http://localhost:8080")
        allowOrigin("http://localhost:3000") // React dev server
        
        // tests Origin matches Host header  
        originMatchesHost()
    }
    
    // JWT Authentication Configuration
    val jwtAudience = "ktor-dummy-users"
    val jwtIssuer = "ktor-dummy-api"
    val jwtSecret = System.getenv("JWT_SECRET") ?: "your-super-secret-jwt-key-change-in-production"
    
    authentication {
        jwt("auth-jwt") {
            realm = "Todo API"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}
