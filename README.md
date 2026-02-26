# Joystick 🎮
> Your Game Universe — Browse video games by genre using the RAWG API.

---

## 📸 Screenshots
<!-- Add screenshots here -->
| Game Browser | Game Detail | Trailer |
|---|---|---|
| ![Game Browser]() | ![Game Detail]() | ![Trailer]() |

---

## 🛠️ Tech Stack

| Category | Technology | Reason |
|---|---|---|
| Language | Kotlin | Official Android language, full coroutines support |
| UI | Jetpack Compose + Material 3 | Declarative UI, less boilerplate, modern Android standard |
| Architecture | MVVM + Clean Architecture | Clear separation of concerns, testable, scalable |
| Async | Kotlin Coroutines + StateFlow | Structured concurrency, reactive UI state management |
| Networking | Retrofit 2 + OkHttp | Industry standard, easy REST integration, interceptor support |
| Dependency Injection | Dagger Hilt | Compile-time safe DI, officially recommended by Google |
| Image Loading | Coil | Compose-native, lightweight, coroutine-backed |
| Video Playback | AndroidX Media3 ExoPlayer | Official Google player, lifecycle-aware |
| Local Storage | Room | Type-safe SQLite abstraction, first-party Jetpack library |
| Navigation | Jetpack Compose Navigation 3 | Official Compose navigation with type-safe serialized arguments |
| API | RAWG Video Games Database | Free tier, 500k+ games, covers genres/details/screenshots |

---

## 🏛️ Architecture Choice

### MVVM + Clean Architecture

This project uses **MVVM (Model-View-ViewModel)** combined with **Clean Architecture** principles, organized into 3 distinct layers:

1. **Domain Layer** — The core of the application. Contains entity models (e.g., `Game`, `GameDetail`, `Trailer`) and Use Cases (e.g., `GetGamesUseCase`, `GetGameDetailUseCase`). This layer has zero dependencies on Android frameworks, ensuring business rules remain isolated and fully unit-testable.

2. **Data Layer** — Responsible for fetching and managing data. `GameRepositoryImpl` acts as the single source of truth, communicating with the RAWG API via Retrofit (`RawgApiService`) and caching results using Room (`GameDao`). It maps external DTOs and database Entities to internal Domain models.

3. **Presentation Layer** — Handles UI and user interactions, built entirely with Jetpack Compose. Composables observe UI state (`GameListUiState`, `GameDetailUiState`) emitted by ViewModels via `StateFlow`. Hilt Assisted Injection is used to safely pass runtime arguments (e.g., `gameId`) into ViewModels.

```
app/
├── data/
│   ├── local/          # Room entities, DAOs, database
│   ├── remote/         # Retrofit API service, DTOs, interceptors, mappers
│   └── repository/     # GameRepositoryImpl (single source of truth)
├── domain/
│   ├── model/          # Pure Kotlin domain models
│   ├── repository/     # GameRepository interface
│   └── usecase/        # One class per use case
├── presentation/
│   ├── gamelist/       # GameListScreen + ViewModel + UiState
│   └── gamedetail/     # GameDetailScreen + ViewModel + UiState
├── navigation/         # Type-safe Nav3 routes
├── di/                 # Hilt modules
└── ui/
    ├── components/     # Reusable composables
    └── theme/          # Material 3 color scheme & typography
```

---

## 📝 Assumptions & Shortcuts

During development, a few pragmatic decisions were made to keep the scope focused:

1. **Caching scope** — Room caches only the *first page* of each genre with a rolling 10-minute expiry, providing a basic offline fallback. Deep offline pagination and offline detail viewing were intentionally skipped.

2. **Client-side search** — Search is applied locally over the already-loaded game list rather than issuing a `&search=` API call. This gives instant feedback but limits the search corpus to already-fetched pages.

3. **Thread-safe pagination** — A `Mutex` inside `GameListViewModel` prevents concurrent page requests. The RAWG API can occasionally return inconsistent data when pages are requested in rapid succession.

4. **Generalised error handling** — Network vs. no-content states are clearly distinguished with user-friendly UI. Granular HTTP code mapping (404 vs 500) was consolidated into a single error block to keep scope manageable.

5. **Test coverage** — Core use cases and ViewModels have unit tests scaffolded using MockK + Turbine + Truth. Full UI/instrumentation test coverage was considered out of scope for the initial release.
