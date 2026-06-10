# Informe Tecnico - Laboratorio 10.1 Concurrencia y Programacion Asincrona

## 1. Datos Generales

**Asignatura:** Programacion Movil  
**Laboratorio:** RDA-3 Lab 10.1 - Diagnostico de bloqueos en el hilo principal y optimizacion asincrona  
**Proyecto:** ANRSimulationLab  
**Plataforma:** Android 16, API 36, Kotlin, Jetpack Compose, Java 21  

## 2. Objetivo

Diagnosticar el bloqueo del hilo principal de Android al ejecutar una tarea pesada de forma sincronica y refactorizar la solucion mediante MVVM, `viewModelScope`, `StateFlow` y `Dispatchers.IO` para conservar una interfaz fluida.

## 3. Evidencia de la Version Bloqueante

Insertar captura de Logcat filtrando por `ANR_LAB` despues de presionar el boton **Simular Bloqueo Main Thread**.

Logs esperados:

```text
D/ANR_LAB: Inicio de operacion pesada en Hilo Principal: main
D/ANR_LAB: Procesando bucle de alta densidad en el Main Thread...
D/ANR_LAB: Fin de operacion bloqueante en Hilo Principal
```

Durante esta prueba, el `TextField` deja de responder por aproximadamente 5 segundos porque `Thread.sleep(5000)` se ejecuta directamente en el hilo `main`.

## 4. Evidencia de la Version Optimizada

Insertar captura del emulador mostrando el `CircularProgressIndicator` activo mientras se escribe en el `TextField`.

Insertar captura de Logcat filtrando por `ANR_LAB` despues de presionar **Cargar Datos Pesados (Asincrono y Seguro)**.

Logs esperados:

```text
D/ANR_LAB: Inicio Corrutina en hilo: main
D/ANR_LAB: Procesando calculo intensivo en segundo plano de forma no bloqueante sobre hilo: DefaultDispatcher-worker-*
D/ANR_LAB: Retorno seguro a Dispatchers.Main sobre hilo: main
```

## 5. Evidencia del Reto

Insertar captura donde el usuario escriba `ERROR` en el campo de texto y presione el boton asincrono. La interfaz debe mostrar un mensaje rojo sin cerrar la aplicacion.

Resultado esperado:

```text
Error: Entrada restringida detectada por simulacion de seguridad.
```

## 6. Analisis Ingenieril

`withContext(Dispatchers.IO)` cambia temporalmente el contexto de ejecucion de la corrutina desde el hilo principal hacia un pool de hilos de fondo optimizado para operaciones costosas o de entrada/salida. Gracias al modelo CPS de Kotlin, la corrutina no bloquea el hilo fisico: suspende su continuacion, ejecuta el trabajo pesado fuera de `main` y luego reanuda el flujo en el contexto original. Esto permite que Compose siga procesando recomposiciones, eventos tactiles y animaciones mientras la tarea se completa. En consecuencia, el `CircularProgressIndicator` se anima, el `TextField` acepta escritura y el sistema evita condiciones de ANR, porque el hilo principal nunca queda monopolizado por `Thread.sleep` ni por calculos extensos.

## 7. Conclusiones

La version bloqueante demuestra que una operacion sincronica sobre el hilo principal paraliza toda la interfaz. La version final aplica concurrencia estructurada con `viewModelScope`, expone estado reactivo mediante `StateFlow` y delega el trabajo pesado a `Dispatchers.IO`, manteniendo la UI fluida y controlando errores mediante `SimulationUiState.Error`.
