# EcoTrip 2026 - RDA-2

Aplicacion Android nativa construida con Kotlin, Jetpack Compose, Material 3, Navigation Compose tipado, SavedStateHandle y DataStore.

## Entorno

- Java/JBR 21
- `compileSdk = 36`
- `targetSdk = 36`
- UI 100% declarativa con Jetpack Compose
- Dependencias centralizadas en `gradle/libs.versions.toml`

## Verificacion local

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```

APK debug generado en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Flujo Git sugerido

Configure primero el usuario real de cada integrante o el usuario del repositorio del grupo:

```powershell
git config user.name "Nombre Integrante"
git config user.email "correo@ejemplo.com"
```

Commits recomendados para cumplir la rubrica:

```powershell
git add gradle/libs.versions.toml build.gradle.kts app/build.gradle.kts app/src/main/java/com/example/programacionmovil/data app/src/main/java/com/example/programacionmovil/navigation
git commit -m "feat: data models and routes"

git add app/src/main/java/com/example/programacionmovil/ui/EcoTripViewModel.kt app/src/main/java/com/example/programacionmovil/ui/EcoTripUiState.kt
git commit -m "feat: viewmodel and datastore implementation"

git add app/src/main/java/com/example/programacionmovil/MainActivity.kt app/src/main/java/com/example/programacionmovil/ui/EcoTripApp.kt app/src/main/java/com/example/programacionmovil/ui/EcoTripScreens.kt app/src/main/java/com/example/programacionmovil/ui/theme app/src/main/res/values/strings.xml
git commit -m "feat: compose ui layout with material design 3"

git add app/src/test/java/com/example/programacionmovil/EcoTripStateTest.kt docs/Informe_Tecnico_EcoTrip_2026.md README.md gradle.properties
git commit -m "feat: type-safe navigation and testing"
```

## Capturas requeridas

Use un emulador API 36 y capture:

1. Formulario de viaje con datos validos en vertical.
2. Rotacion o muerte de proceso sin perdida de campos.
3. Resumen de ruta despues de cambiar el wallpaper para evidenciar Material You.
