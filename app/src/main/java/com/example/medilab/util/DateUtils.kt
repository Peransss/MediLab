package com.example.medilab.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val displayFormat = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale("id", "ID"))
    private val dateOnlyFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

    fun formatDisplay(date: Date?): String {
        return date?.let { displayFormat.format(it) } ?: "-"
    }

    fun formatDateOnly(date: Date?): String {
        return date?.let { dateOnlyFormat.format(it) } ?: "-"
    }

    fun now(): Date = Date()
}
