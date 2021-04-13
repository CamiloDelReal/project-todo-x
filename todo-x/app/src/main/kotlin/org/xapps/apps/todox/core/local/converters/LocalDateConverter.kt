package org.xapps.apps.todox.core.local.converters

import androidx.room.TypeConverter
import org.xapps.apps.todox.core.utils.parseToString
import org.xapps.apps.todox.viewmodels.Constants
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class LocalDateConverter {

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.parseToString(Constants.DATE_PATTERN_DB) ?: Constants.DATE_PATTERN_DB_NULL
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let {
            if (it.startsWith(Constants.DATE_PATTERN_DB_NULL)) {
                null
            } else {
                val formatter = DateTimeFormatter.ofPattern(Constants.DATE_PATTERN_DB)
                LocalDate.parse(it, formatter)
            }
        }
    }
}