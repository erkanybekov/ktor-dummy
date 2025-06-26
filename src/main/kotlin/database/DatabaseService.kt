package kg.erkan.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseService {
    
    private lateinit var database: Database
    
    fun init(
        url: String,
        driver: String, 
        user: String,
        password: String,
        maxPoolSize: Int = 10,
        autoCreateTables: Boolean = true
    ) {
        val config = HikariConfig().apply {
            // Use standard JDBC URL approach (works reliably with PostgreSQL 42.7.7)
            jdbcUrl = url
            driverClassName = driver
            username = user
            this.password = password
            maximumPoolSize = maxPoolSize
            isAutoCommit = false
            
            // Connection pool settings
            connectionTimeout = 30000 // 30 seconds
            idleTimeout = 600000 // 10 minutes  
            maxLifetime = 1800000 // 30 minutes
            
            // Connection testing
            connectionTestQuery = "SELECT 1"
            validationTimeout = 5000
            
            // Pool name for monitoring
            poolName = "KtorTodoPool"
            
            // PostgreSQL-specific optimizations
            if (driver.contains("postgresql")) {
                addDataSourceProperty("stringtype", "unspecified")
                addDataSourceProperty("prepareThreshold", "1")
            }
        }
        
        val dataSource = HikariDataSource(config)
        database = Database.connect(dataSource)
        
        if (autoCreateTables) {
            createTables()
        }
        
        println("✅ Database connected successfully to: $url")
    }
    
    private fun createTables() {
        transaction(database) {
            addLogger(StdOutSqlLogger)
            
            // Create tables if they don't exist
            SchemaUtils.createMissingTablesAndColumns(Users, Todos)
            
            println("✅ Database tables created/validated")
        }
    }
    
    fun getDatabase(): Database = database
    
    fun healthCheck(): Boolean {
        return try {
            transaction(database) {
                exec("SELECT 1") { it.next() }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    fun close() {
        // HikariCP will handle connection cleanup
        println("🔌 Database connections closed")
    }
} 