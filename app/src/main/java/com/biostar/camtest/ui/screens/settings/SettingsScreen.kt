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
                // Video Quality
                VideoQualitySelector(
                    selectedQuality = uiState.videoQuality,
                    onQualitySelected = { viewModel.setVideoQuality(it) }
                )
                
                Divider(color = Gray200, modifier = Modifier.padding(horizontal = 16.dp))
                
                // Grid Settings
                GridSettingsSection(
                    defaultGridType = uiState.defaultGridType,
                    gridEnabledByDefault = uiState.gridEnabledByDefault,
                    onGridTypeSelected = { viewModel.setDefaultGridType(it) },
                    onGridEnabledChanged = { viewModel.setGridEnabledByDefault(it) }
                )
            }

            // Audio Settings Section
            SettingsSection(title = "Audio") {
                AudioBitrateSelector(
                    selectedBitrate = uiState.audioBitrate,
                    onBitrateSelected = { viewModel.setAudioBitrate(it) }
                )
                
                Divider(color = Gray200, modifier = Modifier.padding(horizontal = 16.dp))
                
                AudioSampleRateSelector(
                    selectedSampleRate = uiState.audioSampleRate,
                    onSampleRateSelected = { viewModel.setAudioSampleRate(it) }
                )
            }

            // Storage Settings Section
            SettingsSection(title = "Almacenamiento") {
                SettingsItem(
                    icon = Icons.Default.Folder,
                    title = "Ubicación de guardado",
                    subtitle = uiState.autoSaveLocation,
                    onClick = { /* TODO: Implement folder picker */ }
                )
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

    // About Dialog
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
                    text = "Versión 2.0.0\n\nAplicación profesional para cámaras USB\n\nCaracterísticas:\n• Grabación de video HD\n• Captura de fotos\n• Grid overlay\n• Galería integrada\n• Controles profesionales",
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
}

@Composable
private fun VideoQualitySelector(
    selectedQuality: String,
    onQualitySelected: (String) -> Unit
) {
    val qualities = listOf("4K", "1080p", "720p", "480p")
    
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
                imageVector = Icons.Default.Videocam,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Calidad de video",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Gray900
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            qualities.forEach { quality ->
                QualityChip(
                    text = quality,
                    selected = quality == selectedQuality,
                    onClick = { onQualitySelected(quality) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QualityChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(40.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) AccentBlue else Gray100,
        contentColor = if (selected) White else Gray700
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
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
private fun AudioBitrateSelector(
    selectedBitrate: Int,
    onBitrateSelected: (Int) -> Unit
) {
    val bitrates = listOf(
        64000 to "64 kbps",
        128000 to "128 kbps",
        192000 to "192 kbps",
        256000 to "256 kbps"
    )
    
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
                imageVector = Icons.Default.GraphicEq,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Bitrate de audio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Gray900
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            bitrates.forEach { (bitrate, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = bitrate == selectedBitrate,
                            onClick = { onBitrateSelected(bitrate) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RadioButton(
                        selected = bitrate == selectedBitrate,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = AccentBlue,
                            unselectedColor = Gray400
                        )
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Gray900
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioSampleRateSelector(
    selectedSampleRate: Int,
    onSampleRateSelected: (Int) -> Unit
) {
    val sampleRates = listOf(
        44100 to "44.1 kHz",
        48000 to "48 kHz"
    )
    
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
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Frecuencia de muestreo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Gray900
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            sampleRates.forEach { (sampleRate, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = sampleRate == selectedSampleRate,
                            onClick = { onSampleRateSelected(sampleRate) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RadioButton(
                        selected = sampleRate == selectedSampleRate,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = AccentBlue,
                            unselectedColor = Gray400
                        )
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Gray900
                    )
                }
            }
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
