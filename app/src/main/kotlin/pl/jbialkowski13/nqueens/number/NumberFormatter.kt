package pl.jbialkowski13.nqueens.number

import pl.jbialkowski13.nqueens.locale.LocaleProvider
import java.text.NumberFormat
import javax.inject.Inject

internal class NumberFormatter @Inject constructor(
    private val localeProvider: LocaleProvider
) {

    fun format(number: Number): String {
        return NumberFormat
            .getNumberInstance(localeProvider.current)
            .format(number)
    }
}
