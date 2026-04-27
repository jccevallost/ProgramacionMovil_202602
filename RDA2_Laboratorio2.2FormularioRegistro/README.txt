# Laboratorio 2.2: Formulario Ultra-Resiliente (API 36)

## Diferencia entre SavedStateHandle y DataStore
* **SavedStateHandle (Email)**: Persistencia de nivel de proceso. [cite_start]Sobrevive a la "Muerte del Proceso" y cambios de orientación (rotación), pero los datos se pierden si el usuario cierra la app manualmente[cite: 7, 37, 44].
* **DataStore (Nombre)**: Persistencia de nivel de disco. [cite_start]Los datos son "eternos" y sobreviven incluso si se cierra la aplicación por completo o se reinicia el dispositivo, ya que se escriben físicamente en el almacenamiento[cite: 7, 24, 93].

## Objetivos Cumplidos
* [cite_start]Implementación de **Predictive Back** para interceptar la navegación predictiva de Android 16[cite: 8, 56, 64].
* [cite_start]Aplicación del patrón **Unidirectional Data Flow (UDF)** en Jetpack Compose[cite: 9].
* [cite_start]Persistencia multinivel integrada con **MVVM**[cite: 5].
