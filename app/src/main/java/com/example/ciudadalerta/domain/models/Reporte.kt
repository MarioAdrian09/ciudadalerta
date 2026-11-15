package com.example.ciudadalerta.domain.models

/**
 *
 * Modelo de datos temporal para representar un reporte en la interfaz.
 * @author Mario Nambo
 */

data class Reporte (
    var id: String,
    var informacion: String,
    var region: String,
    val descripcion: String,
    val categoria: String,
    val hora: String,
    val requiereAtencionInmediata: Boolean,
    val estatus: String = "Pendiente",
    val estatusSincronizacion: String = "LOCAL",
    val usuarioId: Int
)