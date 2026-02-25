package com.joystick.app.presentation.genrepicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenrePickerScreen(
    onGenreSelected: (String) -> Unit
) {
    val genres = listOf(
        "action", "indie", "adventure", "rpg", "strategy", "shooter",
        "casual", "simulation", "puzzle", "arcade", "platformer",
        "racing", "sports", "fighting", "family"
    )
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Select Genre") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            items(genres) { genre ->
                ListItem(
                    headlineContent = { Text(genre.replaceFirstChar { it.uppercase() }) },
                    modifier = Modifier.clickable { onGenreSelected(genre) }
                )
            }
        }
    }
}
