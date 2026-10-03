package com.example.noter.feature_note.presentation.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeFormat {
    private const val MINUTE = 60_000L
    private const val HOUR = 60 * MINUTE
    private const val DAY = 24 * HOUR

    /** "Just now", "12m ago", "3h ago", "Yesterday", "Mon", "3 Oct". */
    fun relative(timestamp: Long, now: Long = System.currentTimeMillis()): String {
        val diff = now - timestamp
        return when {
            diff < MINUTE -> "Just now"
            diff < HOUR -> "${diff / MINUTE}m ago"
            diff < DAY && sameDay(timestamp, now) -> "${diff / HOUR}h ago"
            diff < 2 * DAY && sameDay(timestamp, now - DAY) -> "Yesterday"
            diff < 7 * DAY -> SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(timestamp))
            else -> SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(timestamp))
        }
    }

    /** "Saturday, 3 October" */
    fun today(now: Long = System.currentTimeMillis()): String =
        SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date(now))

    /** "3 Oct 2026, 14:20" */
    fun full(timestamp: Long): String =
        SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault()).format(Date(timestamp))

    private fun sameDay(a: Long, b: Long): Boolean {
        val ca = Calendar.getInstance().apply { timeInMillis = a }
        val cb = Calendar.getInstance().apply { timeInMillis = b }
        return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR) &&
            ca.get(Calendar.DAY_OF_YEAR) == cb.get(Calendar.DAY_OF_YEAR)
    }
}
