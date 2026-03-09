<a href="https://play.google.com/store/apps/details?id=io.github.ifa.glancewidget">
  <p align="center">
    <img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png"
      alt="Get it on Google Play" width="323" height="125" border="10"/>
  </p>
</a>

<h1 align="center">Battery Meter & Widget</h1>

<p align="center">
  A real-time battery monitoring app with a customizable home screen widget — built with modern Android architecture.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" alt="Platform"/>
  <img src="https://img.shields.io/badge/Jetpack_Compose-UI-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/>
  <img src="https://img.shields.io/badge/Glance-Widget-FF6F00?logo=android&logoColor=white" alt="Glance Widget"/>
  <img src="https://img.shields.io/badge/Hilt-DI-FF6F00?logo=android&logoColor=white" alt="Hilt"/>
  <img src="https://img.shields.io/badge/Clean_Architecture-Pattern-blueviolet" alt="Clean Architecture"/>
</p>

---

## Overview

**Battery Meter & Widget** provides real-time battery level monitoring both in-app and on the Android home screen. The widget supports multiple display styles (circular, horizontal, full-width, grid) and updates frequently enough to always reflect the current device state.

<p align="center">
  <video src="https://github-production-user-asset-6210df.s3.amazonaws.com/46745326/368035607-3bea29fe-c25a-40fb-9bb5-9d6f16aebbb7.mp4" width="360"/>
</p>

---

## Features

| Feature | Description |
|---|---|
| **Real-time Battery Monitoring** | Displays live battery level, charging state, and health via a dynamic UI |
| **Home Screen Widget** | Multiple widget layouts powered by Jetpack Glance — circle, horizontal, full-width, and grid |
| **App Usage Stats** | View per-app battery consumption with usage tracking |
| **Widget Customization** | Customize widget appearance, colors, and display preferences from the settings screen |
| **GoPro Integration** | Extended monitoring support for GoPro accessories |
| **Background Updates** | Efficient WorkManager-based background refresh with broadcast receivers for instant events |
| **No Login Required** | Core features are fully available without an account |

---

## Tech Stack

| Layer | Library / Tool |
|---|---|
| **UI** | [Jetpack Compose](https://developer.android.com/jetpack/compose) + Material 3 |
| **Widget** | [Jetpack Glance](https://developer.android.com/develop/ui/compose/glance) |
| **DI** | [Hilt](https://dagger.dev/hilt/) |
| **Navigation** | [Navigation Compose](https://developer.android.com/develop/ui/compose/navigation) (type-safe, `@Serializable` routes) |
| **Local DB** | [Room](https://developer.android.com/training/data-storage/room) |
| **Preferences** | [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) |
| **Background** | [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) + `BroadcastReceiver` |
| **State** | `StateFlow` + `collectAsStateWithLifecycle` |
| **Build** | Gradle Kotlin DSL |

---

## Architecture

This project follows **Clean Architecture** principles organized into distinct layers:

```
app/src/main/java/io/github/ifa/glancewidget/
├── Application.kt              ← @HiltAndroidApp + WorkManager configuration
├── MainActivity.kt             ← @AndroidEntryPoint + RepositoryProvider.Provide{}
├── background/                 ← WorkManager workers (@HiltWorker / @AssistedInject)
├── broadcast/                  ← BroadcastReceivers (battery events, etc.)
├── data/                       ← Repository implementations, DataStore wrappers
├── di/                         ← Hilt modules (common + feature-specific)
├── domain/                     ← Repository interfaces + UseCases + domain models
├── features/                   ← Feature screens (composable routes + ViewModels)
│   ├── main/                   ← Main screen
│   ├── appusage/               ← App usage stats screen
│   ├── widget/                 ← Widget management screen
│   ├── widgetsettings/         ← Widget customization screen
│   ├── settings/               ← App settings screen
│   ├── gopro/                  ← GoPro integration screen
│   └── about/                  ← About screen
├── glance/                     ← Glance widget composables (battery styles)
│   └── battery/                ← BatteryWidget, circular, horizontal, grid variants
├── model/                      ← Shared data models
├── navigation/                 ← AppNavHost, type-safe routes, slide transitions
├── service/                    ← Notification and foreground services
├── ui/                         ← Design system (theme, components, CompositionLocals)
└── utils/                      ← Extension functions, UiStateBuilder
```

### Layer Responsibilities

- **Domain**: Pure Kotlin interfaces (`domain/repository/`) and use-case business logic (`domain/usecase/`). No Android framework dependencies.
- **Data**: `Default*Repository` implementations. Talks to Room DAOs, DataStore, and external APIs.
- **UI**: Jetpack Compose screens backed by `@HiltViewModel`s. UI state exposed via `StateFlow` and consumed with `collectAsStateWithLifecycle()`.
- **DI**: Hilt modules wire everything together. Repositories are registered via multibinding into a `RepositoryProvider` and surfaced through `CompositionLocal`.

### Key Patterns

- **`buildUiState`** — a `ViewModel` extension that combines multiple `StateFlow`s into a single `UiState` using `WhileSubscribed(5_000)`, handling configuration changes gracefully.
- **`RepositoryProvider`** — a Hilt multibinding map of all repositories exposed to the Compose tree as `LocalRepositories`, so composables can call `localXxxRepository()` without Hilt knowledge.
- **Type-safe Navigation** — routes are `@Serializable` data objects/classes; navigation actions live as `NavController` extension functions in `NavigationMethod.kt`.
- **`@HiltWorker` + `@AssistedInject`** — all WorkManager workers obtain dependencies via an `@EntryPoint` interface on `SingletonComponent`.

---

> The aim of this app is to provide an appwidget that shows the battery level of the main device. It uses Glance and a couple of other Jetpack libraries.
>
> For now the main goal is not to offer it on Google Play, but to use it as a tool for finding out how easy / still possible it is to write appwidgets that are updated frequently enough to be useful.

---

## License

Distributed under the Apache 2.0 License. See [`LICENSE`](LICENSE) for more information.
