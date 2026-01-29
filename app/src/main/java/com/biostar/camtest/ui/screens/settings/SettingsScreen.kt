package com.biostar.camtest.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.biostar.camtest.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var showAboutDialog by remember { mutableStateOf(false) }
    var showQualityDialog by remember { mutableStateOf(false) }
    var showGridDialog by remember { mutableStateOf(false) }
    var showStorageDialog by remember { mutableStateOf(false) }
    var showAudioDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    
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
                SettingsItem(
                    icon = Icons.Default.Videocam,
                    title = "Calidad de video",
                    subtitle = "Ajustar resolución y FPS",
                    onClick = { showQualityDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.GridOn,
                    title = "Grid por defecto",
                    subtitle = "Regla de 3, cuadrícula o proporción áurea",
                    onClick = { showGridDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.PhotoLibrary,
                    title = "Ubicación de guardado",
                    subtitle = "Carpeta de fotos y videos",
                    onClick = { showStorageDialog = true }
                )
            }

            // Audio Settings Section
            SettingsSection(title = "Audio") {
                SettingsItem(
                    icon = Icons.Default.Mic,
                    title = "Calidad de audio",
                    subtitle = "Bitrate y frecuencia de muestreo",
                    onClick = { showAudioDialog = true }
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
                SettingsItem(
                    icon = Icons.Default.BugReport,
                    title = "Reportar problema",
                    subtitle = "Enviar feedback",
                    onClick = { showFeedbackDialog = true }
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
        InfoDialog(
            icon = Icons.Default.Videocam,
            title = "USB CAM Pro",
            message = "Versión 2.0.0\n\nAplicación profesional para cámaras USB\n\nCaracterísticas:\n• Grabación de video HD\n• Captura de fotos\n• Grid overlay\n• Galería integrada\n• Controles profesionales",
            onDismiss = { showAboutDialog = false }
        )
    }
    
    if (showQualityDialog) {
        InfoDialog(
            icon = Icons.Default.Videocam,
            title = "Calidad de Video",
            message = "La calidad de video se ajusta automáticamente según la resolución seleccionada en la pantalla de cámara.\n\nResoluciones disponibles:\n• 4K (3840x2160)\n• 1080p (1920x1080)\n• 720p (1280x720)\n\nFPS: 30/60 según capacidad de la cámara",
            onDismiss = { showQualityDialog = false }
        )
    }
    
    if (showGridDialog) {
        InfoDialog(
            icon = Icons.Default.GridOn,
            title = "Grid Overlay",
            message = "Activa el grid desde el botón en la pantalla de cámara.\n\nTipos disponibles:\n• Regla de 3 (Rule of Thirds)\n• Cuadrícula (Square Grid)\n• Proporción Áurea (Golden Ratio)\n\nÚsalo para mejorar la composición de tus fotos y videos.",
            onDismiss = { showGridDialog = false }
        )
    }
    
    if (showStorageDialog) {
        InfoDialog(
            icon = Icons.Default.PhotoLibrary,
            title = "Ubicación de Guardado",
            message = "Las fotos y videos se guardan en:\n\nPictures/USBCam/\n\nPuedes acceder a ellos desde:\n• La galería de la app\n• La app Fotos de tu dispositivo\n• Cualquier explorador de archivos",
            onDismiss = { showStorageDialog = false }
        )
    }
    
    if (showAudioDialog) {
        InfoDialog(
            icon = Icons.Default.Mic,
            title = "Calidad de Audio",
            message = "Configuración de audio para grabación:\n\nBitrate: 128 kbps\nFrecuencia: 44.1 kHz\nCanales: Estéreo\n\nEl audio se captura automáticamente durante la grabación de video.",
            onDismiss = { showAudioDialog = false }
        )
    }
    
    if (showFeedbackDialog) {
        InfoDialog(
            icon = Icons.Default.BugReport,
            title = "Reportar Problema",
            message = "¿Encontraste un problema?\n\nPor favor contacta al desarrollador con:\n• Descripción del problema\n• Pasos para reproducirlo\n• Modelo de cámara USB\n• Versión de Android\n\nGracias por ayudarnos a mejorar.",
            onDismiss = { showFeedbackDialog = false }
        )
    }
}

@Composable
private fun InfoDialog(
    icon: ImageVector,
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido")
            }
        }
    )
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
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
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
