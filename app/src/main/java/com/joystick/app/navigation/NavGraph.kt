package com.joystick.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.joystick.app.presentation.gamedetail.GameDetailScreen
import com.joystick.app.presentation.gamelist.GameListScreen

/**
 * Defines the app's navigation routes.
 */
object Routes {
    const val GAME_LIST = "game_list"
    const val GAME_DETAIL = "game_detail/{gameId}"

    fun gameDetail(gameId: Int): String = "game_detail/$gameId"
}

/**
 * Top-level navigation graph for the Joystick app.
 */
@Composable
fun JoystickNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.GAME_LIST
    ) {
        composable(route = Routes.GAME_LIST) {
            GameListScreen(
                onGameClick = { gameId ->
                    navController.navigate(Routes.gameDetail(gameId))
                }
            )
        }

        composable(
            route = Routes.GAME_DETAIL,
            arguments = listOf(
                navArgument("gameId") { type = NavType.IntType }
            )
        ) {
            GameDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
