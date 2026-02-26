package com.joystick.app.presentation.gamelist.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.joystick.app.presentation.gamelist.EmptyReason
import com.joystick.app.ui.components.EmptyStateView

@Composable
internal fun GameListEmptyView(
    reason: EmptyReason,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (reason) {
        EmptyReason.NO_SEARCH_RESULTS -> {
            EmptyStateView(
                icon = Icons.Default.Search,
                title = "No Results Found",
                description = "Try a different search term",
                modifier = modifier
            )
        }
        EmptyReason.NO_GENRE_RESULTS -> {
            EmptyStateView(
                icon = Icons.Default.PlayArrow,
                title = "No Games Found",
                description = "No games available for this genre",
                onAction = onRetry,
                actionLabel = "Retry",
                modifier = modifier
            )
        }
    }
}
