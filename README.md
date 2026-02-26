# Joystick 🎮

A modern Android app for browsing video games by genre using the RAWG API.

## Tech Stack
| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| Async | Coroutines + StateFlow |
| Networking | Retrofit 2 + OkHttp |
| DI | Dagger Hilt |
| Image Loading | Coil |
| Video Playback | Media3 ExoPlayer |
| Local Storage | Room |
| Navigation | Jetpack Compose Navigation |

## Architecture
3-layer Clean Architecture: data / domain / presentation.
State driven by StateFlow + sealed UiState interfaces.
Zero business logic in Composables.

## Build Instructions
1. Clone the repo
2. Get free API key at https://rawg.io/apidocs
3. Add to local.properties: rawg_api_key=YOUR_KEY
4. Run: `./gradlew assembleDebug`

## Assumptions & Shortcuts
- Genre is selected by user from a hardcoded list of 10 RAWG genre slugs
- Search is local/in-memory — no server-side search
- InitialLoading uses shimmer cards; pagination uses footer spinner (visually distinct)
- `description_raw` used from API (plain text, not HTML)
- Offline cache: Room, offline-first for page 1, 10min expiry, stale fallback on failure
- Trailer/screenshot failures are silent — optional enhancements only
- Dark theme forced — no light theme
- API key in `local.properties` — never committed to VCS
