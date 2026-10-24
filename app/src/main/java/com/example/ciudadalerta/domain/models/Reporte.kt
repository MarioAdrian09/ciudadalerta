package com.example.ciudadalerta.domain.models

/**
 *
 * Modelo de datos temporal para representar un reporte en la interfaz.
 * Más adelante esto será reemplazado por la entidad real del dominio y base de datos.
 * @author Mario Nambo
 */

data class Reporte (
    val id: String,
    val titulo: String,
    val descripcion: String,
    val estatus: String,
    val fecha: String
)