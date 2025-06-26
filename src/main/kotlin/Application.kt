package kg.erkan

import io.ktor.server.application.*
import kg.erkan.database.DatabaseService

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // configureDatabase() // Disabled for simplicity
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureRouting()
}

fun Application.configureDatabase() {
    val config = environment.config
    
    val url = config.property("database.url").getString()
    val driver = config.property("database.driver").getString()
    val user = config.property("database.user").getString()
    val password = config.property("database.password").getString()
    val maxPoolSize = config.propertyOrNull("database.maxPoolSize")?.getString()?.toInt() ?: 10
    val autoCreateTables = config.propertyOrNull("database.autoCreateTables")?.getString()?.toBoolean() ?: true
    
    try {
        DatabaseService.init(
            url = url,
            driver = driver,
            user = user,
            password = password,
            maxPoolSize = maxPoolSize,
            autoCreateTables = autoCreateTables
        )
        
        // Add shutdown hook to close database connections
        environment.monitor.subscribe(ApplicationStopping) {
            DatabaseService.close()
        }
        
    } catch (e: Exception) {
        log.error("Failed to initialize database", e)
        throw e
    }
}
