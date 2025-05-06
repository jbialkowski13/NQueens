package pl.jbialkowski13.nqueens.game

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.persistentSetOf
import pl.jbialkowski13.nqueens.R
import pl.jbialkowski13.nqueens.theme.NQueensTheme

@Composable
internal fun GameScreen(
    boardSize: Int,
    viewModel: GameScreenViewModel = hiltViewModel<GameScreenViewModel, GameScreenViewModel.Factory> { factory ->
        factory.create(boardSize)
    }
) {
    GameScreen(
        state = viewModel.state,
        onCloseClick = viewModel::onCloseClick,
        onRestartClick = viewModel::onRestartClick,
        onPositionClick = viewModel::onPositionClick
    )

    if (viewModel.state.displayCloseConfirmation) {
        CloseDialog(
            onDismissRequest = viewModel::onCloseCancelled,
            onConfirmClick = viewModel::onCloseConfirmed
        )
    }

    LaunchedEffect(Unit) { viewModel.init() }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    remember(viewModel) { viewModel.collectData() }
        .collectAsStateWithLifecycle(
            initialValue = Unit,
            lifecycle = lifecycle
        )

    val somethingWentWrong = stringResource(R.string.something_went_wrong)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffects
            .collect { uiEffect ->
                when (uiEffect) {
                    GameScreenViewModel.UiEffect.ShowSaveScoreError -> {
                        Toast.makeText(
                            context,
                            somethingWentWrong,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    BackHandler {
        viewModel.onCloseClick()
    }
}

@Composable
private fun GameScreen(
    state: GameUiState,
    onCloseClick: () -> Unit,
    onRestartClick: () -> Unit,
    onPositionClick: (Position) -> Unit,
) {
    var boardScale by remember { mutableFloatStateOf(1f) }

    Scaffold(
        topBar = {
            TopBar(
                queensLeft = state.queensLeft,
                onCloseClick = onCloseClick,
                onZoomOut = { boardScale = boardScale - 0.1f },
                onZoomIn = { boardScale = boardScale + 0.1f }
            )
        },
        content = { contentPadding ->
            Content(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
                boardState = state.boardState,
                scale = boardScale,
                onPositionClick = onPositionClick
            )
        },
        bottomBar = {
            BottomBar(
                onRestartClick = onRestartClick,
                time = state.time
            )
        }
    )
}

@Composable
private fun TopBar(
    queensLeft: String,
    onCloseClick: () -> Unit,
    onZoomOut: () -> Unit,
    onZoomIn: () -> Unit
) {
    MediumTopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onCloseClick
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.queens_left, queensLeft),
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            IconButton(onClick = onZoomOut) {
                Icon(
                    painter = painterResource(R.drawable.ic_zoom_out),
                    contentDescription = null
                )
            }
            IconButton(onClick = onZoomIn) {
                Icon(
                    painter = painterResource(R.drawable.ic_zoom_in),
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun Content(
    boardState: BoardState,
    scale: Float,
    onPositionClick: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Board(
            boardState = boardState,
            scale = scale,
            onPositionClick = onPositionClick
        )
    }
}

@Composable
private fun CloseDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.close_game)) },
        text = { Text(text = stringResource(R.string.close_game_message)) },
        confirmButton = {
            TextButton(onClick = onConfirmClick) { Text(text = stringResource(R.string.yes)) }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text(text = stringResource(R.string.no)) }
        }
    )
}

@Composable
private fun BottomBar(
    onRestartClick: () -> Unit,
    time: String
) {
    BottomAppBar {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onRestartClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_replay),
                    contentDescription = null
                )
            }
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .widthIn(min = 120.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    text = time,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}

@Composable
@PreviewFontScale
private fun GameScreenPreview() {
    NQueensTheme {
        Surface {
            GameScreen(
                state = GameUiState(
                    boardState = BoardState(
                        size = 4,
                        queens = persistentSetOf(),
                        conflictingPositions = persistentSetOf()
                    ),
                    queensLeft = "8",
                    time = "00:20",
                    displayCloseConfirmation = false
                ),
                onCloseClick = {},
                onRestartClick = {},
                onPositionClick = {}
            )
        }
    }
}
