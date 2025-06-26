
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "kg.erkan"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

// Note: buildFatJar task is automatically provided by Ktor plugin
// JAR file will be created at: build/libs/ktor-dummy-all.jar

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.csrf)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.swagger)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    
    // Database dependencies
    implementation("org.jetbrains.exposed:exposed-core:0.54.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.54.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.54.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.54.0")
    implementation("org.postgresql:postgresql:42.7.7") // PostgreSQL - production database!
    implementation("com.zaxxer:HikariCP:5.1.0")
    
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
