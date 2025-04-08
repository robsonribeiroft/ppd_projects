package org.robsonribeiro.ppd.helper

import androidx.compose.runtime.Stable

@Stable
data class ValidationResult(
    val host: String? = null,
    val port: Int? = null,
    val errorMessage: String? = null
) {
    val isValid: Boolean get() = errorMessage == null && host != null && port != null
}

object NetworkInputValidator {
    private val addressRegex =
        """^((localhost)|(((?:(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)))):([0-9]{1,5})$""".toRegex()

    // More strict port range validation (1-65535)
    private val PORT_RANGE = (1..65535)
    private const val LOCALHOST_IP = "127.0.0.1"

    fun validateServerAddress(input: String): ValidationResult {
        val trimmedInput = input.trim()
        if (trimmedInput.isEmpty()) {
            return ValidationResult(errorMessage = "Address cannot be empty.")
        }

        val match = addressRegex.matchEntire(trimmedInput)
            ?: return ValidationResult(errorMessage = "Invalid format. Use hostname:port or ip:port (e.g., localhost:12345 or 192.168.1.10:8080).")

        val hostInput = match.groupValues[1]
        val isLocalhost = match.groupValues[2].isNotEmpty()

        val hostResultIp = if (isLocalhost) {
            LOCALHOST_IP
        } else {
            hostInput
        }

        val port = match.groupValues[7].toIntOrNull()
        if (port == null || port !in PORT_RANGE) {
            return ValidationResult(errorMessage = "Invalid port number. Must be between $PORT_RANGE.")
        }

        return ValidationResult(host = hostResultIp, port = port)
    }
}