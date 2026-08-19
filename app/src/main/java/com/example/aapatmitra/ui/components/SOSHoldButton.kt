package com.example.aapatmitra.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aapatmitra.model.*
import com.example.aapatmitra.ui.theme.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SOSHoldButton(
    currentLang: Language,
    transportMode: String,
    onModeChanged: (String) -> Unit,
    activePatient: FamilyMember?,
    familyMembers: List<FamilyMember>,
    onSelectPatient: (FamilyMember) -> Unit,
    selectedHospital: Hospital,
    onOpenHospitalPicker: () -> Unit,
    pickupAddress: String,
    onUpdateAddress: (String) -> Unit,
    isFastDemoMode: Boolean,
    onToggleFastDemoMode: () -> Unit,
    onStartDispatch: () -> Unit,
    onOpenFamilyPortal: () -> Unit
) {
    val t = Translations.get(currentLang)
    val coroutineScope = rememberCoroutineScope()

    // 2-Second Press and Hold Engine
    var isHolding by remember { mutableStateOf(false) }
    var holdProgress by remember { mutableFloatStateOf(0f) }
    var holdJob by remember { mutableStateOf<Job?>(null) }
    var showAddressEditor by remember { mutableStateOf(false) }
    var tempAddressText by remember { mutableStateOf(pickupAddress) }

    val infinitePulse = rememberInfiniteTransition(label = "sos_outer_pulse")
    val outerGlowScale by infinitePulse.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TOP 1: Mode Switcher Tab (Emergency SOS vs Non-Emergency)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E293B))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (transportMode == "emergency") CrimsonRed else Color.Transparent)
                    .clickable { onModeChanged("emergency") }
                    .padding(vertical = 10.dp)
                    .testTag("mode_emergency_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Emergency SOS",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (transportMode == "non-emergency") Color(0xFF0284C7) else Color.Transparent)
                    .clickable { onModeChanged("non-emergency") }
                    .padding(vertical = 10.dp)
                    .testTag("mode_non_emergency_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Non Emergency",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // TOP 2: USP HERO - PROMINENT SOS PRESS & HOLD BUTTON ON TOP
        Box(
            modifier = Modifier
                .size(210.dp)
                .testTag("sos_giant_button_container"),
            contentAlignment = Alignment.Center
        ) {
            // Pulsing Outer Aura
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(if (isHolding) 1.25f else outerGlowScale)
                    .clip(CircleShape)
                    .background(CrimsonRed.copy(alpha = if (isHolding) 0.35f else 0.18f))
            )

            // Canvas for Circular Progress Arc
            Canvas(modifier = Modifier.size(185.dp)) {
                // Background Track
                drawCircle(
                    color = CrimsonDark.copy(alpha = 0.5f),
                    radius = size.minDimension / 2 - 4.dp.toPx(),
                    style = Stroke(width = 7.dp.toPx())
                )

                // Active Progress Arc
                if (holdProgress > 0f) {
                    drawArc(
                        brush = Brush.sweepGradient(listOf(Color(0xFFFBBF24), Color(0xFFEF4444), Color(0xFFFBBF24))),
                        startAngle = -90f,
                        sweepAngle = 360f * holdProgress,
                        useCenter = false,
                        topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                        size = Size(size.width - 8.dp.toPx(), size.height - 8.dp.toPx()),
                        style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // Core Pressable Circle
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .shadow(16.dp, CircleShape, spotColor = CrimsonRed)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                if (isHolding) Color(0xFFB91C1C) else CrimsonRed,
                                CrimsonDark
                            )
                        )
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isHolding = true
                                holdProgress = 0f
                                holdJob = coroutineScope.launch {
                                    val durationMs = 2000
                                    val intervalMs = 20
                                    var elapsed = 0
                                    while (elapsed < durationMs) {
                                        delay(intervalMs.toLong())
                                        elapsed += intervalMs
                                        holdProgress = (elapsed.toFloat() / durationMs).coerceAtMost(1f)
                                    }
                                    onStartDispatch()
                                    isHolding = false
                                    holdProgress = 0f
                                }

                                tryAwaitRelease()
                                isHolding = false
                                holdProgress = 0f
                                holdJob?.cancel()
                            },
                            onTap = {
                                coroutineScope.launch {
                                    holdProgress = 0.25f
                                    delay(200)
                                    holdProgress = 0f
                                }
                            }
                        )
                    }
                    .testTag("sos_core_hold_button"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "SOS Siren",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isHolding) t.holdingProgress else "SOS",
                        color = Color.White,
                        fontSize = if (isHolding) 13.sp else 26.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = if (isHolding) "${(holdProgress * 100).toInt()}%" else "HOLD 2 SECS",
                        color = if (isHolding) Color(0xFFFDE047) else Color(0xFFFCA5A5),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Direct Quick Dispatch Button
        Button(
            onClick = onStartDispatch,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("direct_dispatch_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "⚡ TAP FOR INSTANT DISPATCH",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 1. Patient Selector Card with Insurance details
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("patient_selector_card"),
            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CardSurfaceBorder, CardSurfaceBorder))),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = t.whoNeedsHelp,
                            color = Color(0xFF38BDF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    TextButton(
                        onClick = onOpenFamilyPortal,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "+ Manage Family",
                            color = Color(0xFF60A5FA),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Horizontal Family Member Selection Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    familyMembers.forEach { member ->
                        val isSelected = activePatient?.id == member.id

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectPatient(member) }
                                .testTag("patient_chip_${member.id}"),
                            color = if (isSelected) CrimsonRed else Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)) else androidx.compose.foundation.BorderStroke(1.dp, CardSurfaceBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color(0xFF0F172A)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = member.relation.take(1).uppercase(),
                                        color = if (isSelected) Color.White else Color(0xFF38BDF8),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "${member.name} (${member.relation})",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }

                // Active Selected Patient Details Card
                if (activePatient != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${activePatient.name} • ${activePatient.relation} • ${activePatient.age} yrs • Blood: ${activePatient.bloodGroup}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                                if (activePatient.allergies.isNotEmpty()) {
                                    Text(
                                        text = "Allergies: ${activePatient.allergies.joinToString(", ")}",
                                        color = Color(0xFFF87171),
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                }
                            }

                            val insuranceName = activePatient.insurances.firstOrNull()?.provider ?: "Cashless Active"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🛡️ $insuranceName",
                                    color = EmeraldGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Destination Hospital Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenHospitalPicker() }
                .testTag("hospital_selector_card"),
            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = CrimsonRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = t.dropHospital,
                            color = SlateGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = selectedHospital.name,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${selectedHospital.distanceKm} km away • ${selectedHospital.availableEmergencyBeds} ER Beds Available",
                            color = Color(0xFF34D399),
                            fontSize = 11.sp
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Change Hospital",
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Pickup Location Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = AmberWarning,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = t.pickupLocation,
                            color = AmberWarning,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .clickable {
                                onUpdateAddress("Live GPS: 28.4595° N, 77.0725° E (±5m verified)")
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = null,
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = t.usePhoneGps,
                            color = Color(0xFF60A5FA),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (showAddressEditor) {
                    OutlinedTextField(
                        value = tempAddressText,
                        onValueChange = { tempAddressText = it },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = {
                                onUpdateAddress(tempAddressText)
                                showAddressEditor = false
                            }) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = "Save", tint = Color.Green)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CrimsonRed
                        )
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAddressEditor = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pickupAddress,
                            color = Color.White,
                            fontSize = 12.sp,
                            maxLines = 2,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = SlateGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Fast Demo Mode Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1E293B))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = t.fastDemoMode,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "5s tier search speed for quick testing",
                        color = SlateGray,
                        fontSize = 10.sp
                    )
                }
            }

            Switch(
                checked = isFastDemoMode,
                onCheckedChange = { onToggleFastDemoMode() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = CrimsonRed
                ),
                modifier = Modifier.testTag("fast_demo_switch")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Smart 2-Stage Dispatch Algorithm Guarantee Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = t.twoStageGuaranteeTitle,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = t.twoStageGuaranteeDesc,
                        color = SlateGray,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
