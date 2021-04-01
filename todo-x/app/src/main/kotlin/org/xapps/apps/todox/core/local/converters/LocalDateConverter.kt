package org.xapps.apps.todox.core.local.converters

import androidx.room.TypeConverter
import java.time.LocalDate


class LocalDateConverter {

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.toString() ?: "9999"
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let {
            if (it.startsWith("9999")) {
                null
            } else {
                LocalDate.parse(it)
            }
        }
    }
}