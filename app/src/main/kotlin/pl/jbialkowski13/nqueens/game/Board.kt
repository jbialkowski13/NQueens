package pl.jbialkowski13.nqueens.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentSetOf
import pl.jbialkowski13.nqueens.R
import pl.jbialkowski13.nqueens.theme.NQueensTheme

@Composable
internal fun Board(
    boardState: BoardState,
    scale: Float,
    onPositionClick: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .horizontalScroll(rememberScrollState())
            .border(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column {
            repeat(boardState.size) { row ->
                Row {
                    repeat(boardState.size) { column ->
                        val even = (row + column) % 2 == 0
                        val position = Position(row = row, column = column)
                        Square(
                            scale = scale,
                            even = even,
                            hasQueen = boardState.queens.contains(position),
                            hasConflict = boardState.conflictingPositions.contains(position),
                            onClick = { onPositionClick(position) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Square(
    even: Boolean,
    hasQueen: Boolean,
    hasConflict: Boolean,
    scale: Float,
    onClick: () -> Unit
) {
    val squareColor = squareColor(even = even, hasConflict = hasConflict)

    Box(
        modifier = Modifier
            .size(48.dp * scale)
            .drawBehind { drawRect(color = squareColor.value) }
            .border(Dp.Hairline, MaterialTheme.colorScheme.outline)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (hasQueen) {
            Icon(
                modifier = Modifier.size(36.dp * scale),
                painter = painterResource(R.drawable.ic_queen),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun squareColor(even: Boolean, hasConflict: Boolean): State<Color> {
    val baseColor = if (even) {
        MaterialTheme.colorScheme.surfaceContainerLowest
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
    val errorColor = MaterialTheme.colorScheme.error.copy(alpha = 0.3f)

    return animateColorAsState(if (hasConflict) errorColor else baseColor)
}

@Composable
@PreviewLightDark
private fun BoardPreview() {
    NQueensTheme {
        Surface {
            Board(
                boardState = BoardState(
                    size = 8,
                    queens = persistentSetOf(Position(0, 0)),
                    conflictingPositions = persistentSetOf(
                        Position(1, 1),
                        Position(2, 2),
                        Position(0, 1)
                    )
                ),
                scale = 1f,
                onPositionClick = {}
            )
        }
    }
}
