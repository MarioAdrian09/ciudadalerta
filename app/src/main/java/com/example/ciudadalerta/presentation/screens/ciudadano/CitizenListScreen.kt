package com.example.ciudadalerta.presentation.screens.ciudadano

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ciudadalerta.data.local.entity.EstadoSync
import com.example.ciudadalerta.domain.models.EstadoRed
import com.example.ciudadalerta.presentation.components.AdaptivePanelLayout
import com.example.ciudadalerta.presentation.components.SyncBanner
import com.example.ciudadalerta.presentation.components.SyncChip
import com.example.ciudadalerta.presentation.navigation.Rutas
import com.example.ciudadalerta.presentation.util.WindowWidthSizeClass
import com.example.ciudadalerta.presentation.util.rememberWindowWidthSizeClass
import com.example.ciudadalerta.ui.theme.CiudadalertaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Pantalla que muestra el historial de reportes creados por el ciudadano.
 * Se adapta a pantallas grandes (Medium / Expanded) mediante panel lateral (AdaptivePanelLayout).
 *
 * @author Mario Nambo
 */
@Composable
fun CitizenListScreen(
    navController: NavController,
    viewModel: CitizenListViewModel,
    onCerrarSesion: () -> Unit = {}
) {
    val nombreUsuario by viewModel.nombreUsuario.collectAsState()
    val listaReportes by viewModel.reportes.collectAsState()
    val estadoRed by viewModel.estadoRed.collectAsState()

    CitizenListContent(
        nombreUsuario = nombreUsuario,
        listaReportes = listaReportes,
        estadoRed = estadoRed,
        onCerrarSesion = onCerrarSesion,
        onCrearReporte = { navController.navigate(Rutas.CreateReport.ruta) },
        onSincronizar = viewModel::sincronizarAhora
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenListContent(
    nombreUsuario: String,
    listaReportes: List<ReporteDummy>,
    estadoRed: EstadoRed,
    onCerrarSesion: () -> Unit = {},
    onCrearReporte: () -> Unit = {},
    onSincronizar: () -> Unit = {},
    windowWidthSizeClass: WindowWidthSizeClass = rememberWindowWidthSizeClass()
) {
    var reporteIdSeleccionado by remember { mutableStateOf<String?>(null) }
    val reporteSeleccionado = listaReportes.find { it.id == reporteIdSeleccionado } ?: listaReportes.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Usuario"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = nombreUsuario,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text("Mis Reportes", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCrearReporte,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Crear nuevo reporte")
            }
        }
    ) { paddingValues ->
        AdaptivePanelLayout(
            modifier = Modifier.padding(paddingValues),
            windowWidthSizeClass = windowWidthSizeClass,
            listPane = {
                Column(modifier = Modifier.fillMaxSize()) {
                    SyncBanner(
                        estado = estadoRed,
                        pendientes = listaReportes.count { it.sincronizacion != EstadoSync.ENVIADO },
                        onSincronizar = onSincronizar
                    )
                    if (listaReportes.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Aún no tienes reportes. Toca + para crear uno.")
                        }
                    } else if (windowWidthSizeClass.muestraPanel) {
                        // En pantallas medianas/grandes usamos cuadrícula adaptable
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 280.dp),
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            items(listaReportes, key = { it.id }) { reporte ->
                                ReporteItem(
                                    reporte = reporte,
                                    esSeleccionado = (reporte.id == reporteSeleccionado?.id),
                                    onClick = { reporteIdSeleccionado = reporte.id }
                                )
                            }
                        }
                    } else {
                        // En pantallas pequeñas usamos lista vertical estándar
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            items(listaReportes, key = { it.id }) { reporte ->
                                ReporteItem(
                                    reporte = reporte,
                                    esSeleccionado = false,
                                    onClick = null
                                )
                            }
                        }
                    }
                }
            },
            detailPane = {
                if (reporteSeleccionado != null) {
                    CitizenReportDetailPanel(reporte = reporteSeleccionado)
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Selecciona un reporte de tu lista para ver su detalle aquí",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        )
    }
}

/**
 * Componente de detalle para la vista de panel lateral en el modo del ciudadano.
 * Muestra todos los campos capturados: foto de evidencia, categoría, descripción, urgencia, ubicación GPS y fecha/hora.
 */
@Composable
fun CitizenReportDetailPanel(reporte: ReporteDummy) {
    val context = LocalContext.current

    val colorEstatus = when (reporte.estatus) {
        "Pendiente" -> Color(0xFFE53935)
        "En Revisión" -> Color(0xFFFDD835)
        "Resuelto" -> Color(0xFF43A047)
        else -> Color.Gray
    }

    // Decodifica la foto de forma asíncrona si existe fotoPath
    val fotoBitmap by produceState<ImageBitmap?>(initialValue = null, reporte.fotoPath) {
        value = reporte.fotoPath?.let { ruta ->
            withContext(Dispatchers.IO) {
                try {
                    BitmapFactory.decodeFile(ruta, BitmapFactory.Options().apply { inSampleSize = 4 })
                        ?.asImageBitmap()
                } catch (_: Exception) {
                    null
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Foto de Evidencia
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            val bitmap = fotoBitmap
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Foto del reporte",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = if (reporte.fotoPath == null) "Sin foto adjunta" else "Cargando imagen...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        }

        // Título y Estatus
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = reporte.titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
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

        SyncChip(sincronizacion = reporte.sincronizacion)

        if (reporte.urgente) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFE53935)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Peligro inminente: requiere atención inmediata",
                    color = Color(0xFFE53935),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        HorizontalDivider()

        // Datos del Reporte
        DatoFilaDetalle("Descripción", reporte.descripcion)
        if (reporte.categoria.isNotBlank()) {
            DatoFilaDetalle("Categoría", reporte.categoria)
        }
        DatoFilaDetalle("Fecha de registro", reporte.fecha)
        if (reporte.hora.isNotBlank()) {
            DatoFilaDetalle("Hora aproximada del incidente", reporte.hora)
        }

        // Ubicación GPS
        val latitud = reporte.latitud
        val longitud = reporte.longitud
        if (latitud != null && longitud != null) {
            DatoFilaDetalle("Ubicación GPS", "%.5f, %.5f".format(latitud, longitud))
            OutlinedButton(
                onClick = { abrirEnMaps(context, latitud, longitud, reporte.titulo) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Abrir ubicación en Maps")
            }
        } else {
            DatoFilaDetalle("Ubicación GPS", "Sin ubicación registrada")
        }
    }
}

@Composable
private fun DatoFilaDetalle(etiqueta: String, valor: String) {
    Column {
        Text(
            text = etiqueta,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Componente visual (Tarjeta) que representa un elemento individual dentro de la lista de reportes.
 * @author Mario Nambo
 */
@Composable
fun ReporteItem(
    reporte: ReporteDummy,
    esSeleccionado: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val colorEstatus = when (reporte.estatus) {
        "Pendiente" -> Color(0xFFE53935)
        "En Revisión" -> Color(0xFFFDD835)
        "Resuelto" -> Color(0xFF43A047)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        border = if (esSeleccionado) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
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
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SyncChip(reporte.sincronizacion)
                Text(
                    text = reporte.fecha,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun abrirEnMaps(
    context: Context,
    latitud: Double,
    longitud: Double,
    titulo: String
) {
    val uri = Uri.parse("geo:0,0?q=$latitud,$longitud(${Uri.encode(titulo)})")
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (_: Exception) {}
}

@Preview(showBackground = true)
@Composable
fun ReporteItemPreview() {
    CiudadalertaTheme(dynamicColor = false) {
        Column(modifier = Modifier.padding(16.dp)) {
            ReporteItem(
                reporte = ReporteDummy(
                    id = "1",
                    titulo = "Fuga de Agua",
                    descripcion = "Se detectó fuga de agua potable en la calle 5 de Mayo.",
                    estatus = "Pendiente",
                    fecha = "24 Oct 2023",
                    sincronizacion = EstadoSync.LOCAL,
                    categoria = "Fuga de Agua",
                    hora = "14:30",
                    urgente = true
                )
            )
        }
    }
}

@Preview(name = "Lista Ciudadano - Teléfono", device = Devices.PHONE, showBackground = true)
@Composable
fun CitizenListContentPreview() {
    CiudadalertaTheme(dynamicColor = false) {
        CitizenListContent(
            nombreUsuario = "Juan Pérez",
            listaReportes = listOf(
                ReporteDummy("1", "Fuga de Agua", "Fuga importante en la calle principal.", "Pendiente", "24 Oct 2023", EstadoSync.LOCAL, "Fuga de Agua", "10:30", true),
                ReporteDummy("2", "Bache en Avenida", "Bache en el carril central.", "En Revisión", "23 Oct 2023", EstadoSync.ENVIADO, "Bache", "12:15", false),
                ReporteDummy("3", "Falta de Alumbrado", "Luminaria sin funcionar.", "Resuelto", "20 Oct 2023", EstadoSync.ENVIADO, "Alumbrado", "20:00", false)
            ),
            estadoRed = EstadoRed.EN_LINEA,
            windowWidthSizeClass = WindowWidthSizeClass.COMPACT
        )
    }
}

@Preview(name = "Lista Ciudadano - Tablet (Expanded Two Pane)", device = Devices.TABLET, showBackground = true)
@Composable
fun CitizenListExpandedPreview() {
    CiudadalertaTheme(dynamicColor = false) {
        CitizenListContent(
            nombreUsuario = "Juan Pérez",
            listaReportes = listOf(
                ReporteDummy("1", "Fuga de Agua", "Fuga importante en la calle principal.", "Pendiente", "24 Oct 2023", EstadoSync.LOCAL, "Fuga de Agua", "10:30", true),
                ReporteDummy("2", "Bache en Avenida", "Bache en el carril central.", "En Revisión", "23 Oct 2023", EstadoSync.ENVIADO, "Bache", "12:15", false),
                ReporteDummy("3", "Falta de Alumbrado", "Luminaria sin funcionar.", "Resuelto", "20 Oct 2023", EstadoSync.ENVIADO, "Alumbrado", "20:00", false)
            ),
            estadoRed = EstadoRed.EN_LINEA,
            windowWidthSizeClass = WindowWidthSizeClass.EXPANDED
        )
    }
}
