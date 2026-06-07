package com.example.medilab.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

data class PasswordStrengthResult(
    val isStrong: Boolean,
    val message: String?
)

object PasswordStrengthChecker {
    private const val HIBP_API = "https://api.pwnedpasswords.com/range/"
    private const val TIMEOUT_MS = 5000L

    private val cache = mutableMapOf<String, Set<String>>()

    suspend fun isPasswordStrong(password: String): PasswordStrengthResult {
        val localError = checkLocal(password)
        if (localError != null) {
            return PasswordStrengthResult(false, localError)
        }
        val breachCount = checkHibp(password)
        if (breachCount > 0) {
            return PasswordStrengthResult(
                false, "Password ini sudah pernah bocor ($breachCount kali). Gunakan password lain."
            )
        }
        return PasswordStrengthResult(true, null)
    }

    private fun checkLocal(password: String): String? {
        if (password.length < 8) {
            return "Password harus minimal 8 karakter"
        }
        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        if (!hasUpper || !hasLower || !hasDigit) {
            return "Password harus kombinasi huruf besar, huruf kecil, dan angka"
        }
        if (password.length >= 3) {
            var maxRepeat = 1
            var currentRepeat = 1
            for (i in 1 until password.length) {
                if (password[i] == password[i - 1]) {
                    currentRepeat++
                    if (currentRepeat > maxRepeat) maxRepeat = currentRepeat
                } else {
                    currentRepeat = 1
                }
            }
            if (maxRepeat >= 3) {
                return "Password terlalu sederhana (jangan gunakan karakter yang sama berulang)"
            }
        }
        return null
    }

    private suspend fun checkHibp(password: String): Int {
        val sha1 = sha1Hex(password)
        val prefix = sha1.substring(0, 5)
        val suffix = sha1.substring(5)

        val suffixes = cache.getOrPut(prefix) { fetchSuffixes(prefix) }
        val line = suffixes.firstOrNull { it.startsWith(suffix, ignoreCase = true) } ?: return 0
        val count = line.substringAfter(":").trim().toIntOrNull() ?: 0
        return count
    }

    private suspend fun fetchSuffixes(prefix: String): Set<String> {
        return try {
            withContext(Dispatchers.IO) {
                withTimeout(TIMEOUT_MS) {
                    val conn = URL(HIBP_API + prefix).openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.connectTimeout = TIMEOUT_MS.toInt()
                    conn.readTimeout = TIMEOUT_MS.toInt()
                    conn.setRequestProperty("User-Agent", "MediLab-Android")
                    BufferedReader(InputStreamReader(conn.inputStream)).use { it.readLines().toSet() }
                }
            }
        } catch (e: TimeoutCancellationException) {
            emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    private fun sha1Hex(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
