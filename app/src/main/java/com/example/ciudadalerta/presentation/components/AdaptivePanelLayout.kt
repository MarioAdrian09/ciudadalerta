package com.example.ciudadalerta.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ciudadalerta.presentation.util.WindowWidthSizeClass
import com.example.ciudadalerta.presentation.util.rememberWindowWidthSizeClass
import com.example.ciudadalerta.ui.theme.CiudadalertaTheme

/**
 * Contenedor adaptable que muestra el contenido en un solo panel en pantallas pequeñas (Compact)
 * o en dos paneles (Panel Lateral / Master-Detail) cuando la ventana es de tamaño mediano o grande (Medium / Expanded).
 *
 * @param windowWidthSizeClass Clase de tamaño de ancho de ventana actual.
 * @param listPane Composable para el panel principal / lista de elementos.
 * @param detailPane Composable opcional para el panel secundario de detalle (visible cuando muestraPanel es verdadero).
 *
 * @author Mario Nambo
 */
@Composable
fun AdaptivePanelLayout(
    modifier: Modifier = Modifier,
    windowWidthSizeClass: WindowWidthSizeClass = rememberWindowWidthSizeClass(),
    listPane: @Composable () -> Unit,
    detailPane: (@Composable () -> Unit)? = null
) {
    if (windowWidthSizeClass.muestraPanel && detailPane != null) {
        // Modo dos paneles (Split view / Panel lateral)
        Row(modifier = modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
            ) {
                listPane()
            }
            VerticalDivider(thickness = 1.dp)
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            ) {
                detailPane()
            }
        }
    } else {
        // Modo panel único (Compact)
        Box(modifier = modifier.fillMaxSize()) {
            listPane()
        }
    }
}

@Preview(name = "Teléfono - Compact", device = Devices.PHONE, showBackground = true)
@Composable
fun AdaptivePanelLayoutCompactPreview() {
    CiudadalertaTheme(dynamicColor = false) {
        AdaptivePanelLayout(
            windowWidthSizeClass = WindowWidthSizeClass.COMPACT,
            listPane = {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Lista de elementos (Vista Compacta)")
                    }
                }
            },
            detailPane = {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Detalle (Panel Oculto)")
                    }
                }
            }
        )
    }
}

@Preview(name = "Tableta - Expanded", device = Devices.TABLET, showBackground = true)
@Composable
fun AdaptivePanelLayoutExpandedPreview() {
    CiudadalertaTheme(dynamicColor = false) {
        AdaptivePanelLayout(
            windowWidthSizeClass = WindowWidthSizeClass.EXPANDED,
            listPane = {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Panel 1: Lista de Reportes")
                    }
                }
            },
            detailPane = {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Panel 2: Detalle del Reporte")
                    }
                }
            }
        )
    }
}
