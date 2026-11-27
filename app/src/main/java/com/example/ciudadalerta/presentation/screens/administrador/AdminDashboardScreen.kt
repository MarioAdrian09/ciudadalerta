package com.example.ciudadalerta.presentation.screens.administrador

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ciudadalerta.data.local.entity.EstadoSync
import com.example.ciudadalerta.di.AppContainer
import com.example.ciudadalerta.presentation.components.AdaptivePanelLayout
import com.example.ciudadalerta.presentation.components.SyncBanner
import com.example.ciudadalerta.presentation.components.SyncChip
import com.example.ciudadalerta.presentation.navigation.Rutas
import com.example.ciudadalerta.presentation.util.WindowWidthSizeClass
import com.example.ciudadalerta.presentation.util.rememberWindowWidthSizeClass
import com.example.ciudadalerta.ui.theme.CiudadalertaTheme

/**
 * Panel del administrador: lista de todos los reportes con filtro por estatus.
 * Se adapta a pantallas grandes (Medium / Expanded) mediante vista dividida / panel lateral (AdaptivePanelLayout).
 *
 * @author Mario Nambo
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminDashboardViewModel,
    onCerrarSesion: () -> Unit,
    container: AppContainer? = null
) {
    val state by viewModel.uiState.collectAsState()
    val windowWidthSizeClass = rememberWindowWidthSizeClass()

    var reporteIdSeleccionado by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración", fontWeight = FontWeight.Bold) },
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
        }
    ) { paddingValues ->
        AdaptivePanelLayout(
            modifier = Modifier.padding(paddingValues),
            windowWidthSizeClass = windowWidthSizeClass,
            listPane = {
                AdminDashboardListContent(
                    state = state,
                    onFiltroChanged = viewModel::onFiltroChanged,
                    onSincronizar = viewModel::sincronizarAhora,
                    onReporteSelected = { reportId ->
                        if (windowWidthSizeClass.muestraPanel) {
                            reporteIdSeleccionado = reportId
                        } else {
                            navController.navigate(Rutas.ReportDetail.createRoute(reportId))
                        }
                    }
                )
            },
            detailPane = {
                val selectedId = reporteIdSeleccionado
                if (selectedId != null && container != null) {
                    val detailViewModel: ReportDetailViewModel = viewModel(
                        key = "admin_detail_$selectedId",
                        factory = ReportDetailViewModelFactory(container.reporteRepository, selectedId)
                    )
                    val detailState by detailViewModel.uiState.collectAsState()
                    val reporteUi = detailState.reporte

                    if (detailState.cargando) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (reporteUi != null) {
                        DetalleContenido(
                            reporte = reporteUi,
                            estatusSeleccionado = detailState.estatusSeleccionado,
                            guardando = detailState.guardando,
                            onEstatusSeleccionado = detailViewModel::onEstatusSeleccionado,
                            onActualizar = detailViewModel::actualizarEstatus,
                            onSinAppDeMapas = { detailViewModel.mostrarMensaje("No hay una app de mapas instalada") },
                            fotoRuta = detailState.fotoRuta,
                            fotoCargando = detailState.fotoCargando,
                            fotoError = detailState.fotoError,
                            onReintentarFoto = detailViewModel::reintentarFoto
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Este reporte ya no está disponible.")
                        }
                    }
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
                                text = "Selecciona un reporte para ver sus detalles en este panel",
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

@Composable
fun AdminDashboardListContent(
    state: AdminDashboardUiState,
    onFiltroChanged: (String) -> Unit,
    onSincronizar: () -> Unit,
    onReporteSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SyncBanner(
            estado = state.estadoRed,
            pendientes = state.pendientes,
            onSincronizar = onSincronizar
        )
        // Filtros por estatus
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtrosEstatus) { filtro ->
                val cantidad =
                    if (filtro == FILTRO_TODOS) state.total else state.conteos[filtro] ?: 0
                FilterChip(
                    selected = state.filtro == filtro,
                    onClick = { onFiltroChanged(filtro) },
                    label = { Text("$filtro ($cantidad)") }
                )
            }
        }

        if (state.reportes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(if (state.cargando) "Cargando..." else "No hay reportes en esta categoría.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(state.reportes, key = { it.id }) { reporte ->
                    AdminReporteItem(
                        reporte = reporte,
                        onClick = { onReporteSelected(reporte.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminReporteItem(reporte: AdminReporteUi, onClick: () -> Unit) {
    val colorEstatus = when (reporte.estatus) {
        "Pendiente" -> Color(0xFFE53935)
        "En Revisión" -> Color(0xFFFDD835)
        "Resuelto" -> Color(0xFF43A047)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reporte.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
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
                Text(
                    text = "Por: ${reporte.nombreUsuario} · ${reporte.categoria} · ${reporte.fecha}",
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                if (reporte.urgente) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Atención inmediata",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            if (reporte.sincronizacion != EstadoSync.ENVIADO) {
                Spacer(modifier = Modifier.height(4.dp))
                SyncChip(reporte.sincronizacion)
            }
        }
    }
}

@Preview(name = "Admin Dashboard - Teléfono (Compact)", device = Devices.PHONE, showBackground = true)
@Composable
fun AdminDashboardCompactPreview() {
    CiudadalertaTheme(dynamicColor = false) {
        Surface {
            AdminDashboardListContent(
                state = AdminDashboardUiState(
                    reportes = listOf(
                        AdminReporteUi(
                            id = "1",
                            titulo = "Fuga de agua",
                            descripcion = "Reporte de fuga en banqueta principal",
                            estatus = "Pendiente",
                            categoria = "Fuga de Agua",
                            nombreUsuario = "Carlos M.",
                            fecha = "24 Oct",
                            urgente = true,
                            sincronizacion = EstadoSync.ENVIADO
                        )
                    )
                ),
                onFiltroChanged = {},
                onSincronizar = {},
                onReporteSelected = {}
            )
        }
    }
}

@Preview(name = "Admin Dashboard - Tablet (Expanded Two Pane)", device = Devices.TABLET, showBackground = true)
@Composable
fun AdminDashboardExpandedPreview() {
    CiudadalertaTheme(dynamicColor = false) {
        Surface {
            AdaptivePanelLayout(
                windowWidthSizeClass = WindowWidthSizeClass.EXPANDED,
                listPane = {
                    AdminDashboardListContent(
                        state = AdminDashboardUiState(
                            reportes = listOf(
                                AdminReporteUi(
                                    id = "1",
                                    titulo = "Fuga de agua",
                                    descripcion = "Reporte de fuga en banqueta principal",
                                    estatus = "Pendiente",
                                    categoria = "Fuga de Agua",
                                    nombreUsuario = "Carlos M.",
                                    fecha = "24 Oct",
                                    urgente = true,
                                    sincronizacion = EstadoSync.ENVIADO
                                )
                            )
                        ),
                        onFiltroChanged = {},
                        onSincronizar = {},
                        onReporteSelected = {}
                    )
                },
                detailPane = {
                    DetalleContenido(
                        reporte = ReportDetailUi(
                            id = "1",
                            titulo = "Fuga de agua",
                            descripcion = "Reporte de fuga en banqueta principal",
                            categoria = "Fuga de Agua",
                            estatus = "Pendiente",
                            fecha = "24 Oct 2023",
                            horaIncidente = "14:30",
                            nombreUsuario = "Carlos M.",
                            urgente = true,
                            fotoPath = null,
                            latitud = 19.4326,
                            longitud = -99.1332,
                            sincronizacion = EstadoSync.ENVIADO
                        ),
                        estatusSeleccionado = "Pendiente",
                        guardando = false,
                        onEstatusSeleccionado = {},
                        onActualizar = {},
                        onSinAppDeMapas = {},
                        fotoRuta = null,
                        fotoCargando = false,
                        fotoError = null,
                        onReintentarFoto = {}
                    )
                }
            )
        }
    }
}
