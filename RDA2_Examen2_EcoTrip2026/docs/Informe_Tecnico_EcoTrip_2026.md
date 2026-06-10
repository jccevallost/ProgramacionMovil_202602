# Informe Tecnico de Ingenieria

## Portada

**Aplicacion:** EcoTrip 2026 - Sistema Resiliente de Planificacion de Rutas e Identidad Visual Adaptativa  
**Asignatura:** Programacion Movil - RDA-2  
**Grupo:** Grupo 5  
**Integrantes:**  
- Integrante 1: [Nombre completo]
- Integrante 2: [Nombre completo]
- Integrante 3: [Nombre completo]

**Repositorio GitHub:** [URL publica del repositorio]  
**Fecha:** 21 de mayo de 2026

## Introduccion

Los dispositivos moviles ejecutan aplicaciones bajo condiciones de alta volatilidad: rotacion de pantalla, cambios de configuracion, cierre del proceso por presion de memoria y reinicio completo de la aplicacion. En ese contexto, el estado de la interfaz no puede depender solo de variables locales de una pantalla, porque la composicion puede destruirse y recrearse en cualquier momento.

EcoTrip 2026 resuelve este problema mediante una arquitectura declarativa con Jetpack Compose, ViewModel, SavedStateHandle y DataStore. La solucion mantiene los datos del formulario de viaje disponibles durante recomposiciones, rotaciones, muerte del proceso y reaperturas posteriores de la app.

## Arquitectura Implementada

La aplicacion se organiza en tres capas principales:

- **Modelo y dominio:** `PreferenciaViaje`, `TipoTransporteEcologico` y `generarRecomendacionRuta`.
- **Persistencia:** `PreferenciaViajeRepository`, construido sobre Preferences DataStore.
- **Estado y UI:** `EcoTripViewModel`, `EcoTripUiState`, pantallas Compose y grafo de navegacion tipado.

Flujo conceptual:

```text
Pantallas Compose
      |
      v
EcoTripViewModel
      |
      +--> Memoria estable: StateFlow<EcoTripUiState>
      |
      +--> Proceso: SavedStateHandle
      |
      +--> Disco: PreferenciaViajeRepository + DataStore
      |
      v
PreferenciaViaje
```

`PreferenciaViaje` representa los datos limpios exigidos por la guia: nombre del viajero, destino, duracion en dias, tipo de transporte ecologico y preferencia por rutas de baja huella de carbono. El `ViewModel` eleva el estado de los campos y expone un `StateFlow` para Compose. `SavedStateHandle` guarda cada campo de entrada para sobrevivir a recreaciones y muerte del proceso. `DataStore` persiste la ultima preferencia valida en disco para mantenerla aun despues de cerrar totalmente la aplicacion.

## Resiliencia Multinivel

**Memoria - ViewModel:** conserva el estado durante recomposiciones y rotaciones normales mientras el proceso sigue vivo.

**Proceso - SavedStateHandle:** almacena los valores del formulario como claves simples. Si Android elimina el proceso y restaura la actividad, el formulario recupera nombre del viajero, destino, dias de duracion, transporte seleccionado y estado del switch.

**Disco - DataStore:** guarda la ultima `PreferenciaViaje` valida al planificar la ruta. Al abrir nuevamente la app, el repositorio emite esos valores y el ViewModel los usa para hidratar el formulario si no existe un estado de proceso previo.

## Navegacion Type-Safe

El grafo usa rutas `@Serializable`:

- `FormularioViajeRoute`
- `ResumenRutaRoute(nombreViajero, destino, diasDuracion, tipoTransporte, soloRutasBajaHuella, ticketCode)`

La navegacion fuertemente tipada reduce errores porque el compilador valida la forma de cada destino y sus parametros. En el enfoque antiguo con Strings o Bundles manuales, un typo en una clave, un tipo incorrecto o un argumento faltante podia provocar errores en tiempo de ejecucion. Con rutas serializables, los parametros forman parte del contrato del destino y se construyen como objetos Kotlin, lo que elimina claves magicas y mejora el mantenimiento. En esta app, la pantalla de resumen exige parametros estrictos: nombre, destino, dias, transporte, bandera de baja huella y codigo de boleto turistico.

La higiene del Back Stack se aplica con `popUpTo<FormularioViajeRoute>` y `launchSingleTop`, evitando duplicados innecesarios al navegar entre formulario y resumen.

## Estetica Material 3 y Color Dinamico

La app usa `MaterialTheme.colorScheme` y roles semanticos como `primaryContainer`, `secondaryContainer`, `tertiaryContainer`, `onPrimaryContainer` y `onSurfaceVariant`. En Android 12+ se activa `dynamicLightColorScheme` o `dynamicDarkColorScheme`, por lo que la interfaz se acopla al color dinamico del sistema y permite evidenciar Material You al cambiar el wallpaper del emulador.

## Evidencias Graficas

Agregar capturas completas del Emulador API 36:

1. Formulario de viaje con datos validos en modo vertical.
2. Prueba de rotacion o muerte del proceso, demostrando que no se pierden textos ni estados.
3. Pantalla de resumen con color dinamico despues de modificar el wallpaper del emulador.

## Pruebas de Estabilidad

Pruebas recomendadas:

- Ingresar letras en campos numericos: la app filtra caracteres no numericos y valida con `toIntOrNull()`.
- Enviar formulario incompleto: no navega y muestra campos resaltados.
- Rotar pantalla: el formulario mantiene valores por ViewModel/SavedStateHandle.
- Activar "Don't keep activities" o simular muerte de proceso: los campos se restauran por SavedStateHandle.
- Cerrar y abrir app despues de planificar: DataStore repone la ultima preferencia valida.

## Conclusiones

EcoTrip 2026 implementa una arquitectura resiliente, declarativa y alineada con Material Design 3. La separacion entre modelo, repositorio, ViewModel y pantallas reduce acoplamiento y facilita pruebas. El uso combinado de ViewModel, SavedStateHandle y DataStore protege el estado ante los escenarios moviles mas comunes. La navegacion tipada mejora la seguridad del flujo y reduce errores de integracion entre pantallas.

## Recomendaciones

- Mantener nuevas pantallas dentro del mismo patron UDF: estado elevado en ViewModel y eventos desde Compose.
- Evitar Bundles manuales y rutas por Strings para no reintroducir claves fragiles.
- Ampliar pruebas instrumentadas si se agregan mas pantallas o reglas de negocio.
- Subir el proyecto a GitHub con commits descriptivos antes de generar el ZIP final.

## Anexo: Codigo Fuente Estructurado

Archivos principales:

- `app/src/main/java/com/example/programacionmovil/data/PreferenciaViaje.kt`
- `app/src/main/java/com/example/programacionmovil/data/PreferenciaViajeRepository.kt`
- `app/src/main/java/com/example/programacionmovil/ui/EcoTripViewModel.kt`
- `app/src/main/java/com/example/programacionmovil/ui/EcoTripUiState.kt`
- `app/src/main/java/com/example/programacionmovil/ui/EcoTripApp.kt`
- `app/src/main/java/com/example/programacionmovil/ui/EcoTripScreens.kt`
- `app/src/main/java/com/example/programacionmovil/navigation/EcoTripRoutes.kt`
- `app/src/main/java/com/example/programacionmovil/MainActivity.kt`
