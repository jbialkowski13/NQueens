package pl.jbialkowski13.nqueens.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pl.jbialkowski13.nqueens.R
import pl.jbialkowski13.nqueens.theme.NQueensTheme

@Composable
internal fun MainDestinationScreen(
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    MainDestinationScreen(
        state = viewModel.state,
        onStartGameClick = viewModel::onStartGameClick,
        onScoresClick = viewModel::onScoresClick,
        onDecrementClick = viewModel::onDecrementClick,
        onIncrementClick = viewModel::onIncrementClick
    )

    LaunchedEffect(Unit) { viewModel.init() }
}

@Composable
private fun MainDestinationScreen(
    state: MainUiState,
    onStartGameClick: () -> Unit,
    onScoresClick: () -> Unit,
    onDecrementClick: () -> Unit,
    onIncrementClick: () -> Unit
) {
    Scaffold(
        topBar = { TopBar() },
        content = { contentPadding ->
            Content(
                modifier = Modifier.padding(contentPadding),
                currentSize = state.currentSize,
                incrementEnabled = state.incrementEnabled,
                decrementEnabled = state.decrementEnabled,
                onDecrementClick = onDecrementClick,
                onIncrementClick = onIncrementClick
            )
        },
        bottomBar = {
            BottomBar(
                onStartGameClick = onStartGameClick,
                onScoresClick = onScoresClick
            )
        }
    )
}

@Composable
private fun TopBar() {
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) }
    )
}

@Composable
private fun Content(
    currentSize: String,
    incrementEnabled: Boolean,
    decrementEnabled: Boolean,
    onDecrementClick: () -> Unit,
    onIncrementClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.select_board_size),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDecrementClick,
                enabled = decrementEnabled
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_remove),
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier.width(100.dp),
                text = currentSize,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge
            )
            IconButton(
                onClick = onIncrementClick,
                enabled = incrementEnabled
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun BottomBar(
    onStartGameClick: () -> Unit,
    onScoresClick: () -> Unit,
) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onScoresClick
            ) {
                IconTextContent(
                    icon = R.drawable.ic_leaderboard,
                    text = stringResource(R.string.scores)
                )
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = onStartGameClick
            ) {
                IconTextContent(
                    icon = R.drawable.ic_play_arrow,
                    text = stringResource(R.string.start_new_game)
                )
            }
        }
    }
}

@Composable
private fun IconTextContent(
    @DrawableRes icon: Int,
    text: String,
) {
    Icon(painter = painterResource(icon), contentDescription = null)
    Spacer(modifier = Modifier.width(4.dp))
    Text(text = text)
}

@Composable
@PreviewFontScale
private fun MainDestinationScreenPreview() {
    NQueensTheme {
        Surface {
            MainDestinationScreen(
                state = MainUiState(
                    currentSize = "20",
                    decrementEnabled = true,
                    incrementEnabled = true
                ),
                onStartGameClick = {},
                onScoresClick = {},
                onDecrementClick = {},
                onIncrementClick = {}
            )
        }
    }
}
