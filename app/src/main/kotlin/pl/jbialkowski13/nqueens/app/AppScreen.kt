package pl.jbialkowski13.nqueens.app

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import pl.jbialkowski13.nqueens.navigation.MainDestination
import pl.jbialkowski13.nqueens.navigation.NavigationEvent

@Composable
internal fun AppScreen(
    viewModel: AppScreenViewModel = hiltViewModel()
) {
    val activity = LocalActivity.current
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainDestination
    ) {
        viewModel.destinationBuilders.forEach { destinationBuilder ->
            with(destinationBuilder) {
                add(navController)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEventListener.events.collect { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.destination) {
                        when {
                            event.clearBackStack -> popUpTo(0)
                        }
                    }
                }

                NavigationEvent.NavigateBack -> {
                    val popped = navController.popBackStack()
                    if (!popped) {
                        activity?.finish()
                    }
                }
            }
        }
    }
}
