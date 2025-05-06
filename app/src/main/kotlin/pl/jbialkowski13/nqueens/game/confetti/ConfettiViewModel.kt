package pl.jbialkowski13.nqueens.game.confetti

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pl.jbialkowski13.nqueens.duration.DurationFormatter
import pl.jbialkowski13.nqueens.navigation.MainDestination
import pl.jbialkowski13.nqueens.navigation.Navigator
import kotlin.time.Duration.Companion.milliseconds

@Stable
@HiltViewModel(assistedFactory = ConfettiViewModel.Factory::class)
internal class ConfettiViewModel @AssistedInject constructor(
    @Assisted private val durationMillis: Long,
    private val durationFormatter: DurationFormatter,
    private val navigator: Navigator
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(durationMillis: Long): ConfettiViewModel
    }

    var state by mutableStateOf(createState())
        private set

    fun onCloseClick() {
        viewModelScope.launch {
            navigator.navigateTo(MainDestination, clearBackStack = true)
        }
    }

    private fun createState(): ConfettiUiState = ConfettiUiState(
        time = durationFormatter.format(durationMillis.milliseconds)
    )
}
