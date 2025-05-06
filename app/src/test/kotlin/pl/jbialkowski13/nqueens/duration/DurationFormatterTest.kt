package pl.jbialkowski13.nqueens.duration

import assertk.assertThat
import assertk.assertions.isEqualTo
import pl.jbialkowski13.nqueens.FakeLocaleProvider
import kotlin.time.Duration
import org.junit.jupiter.api.Named.named
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.time.Duration.Companion.seconds

internal class DurationFormatterTest {

    private val localeProvider = FakeLocaleProvider()

    private val durationFormatter = DurationFormatter(localeProvider)

    @ParameterizedTest
    @MethodSource("testData")
    fun `formats duration`(testData: TestData) {
        val actual = durationFormatter.format(testData.duration)
        assertThat(actual).isEqualTo(testData.expected)
    }

    companion object {

        data class TestData(
            val duration: Duration,
            val expected: String
        )

        @JvmStatic
        fun testData() = listOf(
            named(
                "Format 0 seconds",
                TestData(
                    duration = Duration.ZERO,
                    expected = "00:00"
                )
            ),
            named(
                "Format 1 second",
                TestData(
                    duration = 1.seconds,
                    expected = "00:01"
                )
            ),
            named(
                "Format 59 seconds",
                TestData(
                    duration = 59.seconds,
                    expected = "00:59"
                )
            ),
            named(
                "Format 60 seconds",
                TestData(
                    duration = 60.seconds,
                    expected = "01:00"
                )
            ),
            named(
                "Format 61 seconds",
                TestData(
                    duration = 61.seconds,
                    expected = "01:01"
                )
            ),
            named(
                "Format 3599 seconds",
                TestData(
                    duration = 3599.seconds,
                    expected = "59:59"
                )
            ),
            named(
                "Format 3600 seconds",
                TestData(
                    duration = 3600.seconds,
                    expected = "01:00:00"
                )
            )
        )
    }
}
