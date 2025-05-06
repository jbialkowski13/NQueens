package pl.jbialkowski13.nqueens.game.confetti

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import pl.jbialkowski13.nqueens.R
import pl.jbialkowski13.nqueens.theme.NQueensTheme
import java.util.concurrent.TimeUnit

@Composable
internal fun ConfettiScreen(
    durationMillis: Long,
    viewModel: ConfettiViewModel = hiltViewModel<ConfettiViewModel, ConfettiViewModel.Factory> { factory ->
        factory.create(durationMillis)
    }
) {
    ConfettiScreen(
        state = viewModel.state,
        onCloseClick = viewModel::onCloseClick
    )
}

@Composable
private fun ConfettiScreen(
    state: ConfettiUiState,
    onCloseClick: () -> Unit
) {
    Scaffold(
        content = { paddingValues ->
            Content(
                modifier = Modifier
                    .padding(paddingValues),
                state = state,
                onCloseClick = onCloseClick
            )
        }
    )
    BackHandler { onCloseClick() }
}

@Composable
private fun Content(
    state: ConfettiUiState,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Center
    ) {
        val parties = remember {
            listOf(
                Party(emitter = Emitter(duration = 5, TimeUnit.SECONDS).perSecond(30))
            )
        }

        KonfettiView(
            modifier = Modifier.fillMaxSize(),
            parties = parties
        )

        Text(
            text = stringResource(R.string.confetti_message),
            style = MaterialTheme.typography.headlineLarge
        )

        Column(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.confetti_time, state.time),
                style = MaterialTheme.typography.headlineSmall,
            )
            Button(onClick = onCloseClick) {
                Text(
                    text = stringResource(R.string.close)
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun ConfettiScreenPreview() {
    NQueensTheme {
        Surface {
            ConfettiScreen(
                state = ConfettiUiState(
                    time = "02:10",
                ),
                onCloseClick = {}
            )
        }
    }
}
