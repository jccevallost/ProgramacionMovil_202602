package com.example.programacionmovil

fun main() {
    // 1. Definición de variables de producto
    val productId: Int = 101
    val productName: String? = null
    var productDescription: String? = null
    var stockQuantity: Int = 25
    val unitPrice: Double = 599.99

    println("--- Datos Iniciales Cargados ---")

    // 2. Lógica de Impuestos (Declaramos TAX_RATE una sola vez)
    val TAX_RATE = 0.15
    val totalPriceWithTax = unitPrice * (1 + TAX_RATE)

    // 3. Uso del Operador Elvis (?:)
    val descriptionToShow = productDescription ?: "Sin descripción disponible"
    val nameToShow = productName ?: "Sin nombre disponible"

    println("Producto: $nameToShow")
    println("Descripción: $descriptionToShow")

    // 4. Simulación de Venta
    val itemsSold = 5
    stockQuantity -= itemsSold

    // 5. Generación de Reporte
    val report = """
     --- REPORTE DE INVENTARIO ---
     ID: $productId | Nombre: $nameToShow
     Precio Final: $${String.format("%.2f", totalPriceWithTax)}
     Stock Actual: $stockQuantity
     Estado: ${if (stockQuantity > 10) "Suficiente" else "Crítico"}
     -----------------------------
     """.trimIndent()
    println(report)

    // --- DESAFÍO EXTRA: VALIDACIÓN DINÁMICA ---

    // Prueba 1: "650.50" | Prueba 2: "No quiero pagar"
    val inputUsuario: String? = "No quiero pagar"

    // Conversión Segura (toDoubleOrNull)
    val precioConvertido = inputUsuario?.toDoubleOrNull() ?: 0.0
    val nuevoTotalConIva = precioConvertido * (1 + TAX_RATE)

    println("\n--- Validación Dinámica ---")
    println("Input recibido: $inputUsuario")
    println("Precio procesado: $precioConvertido")
    println("Total con impuesto: $nuevoTotalConIva")
}