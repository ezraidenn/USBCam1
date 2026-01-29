package com.biostar.camtest.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.biostar.camtest.ui.theme.*

@Composable
fun IconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    isSelected: Boolean = false,
    tint: Color = if (isSelected) AccentBlue else Gray700
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isSelected) AccentBlue.copy(alpha = 0.1f) else Gray100)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) tint else Gray400,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun RecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    enabled: Boolean = true
) {
    val scale by animateFloatAsState(
        targetValue = if (isRecording) 0.85f else 1f,
        label = "record_button_scale"
    )
    
    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(if (enabled) White else Gray300)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(if (isRecording) RoundedCornerShape(8.dp) else CircleShape)
                .background(if (enabled) RecordingRed else Gray400)
        )
    }
}

@Composable
fun PhotoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(if (enabled) White else Gray300)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Color.Transparent)
        )
    }
}
