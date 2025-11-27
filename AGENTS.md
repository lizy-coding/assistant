# Repository Guidelines

## Project Structure & Module Organization
- Root Gradle uses version catalogs in `gradle/libs.versions.toml`; shared rules live in `build.gradle`.
- `app/` is the entry module (`com.example.assistant`), wiring together feature modules with Compose and ViewBinding. Code sits in `app/src/main/java`, resources in `app/src/main/res`.
- Feature libraries: `biometric_auth/`, `speech_recognition/`, `image_analysis/`, `push_notification/`, `battery_monitor/`. Each mirrors the usual `src/main/java|res` layout and exposes UI/logic consumed by `app`.
- Tests follow module boundaries: JVM tests under `src/test/java`, instrumented tests under `src/androidTest/java`.

## Build, Test, and Development Commands
- Use the wrapper from the repo root: `./gradlew clean assembleDebug` builds all modules and produces a debug APK.
- Install and launch on a connected device/emulator: `./gradlew :app:installDebug` (ensure an emulator or device is running).
- JVM unit tests: `./gradlew test`. Instrumented tests: `./gradlew connectedAndroidTest` (requires a device/emulator).
- Lint and static checks: `./gradlew lint` or module-scoped `./gradlew :speech_recognition:lint`.
- Module-only builds are allowed, e.g., `./gradlew :image_analysis:assembleDebug` for faster iteration.

## Coding Style & Naming Conventions
- Kotlin-first codebase targeting Java 17; prefer idiomatic Kotlin (immutability, data classes, extension functions). Indent with 4 spaces.
- Compose screens live alongside traditional Views; name Composables in `PascalCase` with clear intent (`BatteryDashboard()`). ViewModels and controllers end with `*ViewModel`/`*Controller`.
- Resource names use lowercase snake_case (`ic_microphone.xml`, `activity_main.xml`). Keep package paths feature-scoped (`...feature.speech`).
- Dependency versions come from the catalog; add new libraries through `gradle/libs.versions.toml` and reference via `libs.*`.

## Testing Guidelines
- Default stacks: JUnit4 for JVM tests, AndroidX JUnit + Espresso for instrumented UI tests. Compose UI tests should live in `androidTest` and mirror package names.
- Name tests with intent (`SpeechRecognizerTest`, `BatteryStatusRepositoryTest`); prefer given/when/then structure inside.
- Run `./gradlew test` before pushing; run `./gradlew connectedAndroidTest` when changes touch UI, permissions, or device services.

## Commit & Pull Request Guidelines
- Follow the existing history with concise, prefixed subjects (`feat: ...`, `fix: ...`, `chore: ...`, `init:`). Use present tense and describe scope (module and feature touched).
- PRs should state what changed, why, and how to verify. Link issues/tasks, list affected modules, and attach screenshots or recordings for UI-facing updates.
- Include the commands you ran (`./gradlew test`, `./gradlew connectedAndroidTest`, lint) so reviewers can reproduce quickly.

## Security & Configuration
- Do not commit secrets or device-specific configs. Keep SDK paths in `local.properties`; store API keys in `gradle.properties` or runtime config, not in source.
- If adding new services (e.g., push providers, OCR), gate credentials via build configs and document required entries without checking in private files.
