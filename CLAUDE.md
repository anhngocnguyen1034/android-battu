# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

BatTu — an Android Bazi (Bát Tự / Four Pillars) chart app, part of the "Anhnn ecosystem". Single-module app (`:app`), package `com.anhnn.battu`. Kotlin 2.0 + Jetpack Compose (Material 3), Hilt, Retrofit + kotlinx.serialization, type-safe Compose Navigation. Groovy Gradle DSL (`build.gradle`, not `.kts`), version catalog in `gradle/libs.versions.toml`. Min SDK 24, target/compile SDK 36, JVM target 11.

## Commands

```bash
./gradlew assembleDebug                  # build debug APK
./gradlew testDebugUnitTest              # run unit tests
./gradlew testDebugUnitTest --tests "com.anhnn.battu.CreateChartUseCaseTest"   # single test class
./gradlew connectedDebugAndroidTest      # instrumented/Compose UI tests (device required)
./gradlew lint                           # Android lint
```

CI is Jenkins (`Jenkinsfile` → `.anhnn/build.sh`): builds only on `develop`/`testing` branches and posts to Discord webhooks. `main` is the PR target branch.

## Architecture

Clean Architecture, strictly three layers under `app/src/main/java/com/anhnn/battu/`:

- `data/` — `datasource/BaziApiService.kt` (Retrofit), `models/` (DTOs + mappers), `repository/` (implementations)
- `domain/` — pure Kotlin: `models/` (`@Immutable` entities), `repository/` (interfaces), `usecases/`
- `presentation/` — `screens/`, `viewmodels/`, `theme/`, `navigation/NavGraph.kt`, `util/BaziVi.kt` (Vietnamese display names for Chinese Bazi terms)
- `di/` — Hilt modules (`NetworkModule`, `RepositoryModule`, `AppModule`); everything constructor-injected
- `core/Constants.kt` — backend base URL

Unidirectional data flow: ViewModel exposes a single UI-state object via `StateFlow`; UI collects with `collectAsStateWithLifecycle()` and sends events up as lambdas. Errors are caught in the data layer and returned as `Result<T>` (never let non-cancellation exceptions escape; `CancellationException` is rethrown).

Navigation routes are `@Serializable` objects (`HomeRoute`, `ChartRoute`, `LanguageRoute`) in `NavGraph.kt`.

### FOR-BAZI backend contract (critical — see docs/bug.md)

The app talks to a single endpoint, `POST /api/v1/chart`, with exactly two fields:

- `datetime_str`: `"YYYY-MM-DD HH:MM"` (solar birth time)
- `gender`: must be the exact string `"乾造 (Male)"` or `"坤造 (Female)"` — anything else gets a 400/422. The `Gender` enum in `domain/models/BaziChart.kt` owns this mapping; never send `"male"`/`"nam"`.

`BaziApiService` deliberately returns a raw `JsonObject` (not a typed DTO): the repository keeps the `chart` block verbatim in `BaziChart.rawChartJson` because it must be sent back unchanged as `chart_data` to `POST /api/v1/chat/stream` for the future AI-chat feature. Don't "clean this up" into a typed-only response.

`wuxing_power` and `geju` in the response can be null — keep them nullable. `geju` uses Chinese keys (`格局类型`, …), handled via `@SerialName` in the DTOs.

`Constants.BAZI_BASE_URL` is a LAN IP for a locally running backend (use `http://10.0.2.2:8000/` for the emulator); it must be replaced for production builds.

### Language switching

Uses the in-house JitPack library `com.github.anhngocnguyen1034:anhnn-language` (docs in `docs/language.md`). `MainActivity.attachBaseContext` applies the persisted locale via `LanguageDataSource`/`LanguageManager`; `LanguageScreen` saves a new locale and the activity is `recreate()`d. App strings live in `res/values/strings.xml` (English default) and `res/values-vi/strings.xml`.

## Project conventions (from docs/)

The `docs/` folder holds the Anhnn ecosystem rules (`clauderules.md`, `General.md`, `UI_GUIDE.md`, `Testing.md`, `Perfopmance.md`, `Project_structor.md`). Key points:

- **Theming**: wrap UI in the app theme; use `MaterialTheme.colorScheme` tokens, never hardcode hex colors in composables. Element (Wuxing) colors live in `presentation/theme/WuxingColors.kt`. Anhnn signature gradient: `#A1A2FF → #4B4EEE`.
- **Compose**: stateless composables via state hoisting; composable names are nouns; provide unique `key`s in lazy lists; mark UI-facing data classes `@Immutable`/`@Stable`; defer fast-changing state reads with lambdas; at least two `@Preview`s (Light/Dark) per UI component. Modifier ordering: size → clip/background → clickable → padding.
- **Concurrency**: I/O and heavy parsing on `Dispatchers.IO` (done in repositories, not ViewModels).
- **Testing**: use **Fake repositories** (not Mockito/MockK) for ViewModel/UseCase unit tests, `StandardTestDispatcher` for coroutines, `createComposeRule()` for Compose tests; mock backend JSON so tests run offline. Test names describe the scenario: `should_show_error_when_static_service_fails()`.

Comments and docs in this repo are commonly written in Vietnamese; UI copy is bilingual (en/vi).
