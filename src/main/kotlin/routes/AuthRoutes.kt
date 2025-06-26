package kg.erkan.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kg.erkan.models.*
import kg.erkan.repository.UserRepository
import kg.erkan.services.JWTService
import kg.erkan.services.PasswordService
import kg.erkan.services.ValidationService

fun Route.authRoutes() {
    route("/api/auth") {
        
        post("/register") {
            try {
                val request = call.receive<RegisterRequest>()
                
                // Validate input
                val emailValidation = ValidationService.validateEmail(request.email)
                val passwordValidation = ValidationService.validatePassword(request.password)
                val nameValidation = ValidationService.validateName(request.name)
                
                val allErrors = mutableListOf<String>()
                allErrors.addAll(emailValidation.errors)
                allErrors.addAll(passwordValidation.errors)
                allErrors.addAll(nameValidation.errors)
                
                if (allErrors.isNotEmpty()) {
                    call.respond(HttpStatusCode.BadRequest, allErrors.toValidationErrorResponse())
                    return@post
                }
                
                // Check if user already exists
                if (UserRepository.existsByEmail(request.email)) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        "Email already registered".toErrorResponse()
                    )
                    return@post
                }
                
                // Create user
                val passwordHash = PasswordService.hashPassword(request.password)
                val userEntity = UserEntity(
                    id = "", // Will be set by repository
                    email = request.email.lowercase().trim(),
                    name = request.name.trim(),
                    passwordHash = passwordHash
                )
                
                val savedUser = UserRepository.create(userEntity)
                val user = savedUser.toUser()
                val token = JWTService.generateToken(user)
                
                val authResponse = AuthResponse(
                    user = user,
                    token = token,
                    expiresIn = JWTService.getExpirationTime()
                )
                
                call.respond(HttpStatusCode.Created, authResponse.toSuccessResponse("User registered successfully"))
                
            } catch (e: Exception) {
                call.application.log.error("Registration error", e)
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid request format".toErrorResponse()
                )
            }
        }
        
        post("/login") {
            try {
                val request = call.receive<LoginRequest>()
                
                // Basic validation
                if (request.email.isBlank() || request.password.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        "Email and password are required".toErrorResponse()
                    )
                    return@post
                }
                
                // Find user
                val userEntity = UserRepository.findByEmail(request.email)
                if (userEntity == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        "Invalid email or password".toErrorResponse()
                    )
                    return@post
                }
                
                // Verify password
                if (!PasswordService.verifyPassword(request.password, userEntity.passwordHash)) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        "Invalid email or password".toErrorResponse()
                    )
                    return@post
                }
                
                // Generate token
                val user = userEntity.toUser()
                val token = JWTService.generateToken(user)
                
                val authResponse = AuthResponse(
                    user = user,
                    token = token,
                    expiresIn = JWTService.getExpirationTime()
                )
                
                call.respond(HttpStatusCode.OK, authResponse.toSuccessResponse("Login successful"))
                
            } catch (e: Exception) {
                call.application.log.error("Login error", e)
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid request format".toErrorResponse()
                )
            }
        }
        
        // Test endpoint to verify token (useful for debugging)
        get("/verify") {
            val authHeader = call.request.headers["Authorization"]
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "Missing or invalid authorization header".toErrorResponse()
                )
                return@get
            }
            
            val token = authHeader.removePrefix("Bearer ").trim()
            val userId = JWTService.extractUserId(token)
            
            if (userId == null) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "Invalid token".toErrorResponse()
                )
                return@get
            }
            
            val user = UserRepository.findById(userId)?.toUser()
            if (user == null) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "User not found".toErrorResponse()
                )
                return@get
            }
            
            call.respond(
                HttpStatusCode.OK,
                mapOf(
                    "valid" to true,
                    "user" to user
                ).toSuccessResponse("Token is valid")
            )
        }
    }
} 