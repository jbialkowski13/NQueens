package pl.jbialkowski13.nqueens

import pl.jbialkowski13.nqueens.locale.LocaleProvider
import java.util.Locale

internal class FakeLocaleProvider : LocaleProvider {

    override var current: Locale = Locale.ENGLISH
}
