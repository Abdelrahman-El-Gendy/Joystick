package com.joystick.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joystick.app.ui.theme.JoystickTheme

/**
 * Categorizes error messages into user-friendly error types.
 */
enum class ErrorType(
    val icon: ImageVector,
    val title: String,
    val description: String
) {
    NETWORK(
        icon = Icons.Outlined.WifiOff,
        title = "No Internet Connection",
        description = "It looks like you're offline. Check your Wi-Fi or mobile data and try again."
    ),
    SERVER(
        icon = Icons.Outlined.CloudOff,
        title = "Server Unavailable",
        description = "We couldn't reach our servers right now. Please try again in a moment."
    ),
    GENERIC(
        icon = Icons.Outlined.ErrorOutline,
        title = "Something Went Wrong",
        description = "An unexpected error occurred. Please try again."
    );

    companion object {
        fun from(errorMessage: String): ErrorType {
            val lower = errorMessage.lowercase()
            return when {
                lower.contains("unable to resolve host") ||
                lower.contains("no address associated") ||
                lower.contains("unreachable") ||
                lower.contains("network") ||
                lower.contains("connect") && lower.contains("fail") ||
                lower.contains("no internet") ||
                lower.contains("socketexception") ||
                lower.contains("unknownhostexception") -> NETWORK

                lower.contains("500") ||
                lower.contains("502") ||
                lower.contains("503") ||
                lower.contains("504") ||
                lower.contains("server") ||
                lower.contains("timeout") -> SERVER

                else -> GENERIC
            }
        }
    }
}

/**
 * A polished, production-ready error screen that shows user-friendly
 * messaging based on the type of error encountered.
 */
@Composable
fun ErrorStateView(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val errorType = ErrorType.from(errorMessage)

    // Subtle pulsing animation for the icon background
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon with circular background
        Box(
            modifier = Modifier
                .size(96.dp)
                .alpha(pulseAlpha)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = errorType.icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.85f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Title
        Text(
            text = errorType.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description
        Text(
            text = errorType.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Retry button
        FilledTonalButton(
            onClick = onRetry,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Try Again",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorStateViewNetworkPreview() {
    JoystickTheme {
        ErrorStateView(
            errorMessage = "Unable to resolve host \"api.rawg.io\": No address associated with hostname",
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorStateViewServerPreview() {
    JoystickTheme {
        ErrorStateView(
            errorMessage = "HTTP 500 Internal Server Error",
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorStateViewGenericPreview() {
    JoystickTheme {
        ErrorStateView(
            errorMessage = "Something unexpected happened",
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorStateViewDarkPreview() {
    JoystickTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ErrorStateView(
                errorMessage = "Unable to resolve host \"api.rawg.io\": No address associated with hostname",
                onRetry = {}
            )
        }
    }
}
