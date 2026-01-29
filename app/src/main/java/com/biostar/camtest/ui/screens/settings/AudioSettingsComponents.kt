package com.biostar.camtest.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.biostar.camtest.ui.theme.AccentBlue
import com.biostar.camtest.ui.theme.Gray400
import com.biostar.camtest.ui.theme.Gray900

@Composable
fun AudioBitrateSelector(
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
            Column {
                Text(
                    text = "Bitrate de audio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900
                )
                Text(
                    text = "Para grabación de video",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray400
                )
            }
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
fun AudioSampleRateSelector(
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
            Column {
                Text(
                    text = "Frecuencia de muestreo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Gray900
                )
                Text(
                    text = "Para grabación de video",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray400
                )
            }
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
