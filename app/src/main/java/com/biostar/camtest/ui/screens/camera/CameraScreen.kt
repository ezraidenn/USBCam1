package com.biostar.camtest.ui.screens.camera

import android.view.TextureView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.biostar.camtest.data.models.CameraState
import com.biostar.camtest.data.models.GridType
import com.biostar.camtest.ui.components.*
import com.biostar.camtest.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onNavigateToGallery: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var textureView by remember { mutableStateOf<TextureView?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            CameraTopBar(
                cameraState = uiState.cameraState,
                selectedResolution = uiState.selectedResolution?.displayName ?: "No conectado",
                isRecording = uiState.isRecording,
                recordingDuration = formatDuration(uiState.recordingDuration),
                onSettingsClick = onNavigateToSettings,
                onResolutionClick = { viewModel.showResolutionSheet(true) }
            )

            // Camera preview area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Black)
            ) {
                when (val state = uiState.cameraState) {
                    is CameraState.Idle, is CameraState.Connecting -> {
                        CameraPlaceholder(
                            isConnecting = state is CameraState.Connecting,
                            onStartClick = { viewModel.startCamera() }
                        )
                    }
                    is CameraState.Connected, is CameraState.Recording -> {
                        // TextureView for camera preview
                        AndroidView(
                            factory = { context ->
                                TextureView(context).apply {
                                    textureView = this
                                    surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                                        override fun onSurfaceTextureAvailable(
                                            surface: android.graphics.SurfaceTexture,
                                            width: Int,
                                            height: Int
                                        ) {
                                            viewModel.setSurface(android.view.Surface(surface))
                                        }

                                        override fun onSurfaceTextureSizeChanged(
                                            surface: android.graphics.SurfaceTexture,
                                            width: Int,
                                            height: Int
                                        ) {}

                                        override fun onSurfaceTextureDestroyed(
                                            surface: android.graphics.SurfaceTexture
                                        ): Boolean {
                                            viewModel.removeSurface()
                                            return true
                                        }

                                        override fun onSurfaceTextureUpdated(
                                            surface: android.graphics.SurfaceTexture
                                        ) {}
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Grid overlay
                        if (uiState.gridOverlay.enabled) {
                            GridOverlay(gridType = uiState.gridOverlay.type)
                        }

                        // Top overlay controls
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                icon = Icons.Default.GridOn,
                                onClick = { viewModel.toggleGridOverlay() },
                                isSelected = uiState.gridOverlay.enabled
                            )

                            IconButton(
                                icon = Icons.Default.PhotoLibrary,
                                onClick = onNavigateToGallery
                            )
                        }
                    }
                    is CameraState.Error -> {
                        ErrorPlaceholder(
                            message = state.message,
                            onRetry = { viewModel.startCamera() }
                        )
                    }
                }
            }

            // Bottom controls
            CameraBottomControls(
                isRecording = uiState.isRecording,
                isMirrored = uiState.isMirrored,
                isMuted = uiState.isMuted,
                enabled = uiState.cameraState is CameraState.Connected || uiState.cameraState is CameraState.Recording,
                onRecordClick = { viewModel.toggleRecording() },
                onPhotoClick = {
                    textureView?.bitmap?.let { bitmap ->
                        viewModel.takePhoto(bitmap)
                    }
                },
                onMirrorClick = { viewModel.toggleMirror() },
                onRotateClick = { viewModel.rotateCamera() },
                onMuteClick = { viewModel.toggleMute() }
            )
        }

        // Resolution selector sheet
        if (uiState.showResolutionSheet) {
            ResolutionSelectorSheet(
                resolutions = uiState.availableResolutions,
                selectedResolution = uiState.selectedResolution,
                onResolutionSelected = { resolution ->
                    viewModel.setResolution(resolution)
                    viewModel.showResolutionSheet(false)
                },
                onDismiss = { viewModel.showResolutionSheet(false) }
            )
        }
    }
}

@Composable
private fun CameraTopBar(
    cameraState: CameraState,
    selectedResolution: String,
    isRecording: Boolean,
    recordingDuration: String,
    onSettingsClick: () -> Unit,
    onResolutionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Connection status
        val connectionStatus = when (cameraState) {
            is CameraState.Connected, is CameraState.Recording -> ConnectionStatus.Connected
            is CameraState.Connecting -> ConnectionStatus.Connecting
            is CameraState.Error -> ConnectionStatus.Error
            else -> ConnectionStatus.Disconnected
        }

        val deviceName = when (cameraState) {
            is CameraState.Connected -> cameraState.device.displayName
            is CameraState.Recording -> cameraState.device.displayName
            else -> null
        }

        ConnectionStatusBadge(
            status = connectionStatus,
            deviceName = deviceName
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Recording indicator
            if (isRecording) {
                RecordingIndicator(duration = recordingDuration)
            } else {
                // Resolution selector
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Gray100)
                        .clickable { onResolutionClick() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = selectedResolution,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Gray900
                    )
                }
            }

            IconButton(
                icon = Icons.Default.Settings,
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
private fun CameraPlaceholder(
    isConnecting: Boolean,
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Usb,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = AccentBlue
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Cámara USB",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isConnecting) "Conectando..." else "Conecta tu cámara USB y presiona iniciar",
            style = MaterialTheme.typography.bodyMedium,
            color = Gray400,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!isConnecting) {
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Videocam,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Iniciar Cámara",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            CircularProgressIndicator(color = AccentBlue)
        }
    }
}

@Composable
private fun ErrorPlaceholder(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Error
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Gray400,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentBlue
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Reintentar",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun GridOverlay(gridType: GridType) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 2f
        val color = Color.White.copy(alpha = 0.5f)

        when (gridType) {
            GridType.RULE_OF_THIRDS -> {
                // Vertical lines
                drawLine(
                    color = color,
                    start = Offset(size.width / 3, 0f),
                    end = Offset(size.width / 3, size.height),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = color,
                    start = Offset(size.width * 2 / 3, 0f),
                    end = Offset(size.width * 2 / 3, size.height),
                    strokeWidth = strokeWidth
                )

                // Horizontal lines
                drawLine(
                    color = color,
                    start = Offset(0f, size.height / 3),
                    end = Offset(size.width, size.height / 3),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = color,
                    start = Offset(0f, size.height * 2 / 3),
                    end = Offset(size.width, size.height * 2 / 3),
                    strokeWidth = strokeWidth
                )
            }
            GridType.SQUARE -> {
                val gridSize = 4
                for (i in 1 until gridSize) {
                    drawLine(
                        color = color,
                        start = Offset(size.width * i / gridSize, 0f),
                        end = Offset(size.width * i / gridSize, size.height),
                        strokeWidth = strokeWidth
                    )
                    drawLine(
                        color = color,
                        start = Offset(0f, size.height * i / gridSize),
                        end = Offset(size.width, size.height * i / gridSize),
                        strokeWidth = strokeWidth
                    )
                }
            }
            GridType.GOLDEN_RATIO -> {
                val phi = 1.618f
                val x1 = size.width / phi
                val x2 = size.width - x1
                val y1 = size.height / phi
                val y2 = size.height - y1

                drawLine(color, Offset(x1, 0f), Offset(x1, size.height), strokeWidth)
                drawLine(color, Offset(x2, 0f), Offset(x2, size.height), strokeWidth)
                drawLine(color, Offset(0f, y1), Offset(size.width, y1), strokeWidth)
                drawLine(color, Offset(0f, y2), Offset(size.width, y2), strokeWidth)
            }
        }
    }
}

@Composable
private fun CameraBottomControls(
    isRecording: Boolean,
    isMirrored: Boolean,
    isMuted: Boolean,
    enabled: Boolean,
    onRecordClick: () -> Unit,
    onPhotoClick: () -> Unit,
    onMirrorClick: () -> Unit,
    onRotateClick: () -> Unit,
    onMuteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left controls
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                icon = Icons.Default.FlipCameraAndroid,
                onClick = onMirrorClick,
                enabled = enabled,
                isSelected = isMirrored
            )
            IconButton(
                icon = Icons.Default.RotateRight,
                onClick = onRotateClick,
                enabled = enabled
            )
        }

        // Center controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PhotoButton(
                onClick = onPhotoClick,
                size = 56.dp,
                enabled = enabled
            )
            RecordButton(
                isRecording = isRecording,
                onClick = onRecordClick,
                size = 72.dp,
                enabled = enabled
            )
        }

        // Right controls
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                onClick = onMuteClick,
                enabled = enabled,
                isSelected = isMuted
            )
        }
    }
}

private fun formatDuration(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = millis / (1000 * 60 * 60)
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
