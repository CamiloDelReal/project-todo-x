package org.xapps.apps.todox.core.utils

import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


fun LocalTime.parseToString(format: String? = null, use24Hour: Boolean = true): String {
    val pattern = format ?: if(use24Hour) {
            "HH:mm"
        } else {
            "hh:mm a"
        }
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return format(formatter)
}

fun LocalDate.parseToString(format: String? = null): String {
    val pattern = format ?: "dd/MM/yyyy"
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return format(formatter)
}