package com.joystick.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.joystick.app.presentation.gamedetail.GameDetailScreen
import com.joystick.app.presentation.gamelist.GameListScreen
import com.joystick.app.presentation.genrepicker.GenrePickerScreen

@Composable
fun JoystickNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "game_list/action"
    ) {
        composable(
            route = "game_list/{genre}",
            arguments = listOf(
                navArgument("genre") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            GameListScreen(
                genre = backStackEntry.arguments?.getString("genre") ?: "action",
                onGameClick = { id ->
                    navController.navigate("game_detail/$id")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "game_detail/{gameId}",
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
