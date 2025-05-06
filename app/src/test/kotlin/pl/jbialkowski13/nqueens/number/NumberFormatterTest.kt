package pl.jbialkowski13.nqueens.number

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Named.named
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import pl.jbialkowski13.nqueens.FakeLocaleProvider
import java.util.Locale

internal class NumberFormatterTest {

    private val localeProvider = FakeLocaleProvider()

    private val formatter = NumberFormatter(localeProvider)

    @ParameterizedTest
    @MethodSource("testData")
    fun `formats number`(testData: TestData) {
        localeProvider.current = testData.locale

        val actual = formatter.format(number = testData.number)

        assertThat(actual).isEqualTo(testData.expected)
    }

    companion object {

        data class TestData(
            val number: Number,
            val locale: Locale,
            val expected: String
        )

        val arabic = Locale.forLanguageTag("ar")

        @JvmStatic
        fun testData() = listOf(
            named(
                "Formats int number using English locale",
                TestData(number = 1, locale = Locale.ENGLISH, expected = "1")
            ),
            named(
                "Formats long number using English locale",
                TestData(number = 1L, locale = Locale.ENGLISH, expected = "1")
            ),
            named(
                "Formats double number using English locale",
                TestData(number = 1.1, locale = Locale.ENGLISH, expected = "1.1")
            ),
            named(
                "Formats float number using English locale",
                TestData(number = 1.1f, locale = Locale.ENGLISH, expected = "1.1")
            ),

            // arabic
            named(
                "Formats int number using Arabic locale",
                TestData(number = 1, locale = arabic, expected = "١")
            ),
            named(
                "Formats long number using Arabic locale",
                TestData(number = 1L, locale = arabic, expected = "١")
            ),
            named(
                "Formats double number using Arabic locale",
                TestData(number = 1.1, locale = arabic, expected = "١٫١")
            ),
            named(
                "Formats float number using Arabic locale",
                TestData(number = 1.1f, locale = arabic, expected = "١٫١")
            )
        )
    }
}
