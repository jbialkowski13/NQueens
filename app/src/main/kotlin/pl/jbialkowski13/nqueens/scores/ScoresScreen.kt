package pl.jbialkowski13.nqueens.scores

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import pl.jbialkowski13.nqueens.R
import pl.jbialkowski13.nqueens.theme.NQueensTheme

@Composable
internal fun ScoresScreen(
    viewModel: ScoresViewModel = hiltViewModel()
) {
    ScoresScreen(
        state = viewModel.state,
        onBackClick = viewModel::onBackClick
    )

    LaunchedEffect(Unit) { viewModel.init() }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    remember(viewModel) { viewModel.collectData() }
        .collectAsStateWithLifecycle(
            initialValue = Unit,
            lifecycle = lifecycle
        )
}

@Composable
private fun ScoresScreen(
    state: ScoresUiState,
    onBackClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        contentWindowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0),
        topBar = { TopBar(onBackClick = onBackClick, scrollBehavior = scrollBehavior) },
        content = { paddingValues ->
            val modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))

            Content(
                modifier = modifier,
                scores = state.scores,
                scrollBehavior = scrollBehavior
            )
        }
    )
}

@Composable
private fun TopBar(
    onBackClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = { Text(stringResource(R.string.scores)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun Content(
    scores: ImmutableList<Score>,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    if (scores.isEmpty()) {
        EmptyScores(modifier = modifier)
    } else {
        ScoresList(
            modifier = modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            scores = scores
        )
    }
}

@Composable
private fun EmptyScores(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.scores_empty),
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

@Composable
private fun ScoresList(
    scores: ImmutableList<Score>,
    modifier: Modifier = Modifier
) {
    val listContentPadding = WindowInsets
        .safeDrawing
        .only(WindowInsetsSides.Bottom)
        .asPaddingValues()

    LazyColumn(
        modifier = modifier
            .consumeWindowInsets(listContentPadding),
        contentPadding = listContentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(scores, key = { it.id }) { score ->
            ScoreListItem(
                score = score,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun ScoreListItem(
    score: Score,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.board_size, score.boardSize),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = stringResource(R.string.solve_time, score.time),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = stringResource(R.string.solve_timestamp, score.timestamp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun EmptyScoresScreenPreview() {
    NQueensTheme {
        Surface {
            ScoresScreen(
                state = ScoresUiState(
                    scores = persistentListOf()
                ),
                onBackClick = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun ScoresScreenPreview() {
    NQueensTheme {
        Surface {
            ScoresScreen(
                state = ScoresUiState(
                    scores = persistentListOf(
                        Score(
                            id = 0,
                            time = "00:00:01",
                            boardSize = "4",
                            timestamp = "2025-01-01 10:30"
                        )
                    )
                ),
                onBackClick = {}
            )
        }
    }
}
