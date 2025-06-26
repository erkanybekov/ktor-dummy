package kg.erkan.presentation.controllers

import kg.erkan.domain.usecases.*
import kg.erkan.presentation.dto.*
import kg.erkan.infrastructure.repositories.PostgreSQLUserRepository
import kg.erkan.services.PasswordService
import kg.erkan.services.JWTService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

/**
 * Clean User Controller
 * Uses domain use cases, no business logic here
 */
class UserController(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val userRepository: PostgreSQLUserRepository,
    private val passwordService: PasswordService = PasswordService,
    private val jwtService: JWTService = JWTService
) {
    
    suspend fun register(call: ApplicationCall) {
        try {
            val request = call.receive<CreateUserRequest>()
            
            // Input validation
            val validationErrors = request.validate()
            if (validationErrors.isNotEmpty()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse.validationError<AuthResponse>(validationErrors)
                )
                return
            }
            
            // Create user through use case
            val result = createUserUseCase.execute(request.email, request.name)
            
            result.fold(
                onSuccess = { user ->
                    // Save with password hash (infrastructure concern)
                    val passwordHash = passwordService.hashPassword(request.password)
                    val savedUser = userRepository.saveWithPassword(user, passwordHash)
                    
                    // Generate JWT token
                    val userResponse = UserResponse.from(savedUser)
                    val token = jwtService.generateToken(
                        // Convert domain User to the User model that JWT expects
                        kg.erkan.models.User(
                            id = savedUser.id.value,
                            email = savedUser.email.value,
                            name = savedUser.name,
                            createdAt = savedUser.createdAt,
                            isEmailVerified = savedUser.isEmailVerified
                        )
                    )
                    
                    val authResponse = AuthResponse(
                        user = userResponse,
                        token = token,
                        expiresIn = jwtService.getExpirationTime()
                    )
                    
                    call.respond(
                        HttpStatusCode.Created,
                        ApiResponse.success(authResponse, "User created successfully")
                    )
                },
                onFailure = { exception ->
                    when (exception) {
                        is IllegalArgumentException -> {
                            call.respond(
                                HttpStatusCode.Conflict,
                                ApiResponse.error<AuthResponse>(exception.message ?: "Email already exists")
                            )
                        }
                        else -> {
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                ApiResponse.error<AuthResponse>("Registration failed")
                            )
                        }
                    }
                }
            )
            
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse.error<AuthResponse>("Invalid request format")
            )
        }
    }
    
    suspend fun login(call: ApplicationCall) {
        try {
            val request = call.receive<LoginRequest>()
            
            // Input validation
            val validationErrors = request.validate()
            if (validationErrors.isNotEmpty()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse.validationError<AuthResponse>(validationErrors)
                )
                return
            }
            
            // Find user through use case
            val userResult = getUserUseCase.byEmail(request.email)
            
            userResult.fold(
                onSuccess = { user ->
                    // Verify password (infrastructure concern)
                    val userEntity = userRepository.findByEmailWithPassword(user.email)
                    
                    if (userEntity == null || !passwordService.verifyPassword(request.password, userEntity.passwordHash)) {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ApiResponse.error<AuthResponse>("Invalid credentials")
                        )
                        return
                    }
                    
                    // Generate JWT token
                    val userResponse = UserResponse.from(user)
                    val token = jwtService.generateToken(
                        kg.erkan.models.User(
                            id = user.id.value,
                            email = user.email.value,
                            name = user.name,
                            createdAt = user.createdAt,
                            isEmailVerified = user.isEmailVerified
                        )
                    )
                    
                    val authResponse = AuthResponse(
                        user = userResponse,
                        token = token,
                        expiresIn = jwtService.getExpirationTime()
                    )
                    
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse.success(authResponse, "Login successful")
                    )
                },
                onFailure = {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse.error<AuthResponse>("Invalid credentials")
                    )
                }
            )
            
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse.error<AuthResponse>("Invalid request format")
            )
        }
    }
    
    suspend fun getProfile(call: ApplicationCall, userId: String) {
        val result = getUserUseCase.byId(userId)
        
        result.fold(
            onSuccess = { user ->
                val userResponse = UserResponse.from(user)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse.success(userResponse)
                )
            },
            onFailure = {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse.error<UserResponse>("User not found")
                )
            }
        )
    }
    
    suspend fun updateProfile(call: ApplicationCall, userId: String) {
        try {
            val request = call.receive<UpdateUserRequest>()
            
            // Input validation
            val validationErrors = request.validate()
            if (validationErrors.isNotEmpty()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse.validationError<UserResponse>(validationErrors)
                )
                return
            }
            
            val result = updateUserUseCase.updateName(userId, request.name)
            
            result.fold(
                onSuccess = { user ->
                    val userResponse = UserResponse.from(user)
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse.success(userResponse, "Profile updated successfully")
                    )
                },
                onFailure = { exception ->
                    when (exception) {
                        is NoSuchElementException -> {
                            call.respond(
                                HttpStatusCode.NotFound,
                                ApiResponse.error<UserResponse>("User not found")
                            )
                        }
                        else -> {
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                ApiResponse.error<UserResponse>("Update failed")
                            )
                        }
                    }
                }
            )
            
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse.error<UserResponse>("Invalid request format")
            )
        }
    }
} 