package pl.jbialkowski13.nqueens.game

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Named.named
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class ConflictsResolverImplTest {

    private val conflictsResolver = ConflictsResolverImpl()

    @ParameterizedTest
    @MethodSource("testData")
    fun `should resolve conflicts correctly`(testData: TestData) {
        val actual = conflictsResolver.resolve(testData.queens, testData.position)
        assertThat(actual).isEqualTo(testData.expectedConflicts)
    }

    companion object {

        data class TestData(
            val queens: Set<Position>,
            val position: Position,
            val expectedConflicts: Set<Position>
        )

        @JvmStatic
        fun testData() = listOf(
            named(
                "No queens on the board, no conflicts",
                TestData(
                    queens = emptySet(),
                    position = Position(0, 0),
                    expectedConflicts = emptySet()
                )
            ),
            named(
                "One queen on the board, no conflicts if not in the same row, column or diagonal",
                TestData(
                    queens = setOf(Position(1, 2)),
                    position = Position(0, 0),
                    expectedConflicts = emptySet()
                )
            ),
            named(
                "One queen on the board, conflicts if in the same row",
                TestData(
                    queens = setOf(Position(1, 2)),
                    position = Position(1, 0),
                    expectedConflicts = setOf(Position(1, 2))
                )
            ),
            named(
                "One queen on the board, conflicts if in the same column",
                TestData(
                    queens = setOf(Position(1, 2)),
                    position = Position(0, 2),
                    expectedConflicts = setOf(Position(1, 2))
                )
            ),
            named(
                "One queen on the board, conflicts if in the same diagonal",
                TestData(
                    queens = setOf(Position(1, 2)),
                    position = Position(2, 1),
                    expectedConflicts = setOf(Position(1, 2))
                )
            ),
            named(
                "Multiple queens on the board, conflicts if in the same row",
                TestData(
                    queens = setOf(Position(1, 2), Position(2, 3)),
                    position = Position(1, 0),
                    expectedConflicts = setOf(Position(1, 2))
                )
            ),
            named(
                "Multiple queens on the board, conflicts if in the same column",
                TestData(
                    queens = setOf(Position(1, 2), Position(2, 3)),
                    position = Position(0, 2),
                    expectedConflicts = setOf(Position(1, 2))
                )
            ),
            named(
                "Multiple queens on the board, conflicts if in the same diagonal",
                TestData(
                    queens = setOf(Position(1, 2), Position(2, 3)),
                    position = Position(0, 1),
                    expectedConflicts = setOf(Position(1, 2), Position(2, 3))
                )
            ),
            named(
                "Multiple queens on the board, no conflicts if not in the same row, column or diagonal",
                TestData(
                    queens = setOf(Position(1, 2), Position(2, 3)),
                    position = Position(0, 0),
                    expectedConflicts = emptySet()
                )
            )
        )
    }
}
