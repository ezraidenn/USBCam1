package com.biostar.camtest.ui.screens.camera

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.biostar.camtest.data.models.CameraControlValue
import com.biostar.camtest.data.models.UVCControlId
import com.biostar.camtest.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Camera controls overlay similar to Android stock camera
 * Shows controls over live preview with dimmed background
 */
@Composable
fun CameraControlsOverlay(
    brightness: CameraControlValue,
    contrast: CameraControlValue,
    saturation: CameraControlValue,
    sharpness: CameraControlValue,
    exposure: CameraControlValue,
    whiteBalance: CameraControlValue,
    onControlChange: (controlId: Int, value: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedControl by remember { mutableStateOf<Int?>(null) }
    var autoHideJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    
    // Dimmed background overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .pointerInput(Unit) {
                detectTapGestures {
                    onDismiss()
                }
            }
    ) {
        // Controls panel at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 100.dp)
                .pointerInput(Unit) {
                    detectTapGestures { /* Prevent dismiss */ }
                }
        ) {
            // Active control slider (when a control is selected)
            AnimatedVisibility(
                visible = selectedControl != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                selectedControl?.let { controlId ->
                    val controlValue = when (controlId) {
                        UVCControlId.PU_BRIGHTNESS -> brightness
                        UVCControlId.PU_CONTRAST -> contrast
                        UVCControlId.PU_SATURATION -> saturation
                        UVCControlId.PU_SHARPNESS -> sharpness
                        UVCControlId.CT_EXPOSURE_TIME_ABSOLUTE -> exposure
                        UVCControlId.PU_WHITE_BALANCE_TEMPERATURE -> whiteBalance
                        else -> null
                    }
                    
                    controlValue?.let { value ->
                        ActiveControlSlider(
                            label = getControlLabel(controlId),
                            value = value,
                            onValueChange = { newValue ->
                                onControlChange(controlId, newValue)
                            },
                            onDismiss = {
                                selectedControl = null
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Control buttons grid
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.15f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (brightness.isSupported) {
                            ControlButton(
                                icon = Icons.Default.Brightness6,
                                label = "Brillo",
                                isActive = selectedControl == UVCControlId.PU_BRIGHTNESS,
                                isDimmed = selectedControl != null && selectedControl != UVCControlId.PU_BRIGHTNESS,
                                onClick = {
                                    selectedControl = if (selectedControl == UVCControlId.PU_BRIGHTNESS) {
                                        null
                                    } else {
                                        UVCControlId.PU_BRIGHTNESS
                                    }
                                }
                            )
                        }
                        
                        if (contrast.isSupported) {
                            ControlButton(
                                icon = Icons.Default.Contrast,
                                label = "Contraste",
                                isActive = selectedControl == UVCControlId.PU_CONTRAST,
                                isDimmed = selectedControl != null && selectedControl != UVCControlId.PU_CONTRAST,
                                onClick = {
                                    selectedControl = if (selectedControl == UVCControlId.PU_CONTRAST) {
                                        null
                                    } else {
                                        UVCControlId.PU_CONTRAST
                                    }
                                }
                            )
                        }
                        
                        if (saturation.isSupported) {
                            ControlButton(
                                icon = Icons.Default.Palette,
                                label = "Saturación",
                                isActive = selectedControl == UVCControlId.PU_SATURATION,
                                isDimmed = selectedControl != null && selectedControl != UVCControlId.PU_SATURATION,
                                onClick = {
                                    selectedControl = if (selectedControl == UVCControlId.PU_SATURATION) {
                                        null
                                    } else {
                                        UVCControlId.PU_SATURATION
                                    }
                                }
                            )
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (sharpness.isSupported) {
                            ControlButton(
                                icon = Icons.Default.FilterVintage,
                                label = "Nitidez",
                                isActive = selectedControl == UVCControlId.PU_SHARPNESS,
                                isDimmed = selectedControl != null && selectedControl != UVCControlId.PU_SHARPNESS,
                                onClick = {
                                    selectedControl = if (selectedControl == UVCControlId.PU_SHARPNESS) {
                                        null
                                    } else {
                                        UVCControlId.PU_SHARPNESS
                                    }
                                }
                            )
                        }
                        
                        if (exposure.isSupported) {
                            ControlButton(
                                icon = Icons.Default.Exposure,
                                label = "Exposición",
                                isActive = selectedControl == UVCControlId.CT_EXPOSURE_TIME_ABSOLUTE,
                                isDimmed = selectedControl != null && selectedControl != UVCControlId.CT_EXPOSURE_TIME_ABSOLUTE,
                                onClick = {
                                    selectedControl = if (selectedControl == UVCControlId.CT_EXPOSURE_TIME_ABSOLUTE) {
                                        null
                                    } else {
                                        UVCControlId.CT_EXPOSURE_TIME_ABSOLUTE
                                    }
                                }
                            )
                        }
                        
                        if (whiteBalance.isSupported) {
                            ControlButton(
                                icon = Icons.Default.WbSunny,
                                label = "Balance B.",
                                isActive = selectedControl == UVCControlId.PU_WHITE_BALANCE_TEMPERATURE,
                                isDimmed = selectedControl != null && selectedControl != UVCControlId.PU_WHITE_BALANCE_TEMPERATURE,
                                onClick = {
                                    selectedControl = if (selectedControl == UVCControlId.PU_WHITE_BALANCE_TEMPERATURE) {
                                        null
                                    } else {
                                        UVCControlId.PU_WHITE_BALANCE_TEMPERATURE
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ControlButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    isDimmed: Boolean,
    onClick: () -> Unit
) {
    val alpha by animateFloatAsState(
        targetValue = if (isDimmed) 0.3f else 1f,
        animationSpec = tween(200),
        label = "alpha"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    Column(
        modifier = Modifier
            .alpha(alpha)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = if (isActive) AccentBlue else Color.White.copy(alpha = 0.2f),
            onClick = onClick
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isActive) White else White.copy(alpha = 0.9f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = White,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun ActiveControlSlider(
    label: String,
    value: CameraControlValue,
    onValueChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(value.current.toFloat()) }
    
    LaunchedEffect(Unit) {
        delay(5000) // Auto-hide after 5 seconds
        onDismiss()
    }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Gray900
                )
                
                Text(
                    text = sliderValue.toInt().toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AccentBlue
                )
            }
            
            Slider(
                value = sliderValue,
                onValueChange = { newValue ->
                    sliderValue = newValue
                    onValueChange(newValue.toInt())
                },
                valueRange = value.min.toFloat()..value.max.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = AccentBlue,
                    activeTrackColor = AccentBlue,
                    inactiveTrackColor = Gray200
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = value.min.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500
                )
                Text(
                    text = value.max.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500
                )
            }
        }
    }
}

private fun getControlLabel(controlId: Int): String {
    return when (controlId) {
        UVCControlId.PU_BRIGHTNESS -> "Brillo"
        UVCControlId.PU_CONTRAST -> "Contraste"
        UVCControlId.PU_SATURATION -> "Saturación"
        UVCControlId.PU_SHARPNESS -> "Nitidez"
        UVCControlId.CT_EXPOSURE_TIME_ABSOLUTE -> "Exposición"
        UVCControlId.PU_WHITE_BALANCE_TEMPERATURE -> "Balance de Blancos"
        else -> "Control"
    }
}
