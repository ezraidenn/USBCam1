package com.biostar.camtest.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.biostar.camtest.data.models.GridType
import com.biostar.camtest.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAboutDialog by remember { mutableStateOf(false) }
    var showResolutionInfoDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuración",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = Gray900,
                    navigationIconContentColor = Gray700
                )
            )
        },
        containerColor = Gray50
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Camera Settings Section
            SettingsSection(title = "Cámara") {
                // Resolution Info (not a selector, just info)
                SettingsItem(
                    icon = Icons.Default.Videocam,
                    title = "Resoluciones disponibles",
                    subtitle = "Depende de tu cámara USB",
                    onClick = { showResolutionInfoDialog = true }
                )
                
                Divider(color = Gray200, modifier = Modifier.padding(horizontal = 16.dp))
                
                // Grid Settings - FUNCTIONAL
                GridSettingsSection(
                    defaultGridType = uiState.defaultGridType,
                    gridEnabledByDefault = uiState.gridEnabledByDefault,
                    onGridTypeSelected = { viewModel.setDefaultGridType(it) },
                    onGridEnabledChanged = { viewModel.setGridEnabledByDefault(it) }
                )
            }

            // Storage Settings Section
            SettingsSection(title = "Almacenamiento") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ubicación de guardado",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Gray900
                            )
                            Text(
                                text = uiState.autoSaveLocation,
                                style = MaterialTheme.typography.bodySmall,
                                color = Gray600
                            )
                        }
                    }
                    Text(
                        text = "Las fotos se guardan automáticamente en esta ubicación. Puedes acceder a ellas desde la galería de la app o desde la app Fotos de tu dispositivo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray500,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Coming Soon Section
            SettingsSection(title = "Próximamente") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ComingSoonItem(
                        icon = Icons.Default.VideoLibrary,
                        title = "Grabación de video",
                        description = "Captura video con audio de alta calidad"
                    )
                    ComingSoonItem(
                        icon = Icons.Default.Mic,
                        title = "Configuración de audio",
                        description = "Bitrate y frecuencia de muestreo personalizables"
                    )
                    ComingSoonItem(
                        icon = Icons.Default.FolderOpen,
                        title = "Selector de carpeta",
                        description = "Elige dónde guardar tus capturas"
                    )
                }
            }

            // App Settings Section
            SettingsSection(title = "Aplicación") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Acerca de",
                    subtitle = "Versión 2.0.0",
                    onClick = { showAboutDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Version info
            Text(
                text = "USB CAM Pro v2.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = Gray500,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

    // Dialogs
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "USB CAM Pro",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Versión 2.0.0\n\nAplicación profesional para cámaras USB\n\nCaracterísticas actuales:\n• Captura de fotos HD\n• Grid overlay (3 tipos)\n• Galería integrada\n• Controles profesionales\n• Selector de resolución\n\nPróximamente:\n• Grabación de video\n• Audio personalizable\n• Más opciones de almacenamiento",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    if (showResolutionInfoDialog) {
        AlertDialog(
            onDismissRequest = { showResolutionInfoDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Resoluciones Disponibles",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Las resoluciones disponibles dependen de tu cámara USB.\n\nPara cambiar la resolución:\n1. Conecta tu cámara USB\n2. En la pantalla principal, toca el botón de resolución en la parte superior\n3. Selecciona la resolución deseada\n\nResoluciones comunes:\n• 4K (3840x2160) - Cámaras premium\n• 1080p (1920x1080) - Estándar HD\n• 720p (1280x720) - HD básico\n• 480p (640x480) - Compatibilidad",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showResolutionInfoDialog = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
private fun ComingSoonItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = Gray200
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Gray500,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Gray700
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = AccentBlue.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Próximamente",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentBlue,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Gray500
            )
        }
    }
}

@Composable
private fun GridSettingsSection(
    defaultGridType: GridType,
    gridEnabledByDefault: Boolean,
    onGridTypeSelected: (GridType) -> Unit,
    onGridEnabledChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.GridOn,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Grid por defecto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900
                )
                Text(
                    text = "Activar automáticamente al iniciar",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray600
                )
            }
            Switch(
                checked = gridEnabledByDefault,
                onCheckedChange = onGridEnabledChanged,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = White,
                    checkedTrackColor = AccentBlue,
                    uncheckedThumbColor = White,
                    uncheckedTrackColor = Gray300
                )
            )
        }
        
        if (gridEnabledByDefault) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GridTypeOption(
                    gridType = GridType.RULE_OF_THIRDS,
                    label = "Regla de 3",
                    description = "Composición clásica",
                    selected = defaultGridType == GridType.RULE_OF_THIRDS,
                    onSelected = { onGridTypeSelected(GridType.RULE_OF_THIRDS) }
                )
                GridTypeOption(
                    gridType = GridType.SQUARE,
                    label = "Cuadrícula",
                    description = "Grid uniforme 4x4",
                    selected = defaultGridType == GridType.SQUARE,
                    onSelected = { onGridTypeSelected(GridType.SQUARE) }
                )
                GridTypeOption(
                    gridType = GridType.GOLDEN_RATIO,
                    label = "Proporción Áurea",
                    description = "Composición avanzada",
                    selected = defaultGridType == GridType.GOLDEN_RATIO,
                    onSelected = { onGridTypeSelected(GridType.GOLDEN_RATIO) }
                )
            }
        }
    }
}

@Composable
private fun GridTypeOption(
    gridType: GridType,
    label: String,
    description: String,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onSelected,
                role = Role.RadioButton
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = AccentBlue,
                unselectedColor = Gray400
            )
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Gray900
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Gray600
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = Gray700,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = White,
            shadowElevation = 1.dp
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = AccentBlue.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Gray900
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Gray600
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Gray400,
            modifier = Modifier.size(20.dp)
        )
    }
}
