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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aapatmitra.model.*
import com.example.aapatmitra.ui.theme.*

@Composable
fun SearchingDispatchView(
    currentLang: Language,
    selectedHospital: Hospital,
    searchRadiusKm: Double,
    currentTierIndex: Int,
    countdownSeconds: Int,
    isFastDemoMode: Boolean,
    onCancelEmergency: () -> Unit,
    onForceSelectAmbulance: (Ambulance) -> Unit,
    ambulances: List<Ambulance>
) {
    val t = Translations.get(currentLang)

    val infiniteSweep = rememberInfiniteTransition(label = "radar_sweep")
    val radarAngle by infiniteSweep.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_angle"
    )

    val radarRadiusPulse by infiniteSweep.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius_pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Tier Progress Indicator
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CrimsonRed.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "AREA EXPANSION SEARCH • TIER ${currentTierIndex + 1} (${searchRadiusKm} KM)",
                    color = CrimsonGlow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Auto-Dispatching Nearest Ambulance",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Scanning ${searchRadiusKm} km radius. Auto-dispatch will initiate without manual confirmation.",
                color = SlateGray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Central Radar Canvas Animation
        Box(
            modifier = Modifier
                .size(240.dp)
                .testTag("radar_animation_box"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val maxRadius = size.minDimension / 2

                // Concentric Range Rings
                drawCircle(color = CardSurfaceBorder, radius = maxRadius * 0.33f, style = Stroke(width = 1.5.dp.toPx()))
                drawCircle(color = CardSurfaceBorder, radius = maxRadius * 0.66f, style = Stroke(width = 1.5.dp.toPx()))
                drawCircle(color = CrimsonDark.copy(alpha = 0.6f), radius = maxRadius, style = Stroke(width = 2.dp.toPx()))

                // Pulsing Expanding Ripple
                drawCircle(
                    color = CrimsonRed.copy(alpha = (1f - radarRadiusPulse).coerceIn(0f, 0.6f)),
                    radius = maxRadius * radarRadiusPulse,
                    style = Stroke(width = 2.dp.toPx())
                )

                // Radar Sweeper Arc
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to CrimsonRed.copy(alpha = 0.5f),
                        0.25f to Color.Transparent,
                        1.0f to Color.Transparent,
                        center = center
                    ),
                    startAngle = radarAngle,
                    sweepAngle = 90f,
                    useCenter = true,
                    topLeft = Offset.Zero,
                    size = size
                )

                // Target Hospital Indicator (Center Beacon)
                drawCircle(color = Color(0xFF10B981), radius = 6.dp.toPx(), center = center)
            }

            // Central Hospital Beacon Icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = null,
                    tint = CrimsonRed,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Live Countdown & Status Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Destination Hospital:",
                            color = SlateGray,
                            fontSize = 11.sp
                        )
                        Text(
                            text = selectedHospital.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${countdownSeconds}s",
                            color = Color(0xFFFBBF24),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (countdownSeconds.toFloat() / if (isFastDemoMode) 5f else 60f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = CrimsonRed,
                    trackColor = Color(0xFF1E293B),
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = AccentCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (countdownSeconds > 0) "Auto-expanding radius in ${countdownSeconds}s if no match" else "Expanding search radius...",
                        color = SlateGray,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Quick Controls
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Instant Auto-Dispatch shortcut (bypasses wait)
            Button(
                onClick = {
                    val candidate = ambulances.firstOrNull { it.hospitalAffiliation == selectedHospital.name }
                        ?: ambulances.first()
                    onForceSelectAmbulance(candidate)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("instant_auto_dispatch_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Instant Auto-Dispatch (Skip Timer)", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onCancelEmergency,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("cancel_search_button"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
                border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFF7F1D1D), Color(0xFF7F1D1D)))),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = t.cancelEmergency, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
