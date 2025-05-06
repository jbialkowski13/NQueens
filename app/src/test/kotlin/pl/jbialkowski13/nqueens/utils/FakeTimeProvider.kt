package pl.jbialkowski13.nqueens.utils

import pl.jbialkowski13.nqueens.time.TimeProvider
import java.time.Instant
import kotlin.time.TestTimeSource
import kotlin.time.TimeMark

internal class FakeTimeProvider : TimeProvider {

    var timeSource = TestTimeSource()

    val markNow: TimeMark
        get() = timeSource.markNow()

    var instant: Instant = Instant.MIN

    override fun markNow(): TimeMark = markNow

    override fun instant(): Instant = instant
}
