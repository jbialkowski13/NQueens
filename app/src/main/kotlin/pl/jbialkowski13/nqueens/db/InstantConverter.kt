package pl.jbialkowski13.nqueens.db

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

internal class InstantConverter {

    @TypeConverter
    fun toInstant(date: String?): Instant? {
        return date?.let { LocalDateTime.parse(it, formatter).toInstant(ZoneOffset.UTC) }
    }

    @TypeConverter
    fun toDbDate(date: Instant?): String? {
        return date?.let { formatter.withZone(ZoneOffset.UTC).format(it) }
    }

    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    }
}
