package kg.erkan

import io.ktor.server.application.*
import kg.erkan.database.DatabaseService

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDatabase() // PostgreSQL database enabled!
    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureRouting()
}

fun Application.configureDatabase() {
    val config = environment.config
    
    val rawUrl = config.property("database.url").getString()
    
    // Convert Render's postgresql:// URL to proper JDBC format with port
    val url = when {
        rawUrl.startsWith("postgresql://") -> {
            // Render format: postgresql://user:pass@host/database
            // Need to convert to: jdbc:postgresql://host:5432/database
            val withoutProtocol = rawUrl.removePrefix("postgresql://")
            val parts = withoutProtocol.split("@")
            if (parts.size == 2) {
                val hostAndDb = parts[1]
                // Add default PostgreSQL port if not present
                if (!hostAndDb.contains(":")) {
                    val hostParts = hostAndDb.split("/")
                    if (hostParts.size == 2) {
                        "jdbc:postgresql://${hostParts[0]}:5432/${hostParts[1]}"
                    } else {
                        "jdbc:postgresql://$hostAndDb"
                    }
                } else {
                    "jdbc:postgresql://$hostAndDb"
                }
            } else {
                // Fallback to just adding jdbc: prefix
                "jdbc:$rawUrl"
            }
        }
        rawUrl.startsWith("jdbc:") -> rawUrl
        else -> "jdbc:postgresql://$rawUrl"
    }
    
    // For Render, extract credentials from DATABASE_URL if needed
    val dbUser = if (rawUrl.startsWith("postgresql://")) {
        val match = Regex("postgresql://([^:]+):([^@]+)@").find(rawUrl)
        match?.groupValues?.get(1) ?: config.property("database.user").getString()
    } else {
        config.property("database.user").getString()
    }
    
    val dbPassword = if (rawUrl.startsWith("postgresql://")) {
        val match = Regex("postgresql://[^:]+:([^@]+)@").find(rawUrl)
        match?.groupValues?.get(1) ?: config.property("database.password").getString()
    } else {
        config.property("database.password").getString()
    }
    
    val driver = config.property("database.driver").getString()
    val maxPoolSize = config.propertyOrNull("database.maxPoolSize")?.getString()?.toInt() ?: 10
    val autoCreateTables = config.propertyOrNull("database.autoCreateTables")?.getString()?.toBoolean() ?: true
    
    try {
        DatabaseService.init(
            url = url,
            driver = driver,
            user = dbUser,
            password = dbPassword,
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
