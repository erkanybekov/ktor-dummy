package kg.erkan.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kg.erkan.models.*
import kg.erkan.repository.TodoRepository
import kg.erkan.services.ValidationService

fun Route.todoRoutes() {
    route("/api/todos") {
        
        // Protected routes - require JWT authentication
        authenticate("auth-jwt") {
            
            // GET /api/todos - Get current user's todos
            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("user_id")?.asString()
                
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token".toErrorResponse())
                    return@get
                }
                
                val todos = TodoRepository.findByUserId(userId)
                call.respond(HttpStatusCode.OK, todos.toSuccessResponse())
            }
            
            // GET /api/todos/{id} - Get specific todo by ID (user's only)
            get("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("user_id")?.asString()
                val todoId = call.parameters["id"]
                
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token".toErrorResponse())
                    return@get
                }
                
                if (todoId.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid todo ID".toErrorResponse())
                    return@get
                }
                
                val todo = TodoRepository.findById(todoId)
                if (todo == null) {
                    call.respond(HttpStatusCode.NotFound, "Todo not found".toErrorResponse())
                    return@get
                }
                
                // Check if todo belongs to the authenticated user
                if (todo.userId != userId) {
                    call.respond(HttpStatusCode.Forbidden, "Access denied".toErrorResponse())
                    return@get
                }
                
                call.respond(HttpStatusCode.OK, todo.toSuccessResponse())
            }
            
            // POST /api/todos - Create new todo
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("user_id")?.asString()
                
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token".toErrorResponse())
                    return@post
                }
                
                try {
                    val request = call.receive<CreateTodoRequest>()
                    
                    // Validate input
                    val titleValidation = ValidationService.validateTodoTitle(request.title)
                    val descValidation = ValidationService.validateTodoDescription(request.description)
                    
                    val allErrors = mutableListOf<String>()
                    allErrors.addAll(titleValidation.errors)
                    allErrors.addAll(descValidation.errors)
                    
                    if (allErrors.isNotEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, allErrors.toValidationErrorResponse())
                        return@post
                    }
                    
                    val todo = Todo(
                        title = request.title.trim(),
                        description = request.description?.trim(),
                        userId = userId
                    )
                    
                    val createdTodo = TodoRepository.create(todo)
                    call.respond(HttpStatusCode.Created, createdTodo.toSuccessResponse("Todo created successfully"))
                    
                } catch (e: Exception) {
                    call.application.log.error("Todo creation error", e)
                    call.respond(HttpStatusCode.BadRequest, "Invalid request format".toErrorResponse())
                }
            }
            
            // PUT /api/todos/{id} - Update todo
            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("user_id")?.asString()
                val todoId = call.parameters["id"]
                
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token".toErrorResponse())
                    return@put
                }
                
                if (todoId.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid todo ID".toErrorResponse())
                    return@put
                }
                
                try {
                    val request = call.receive<UpdateTodoRequest>()
                    val existingTodo = TodoRepository.findById(todoId)
                    
                    if (existingTodo == null) {
                        call.respond(HttpStatusCode.NotFound, "Todo not found".toErrorResponse())
                        return@put
                    }
                    
                    // Check if todo belongs to the authenticated user
                    if (existingTodo.userId != userId) {
                        call.respond(HttpStatusCode.Forbidden, "Access denied".toErrorResponse())
                        return@put
                    }
                    
                    // Validate input
                    val titleValidation = if (request.title != null) 
                        ValidationService.validateTodoTitle(request.title) 
                        else ValidationService.ValidationResult(true, emptyList())
                    val descValidation = ValidationService.validateTodoDescription(request.description)
                    
                    val allErrors = mutableListOf<String>()
                    allErrors.addAll(titleValidation.errors)
                    allErrors.addAll(descValidation.errors)
                    
                    if (allErrors.isNotEmpty()) {
                        call.respond(HttpStatusCode.BadRequest, allErrors.toValidationErrorResponse())
                        return@put
                    }
                    
                    val updatedTodo = existingTodo.copy(
                        title = request.title?.trim() ?: existingTodo.title,
                        description = request.description?.trim() ?: existingTodo.description,
                        isCompleted = request.isCompleted ?: existingTodo.isCompleted
                    )
                    
                    TodoRepository.update(todoId, updatedTodo)
                    call.respond(HttpStatusCode.OK, updatedTodo.toSuccessResponse("Todo updated successfully"))
                    
                } catch (e: Exception) {
                    call.application.log.error("Todo update error", e)
                    call.respond(HttpStatusCode.BadRequest, "Invalid request format".toErrorResponse())
                }
            }
            
            // DELETE /api/todos/{id} - Delete todo
            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("user_id")?.asString()
                val todoId = call.parameters["id"]
                
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token".toErrorResponse())
                    return@delete
                }
                
                if (todoId.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid todo ID".toErrorResponse())
                    return@delete
                }
                
                val existingTodo = TodoRepository.findById(todoId)
                if (existingTodo == null) {
                    call.respond(HttpStatusCode.NotFound, "Todo not found".toErrorResponse())
                    return@delete
                }
                
                // Check if todo belongs to the authenticated user
                if (existingTodo.userId != userId) {
                    call.respond(HttpStatusCode.Forbidden, "Access denied".toErrorResponse())
                    return@delete
                }
                
                TodoRepository.delete(todoId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
} 