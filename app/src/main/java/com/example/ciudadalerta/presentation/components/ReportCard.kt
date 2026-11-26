package com.example.ciudadalerta.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ciudadalerta.data.local.entity.EstadoSync
import com.example.ciudadalerta.presentation.screens.ciudadano.ReporteDummy
import com.example.ciudadalerta.ui.theme.CiudadalertaTheme

/**
 * Componente visual reutilizable que representa un reporte individual.
 * @author Mario Nambo
 */
@Composable
fun ReportCard(reporte: ReporteDummy) {
    val colorEstatus = when (reporte.estatus) {
        "Pendiente" -> Color(0xFFE53935)
        "En Revisión" -> Color(0xFFFDD835)
        "Resuelto" -> Color(0xFF43A047)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reporte.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = colorEstatus.copy(alpha = 0.2f),
                    contentColor = colorEstatus
                ) {
                    Text(
                        text = reporte.estatus,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = reporte.descripcion,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Fecha: ${reporte.fecha}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportCardPreview() {
    CiudadalertaTheme(dynamicColor = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReportCard(
                reporte = ReporteDummy(
                    id = "1",
                    titulo = "Fuga de agua en Av. Principal",
                    descripcion = "Se observa una fuga constante de agua potable desde hace 2 días.",
                    estatus = "Pendiente",
                    fecha = "24 Oct 2023",
                    sincronizacion = EstadoSync.ENVIADO
                )
            )
            ReportCard(
                reporte = ReporteDummy(
                    id = "2",
                    titulo = "Bache peligroso",
                    descripcion = "Bache profundo en el carril derecho cerca del semáforo.",
                    estatus = "En Revisión",
                    fecha = "23 Oct 2023",
                    sincronizacion = EstadoSync.LOCAL
                )
            )
            ReportCard(
                reporte = ReporteDummy(
                    id = "3",
                    titulo = "Luminaria apagada",
                    descripcion = "Foco fundido en la esquina del parque central.",
                    estatus = "Resuelto",
                    fecha = "20 Oct 2023",
                    sincronizacion = EstadoSync.ENVIADO
                )
            )
        }
    }
}