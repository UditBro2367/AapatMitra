package com.example.aapatmitra.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.aapatmitra.model.Ambulance
import com.example.aapatmitra.ui.theme.CardDarkBg
import com.example.aapatmitra.ui.theme.CrimsonRed
import com.example.aapatmitra.ui.theme.SlateGray
import kotlinx.coroutines.delay

@Composable
fun DriverCallDialog(
    ambulance: Ambulance,
    onEndCall: () -> Unit
) {
    var callSeconds by remember { mutableIntStateOf(0) }
    var isMuted by remember { mutableStateOf(false) }
    var isSpeaker by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callSeconds++
        }
    }

    val infiniteWave = rememberInfiniteTransition(label = "audio_waveform")
    val waveHeight1 by infiniteWave.animateFloat(
        initialValue = 0.2f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w1"
    )
    val waveHeight2 by infiniteWave.animateFloat(
        initialValue = 0.8f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(450, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "w2"
    )

    Dialog(onDismissRequest = onEndCall) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            color = Color(0xFF0B0F19)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Call Status
                Text(
                    text = "EMERGENCY DISPATCH SECURE CALL",
                    color = Color(0xFF10B981),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = CrimsonRed,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = ambulance.driverName,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${ambulance.name} (${ambulance.vehicleNo})",
                    color = SlateGray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Call Duration
                val min = callSeconds / 60
                val sec = callSeconds % 60
                Text(
                    text = String.format("%02d:%02d", min, sec),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Audio Waveform Visualization
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(36.dp)
                ) {
                    val barWidth = 4.dp.toPx()
                    val gap = 6.dp.toPx()
                    val heights = listOf(waveHeight1, waveHeight2, waveHeight1 * 0.7f, waveHeight2 * 1.1f, waveHeight1 * 0.5f)
                    var x = (size.width - (heights.size * (barWidth + gap))) / 2

                    heights.forEach { h ->
                        val barH = size.height * h.coerceIn(0.1f, 1f)
                        val y = (size.height - barH) / 2
                        drawLine(
                            color = Color(0xFF10B981),
                            start = Offset(x, y),
                            end = Offset(x, y + barH),
                            strokeWidth = barWidth
                        )
                        x += barWidth + gap
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Row (Mute, End Call, Speaker)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Button
                    FilledTonalIconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier.size(52.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (isMuted) Color.White else Color(0xFF1E293B),
                            contentColor = if (isMuted) Color.Black else Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute"
                        )
                    }

                    // End Call Button
                    FilledIconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .size(64.dp)
                            .testTag("end_call_button"),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = CrimsonRed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Speaker Button
                    FilledTonalIconButton(
                        onClick = { isSpeaker = !isSpeaker },
                        modifier = Modifier.size(52.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (isSpeaker) Color(0xFF10B981) else Color(0xFF1E293B),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = if (isSpeaker) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                            contentDescription = "Speaker"
                        )
                    }
                }
            }
        }
    }
}
