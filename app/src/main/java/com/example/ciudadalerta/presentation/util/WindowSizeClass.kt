package com.example.ciudadalerta.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Representa la clasificación del ancho de pantalla (Window Width Size Class)
 * recomendada para layouts adaptables en Android / Material 3.
 *
 * - COMPACT: Pantallas pequeñas / teléfonos en modo vertical (< 600 dp)
 * - MEDIUM: Teléfonos en horizontal, plegables o tabletas en vertical (600 dp a 839 dp)
 * - EXPANDED: Tabletas en horizontal, pantallas de escritorio (>= 840 dp)
 *
 * @author Mario Nambo
 */
enum class WindowWidthSizeClass {
    COMPACT,
    MEDIUM,
    EXPANDED;

    val isCompact: Boolean get() = this == COMPACT
    val isMedium: Boolean get() = this == MEDIUM
    val isExpanded: Boolean get() = this == EXPANDED

    /**
     * Determina si el tamaño de pantalla requiere o soporta un panel lateral / dos paneles.
     */
    val muestraPanel: Boolean get() = this == MEDIUM || this == EXPANDED

    companion object {
        fun calculateFromWidth(widthDp: Dp): WindowWidthSizeClass = when {
            widthDp < 600.dp -> COMPACT
            widthDp < 840.dp -> MEDIUM
            else -> EXPANDED
        }
    }
}

/**
 * Composable que obtiene y recuerda la clase de tamaño de ancho de ventana actual (WindowWidthSizeClass).
 */
@Composable
fun rememberWindowWidthSizeClass(): WindowWidthSizeClass {
    val configuration = LocalConfiguration.current
    return WindowWidthSizeClass.calculateFromWidth(configuration.screenWidthDp.dp)
}
