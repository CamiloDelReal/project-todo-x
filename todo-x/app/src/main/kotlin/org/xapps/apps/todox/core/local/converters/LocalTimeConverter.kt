package org.xapps.apps.todox.core.local.converters

import androidx.room.TypeConverter
import java.time.LocalTime


class LocalTimeConverter {

    @TypeConverter
    fun fromLocalTIme(value: LocalTime?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }
}