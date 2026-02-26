package com.joystick.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.*
import androidx.navigation3.ui.*
import com.joystick.app.presentation.gamedetail.GameDetailScreen
import com.joystick.app.presentation.gamelist.GameListScreen

@Composable
fun JoystickNavGraph() {
    val backStack = rememberNavBackStack(GameListRoute)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeAt(backStack.size - 1) },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<GameListRoute> {
                GameListScreen(
                    onGameClick = { gameId ->
                        backStack.add(GameDetailRoute(gameId = gameId))
                    }
                )
            }

            entry<GameDetailRoute> { routeKey ->
                GameDetailScreen(
                    gameId = routeKey.gameId,
                    onBackClick = { backStack.removeAt(backStack.size - 1) }
                )
            }
        }
    )
}
