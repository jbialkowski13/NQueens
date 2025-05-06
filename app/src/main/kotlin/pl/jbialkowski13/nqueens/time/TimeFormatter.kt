package pl.jbialkowski13.nqueens.time

import java.time.Instant
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class TimeFormatter @Inject constructor() {

    fun formatAsLocalDateTime(instant: Instant): String {
        val zoneId = systemDefault()
        val localDateTime = instant.atZone(zoneId).toLocalDateTime()

        return localDateTime.format(formatter).toString()
    }

    private companion object {
        const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm"

        val formatter: DateTimeFormatter by lazy {
            DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
        }
    }
}
