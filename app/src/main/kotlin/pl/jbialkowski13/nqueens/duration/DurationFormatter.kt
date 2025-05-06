package pl.jbialkowski13.nqueens.duration

import pl.jbialkowski13.nqueens.locale.LocaleProvider
import java.util.Formatter
import javax.inject.Inject
import kotlin.time.Duration

internal class DurationFormatter @Inject constructor(
    localeProvider: LocaleProvider
) {

    private val timeStringBuilder = StringBuilder()
    private val formatter = Formatter(timeStringBuilder, localeProvider.current)

    fun format(duration: Duration): String {
        timeStringBuilder.setLength(0)

        val seconds = duration.inWholeSeconds

        val secondsPart = seconds % 60
        val minutesPart = (seconds / 60) % 60
        val hoursPart = seconds / 3600

        if (hoursPart > 0) {
            return formatter
                .format("%02d:%02d:%02d", hoursPart, minutesPart, secondsPart)
                .toString()
        } else {
            return formatter
                .format("%02d:%02d", minutesPart, secondsPart)
                .toString()
        }
    }
}
