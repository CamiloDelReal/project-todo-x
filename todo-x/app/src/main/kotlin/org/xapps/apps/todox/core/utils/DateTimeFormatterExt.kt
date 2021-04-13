package org.xapps.apps.todox.core.utils

import org.xapps.apps.todox.viewmodels.Constants
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


fun LocalTime.parseToString(format: String? = null, use24Hour: Boolean = true): String {
    val pattern = format ?: if(use24Hour) {
            Constants.TIME_PATTERN_24H
        } else {
            Constants.TIME_PATTERN
        }
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return format(formatter)
}

fun LocalDate.parseToString(format: String? = null): String {
    val pattern = format ?: Constants.DATE_PATTERN_UI
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return format(formatter)
}