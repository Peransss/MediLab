package com.example.medilab.util

object Validators {
    private val allowedEmailDomains = setOf(
        "gmail.com",
        "yahoo.com",
        "yahoo.co.id",
        "outlook.com",
        "hotmail.com",
        "mail.com",
        "protonmail.com",
        "icloud.com"
    )

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isAllowedEmailDomain(email: String): Boolean {
        val domain = email.substringAfterLast("@").lowercase()
        return allowedEmailDomains.contains(domain)
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^[+]?[0-9]{10,15}$"))
    }

    fun isNotEmpty(vararg fields: String): Boolean {
        return fields.all { it.isNotBlank() }
    }
}

