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
                    onClick = { /* TODO: Navigate to quality settings */ }
                )
                SettingsItem(
                    icon = Icons.Default.GridOn,
                    title = "Grid por defecto",
                    subtitle = "Regla de 3, cuadrícula o proporción áurea",
                    onClick = { /* TODO: Navigate to grid settings */ }
                )
                SettingsItem(
                    icon = Icons.Default.PhotoLibrary,
                    title = "Ubicación de guardado",
                    subtitle = "Carpeta de fotos y videos",
                    onClick = { /* TODO: Navigate to storage settings */ }
                )
            }

            // Audio Settings Section
            SettingsSection(title = "Audio") {
                SettingsItem(
                    icon = Icons.Default.Mic,
                    title = "Calidad de audio",
                    subtitle = "Bitrate y frecuencia de muestreo",
                    onClick = { /* TODO: Navigate to audio settings */ }
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
                    onClick = { /* TODO: Open feedback */ }
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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Versión 2.0.0")
                    Text("Aplicación profesional para cámaras USB")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Características:",
                        fontWeight = FontWeight.SemiBold
                    )
                    Text("• Grabación de video HD")
                    Text("• Captura de fotos")
                    Text("• Grid overlay")
                    Text("• Galería integrada")
                    Text("• Controles profesionales")
                }
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
