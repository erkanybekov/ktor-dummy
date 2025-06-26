package kg.erkan.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import kg.erkan.models.User
import java.util.*

object JWTService {
    // In production, load these from environment variables
    private val secret = System.getenv("JWT_SECRET") ?: "your-super-secret-jwt-key-change-in-production"
    private val algorithm = Algorithm.HMAC256(secret)
    private val issuer = "ktor-dummy-api"
    private val audience = "ktor-dummy-users"
    
    // Token expires in 24 hours
    private const val EXPIRATION_TIME = 24 * 60 * 60 * 1000L // 24 hours in milliseconds
    
    fun generateToken(user: User): String {
        val now = Date()
        val expiration = Date(now.time + EXPIRATION_TIME)
        
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withSubject(user.id)
            .withClaim("user_id", user.id)
            .withClaim("email", user.email)
            .withClaim("name", user.name)
            .withIssuedAt(now)
            .withExpiresAt(expiration)
            .sign(algorithm)
    }
    
    fun verifyToken(token: String): DecodedJWT? {
        return try {
            JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(audience)
                .build()
                .verify(token)
        } catch (e: JWTVerificationException) {
            null
        }
    }
    
    fun extractUserId(token: String): String? {
        return verifyToken(token)?.getClaim("user_id")?.asString()
    }
    
    fun isTokenValid(token: String): Boolean {
        return verifyToken(token) != null
    }
    
    fun getExpirationTime(): Long = EXPIRATION_TIME / 1000 // Return in seconds
} 