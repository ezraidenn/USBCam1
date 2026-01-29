package com.biostar.camtest.ui.screens.camera

import android.view.TextureView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Gray50,
                        Gray100
                    )
                )
            )
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Background with rounded corners
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Gray700)
                )
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
                                    setBackgroundColor(android.graphics.Color.parseColor("#262626"))
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

                        // Top overlay controls with glassmorphism
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            GlassButton(
                                icon = Icons.Default.GridOn,
                                onClick = { viewModel.toggleGridOverlay() },
                                isSelected = uiState.gridOverlay.enabled
                            )

                            GlassButton(
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Recording indicator
                AnimatedVisibility(
                    visible = isRecording,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    RecordingIndicator(duration = recordingDuration)
                }

                // Resolution selector
                AnimatedVisibility(
                    visible = !isRecording,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(
                                onClick = onResolutionClick,
                                indication = ripple(),
                                interactionSource = remember { MutableInteractionSource() }
                            ),
                        color = White.copy(alpha = 0.9f),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Gray700
                            )
                            Text(
                                text = selectedResolution,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Gray900
                            )
                        }
                    }
                }

                // Settings button
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable(
                            onClick = onSettingsClick,
                            indication = ripple(),
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                    color = White.copy(alpha = 0.9f),
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Gray700,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraPlaceholder(
    isConnecting: Boolean,
    onStartClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier
                .size(120.dp)
                .scale(if (isConnecting) scale else 1f),
            shape = CircleShape,
            color = AccentBlue.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Videocam,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = AccentBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Cámara USB",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = White
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isConnecting) "Conectando..." else "Conecta tu cámara USB para comenzar",
            style = MaterialTheme.typography.bodyLarge,
            color = Gray300,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        AnimatedVisibility(
            visible = !isConnecting,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Videocam,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Iniciar Cámara",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        AnimatedVisibility(
            visible = isConnecting,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            CircularProgressIndicator(
                color = AccentBlue,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
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
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = Error.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = Error
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Error de Conexión",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = White
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = Gray300,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentBlue
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Reintentar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left controls
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ControlButton(
                    icon = Icons.Default.FlipCameraAndroid,
                    onClick = onMirrorClick,
                    enabled = enabled,
                    isSelected = isMirrored,
                    contentDescription = "Espejo"
                )
                ControlButton(
                    icon = Icons.Default.RotateRight,
                    onClick = onRotateClick,
                    enabled = enabled,
                    contentDescription = "Rotar"
                )
            }

            // Center controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PhotoButton(
                    onClick = onPhotoClick,
                    size = 60.dp,
                    enabled = enabled
                )
                RecordButton(
                    isRecording = isRecording,
                    onClick = onRecordClick,
                    size = 76.dp,
                    enabled = enabled
                )
            }

            // Right controls
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ControlButton(
                    icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    onClick = onMuteClick,
                    enabled = enabled,
                    isSelected = isMuted,
                    contentDescription = if (isMuted) "Activar audio" else "Silenciar"
                )
            }
        }
    }
}

@Composable
private fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean,
    isSelected: Boolean = false,
    contentDescription: String
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Surface(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() }
            ),
        color = if (isSelected) AccentBlue.copy(alpha = 0.15f) else Gray100,
        shadowElevation = if (enabled) 2.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (isSelected) AccentBlue else if (enabled) Gray700 else Gray400,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun GlassButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isSelected: Boolean = false
) {
    val backgroundColor = if (isSelected) White.copy(alpha = 0.3f) else White.copy(alpha = 0.15f)
    
    Surface(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(
                onClick = onClick,
                indication = ripple(color = White),
                interactionSource = remember { MutableInteractionSource() }
            ),
        color = backgroundColor,
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(22.dp)
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
