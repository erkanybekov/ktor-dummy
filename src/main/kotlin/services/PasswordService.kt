package kg.erkan.services

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordService {
    private const val ITERATIONS = 100_000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 32
    private val random = SecureRandom()
    
    fun hashPassword(password: String): String {
        val salt = generateSalt()
        val hash = hashPassword(password, salt)
        return "${Base64.getEncoder().encodeToString(salt)}:${Base64.getEncoder().encodeToString(hash)}"
    }
    
    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        val parts = hashedPassword.split(":")
        if (parts.size != 2) return false
        
        val salt = Base64.getDecoder().decode(parts[0])
        val hash = Base64.getDecoder().decode(parts[1])
        
        val testHash = hashPassword(password, salt)
        return hash.contentEquals(testHash)
    }
    
    private fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return salt
    }
    
    private fun hashPassword(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }
} 