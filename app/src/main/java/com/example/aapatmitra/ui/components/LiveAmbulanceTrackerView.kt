package com.example.aapatmitra.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aapatmitra.model.*
import com.example.aapatmitra.ui.theme.*

@Composable
fun LiveAmbulanceTrackerView(
    currentLang: Language,
    ambulance: Ambulance,
    selectedHospital: Hospital,
    activePatient: FamilyMember?,
    pickupAddress: String,
    routeProgressPercent: Int,
    remainingEtaMinutes: Int,
    remainingDistanceKm: Double,
    isPatientPickedUp: Boolean,
    onConfirmPatientPickup: () -> Unit,
    isStalled: Boolean,
    onTriggerStall: () -> Unit,
    onOpenDriverCall: () -> Unit,
    onOpenHospitalForm: () -> Unit,
    onOpenInsuranceChat: () -> Unit,
    onOpenMedicalReports: () -> Unit,
    onCancelEmergency: () -> Unit,
    hospitalFormSubmitted: Boolean
) {
    val t = Translations.get(currentLang)

    val infiniteTransition = rememberInfiniteTransition(label = "google_map_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val dashOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dash_offset"
    )

    var showShareSnackbar by remember { mutableStateOf(false) }
    var showPickupConfirmationToast by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(EmergencyDarkBg)
    ) {
        // 1. GOOGLE MAPS SYNTAX & UBER FORMAT NAVIGATION CONTAINER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
                .background(Color(0xFF1E222B))
                .testTag("google_map_live_container")
        ) {
            // Google Maps Styled Dark Canvas (Roads, Blocks, Water, Pins & Polyline)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Map Base Background Grid (Google Maps Dark Mode theme)
                drawRect(color = Color(0xFF1E222B))

                // City Blocks & Parks
                drawRect(color = Color(0xFF161922), topLeft = Offset(0f, 0f), size = androidx.compose.ui.geometry.Size(w * 0.4f, h * 0.35f))
                drawRect(color = Color(0xFF161922), topLeft = Offset(w * 0.6f, 0f), size = androidx.compose.ui.geometry.Size(w * 0.4f, h * 0.4f))
                drawRect(color = Color(0xFF161922), topLeft = Offset(0f, h * 0.65f), size = androidx.compose.ui.geometry.Size(w * 0.35f, h * 0.35f))
                drawRect(color = Color(0xFF161922), topLeft = Offset(w * 0.55f, h * 0.65f), size = androidx.compose.ui.geometry.Size(w * 0.45f, h * 0.35f))

                // Water Canal / Feature
                drawRect(color = Color(0xFF0F172A), topLeft = Offset(w * 0.88f, 0f), size = androidx.compose.ui.geometry.Size(w * 0.12f, h))

                // Local Arterial Roads (Google Maps Road Grid)
                val roadPaint = Color(0xFF2C3240)
                drawLine(roadPaint, Offset(0f, h * 0.28f), Offset(w, h * 0.28f), strokeWidth = 3.dp.toPx())
                drawLine(roadPaint, Offset(0f, h * 0.62f), Offset(w, h * 0.62f), strokeWidth = 3.dp.toPx())
                drawLine(roadPaint, Offset(w * 0.22f, 0f), Offset(w * 0.22f, h), strokeWidth = 3.dp.toPx())
                drawLine(roadPaint, Offset(w * 0.72f, 0f), Offset(w * 0.72f, h), strokeWidth = 3.dp.toPx())

                // Key Waypoints:
                // Point A: Ambulance Starting Garage / Station
                val startAmb = Offset(w * 0.16f, h * 0.22f)
                // Point B: Patient Home (Pickup Point 🏠)
                val homePickup = Offset(w * 0.48f, h * 0.54f)
                // Point C: Destination Hospital (🏥)
                val hospitalDest = Offset(w * 0.82f, h * 0.78f)

                // Leg 1 Path: Ambulance -> Home
                val leg1Path = Path().apply {
                    moveTo(startAmb.x, startAmb.y)
                    cubicTo(w * 0.22f, h * 0.45f, w * 0.32f, h * 0.38f, homePickup.x, homePickup.y)
                }

                // Leg 2 Path: Home -> Hospital
                val leg2Path = Path().apply {
                    moveTo(homePickup.x, homePickup.y)
                    cubicTo(w * 0.62f, h * 0.68f, w * 0.72f, h * 0.60f, hospitalDest.x, hospitalDest.y)
                }

                // Full Route Path
                val fullPath = Path().apply {
                    moveTo(startAmb.x, startAmb.y)
                    cubicTo(w * 0.22f, h * 0.45f, w * 0.32f, h * 0.38f, homePickup.x, homePickup.y)
                    cubicTo(w * 0.62f, h * 0.68f, w * 0.72f, h * 0.60f, hospitalDest.x, hospitalDest.y)
                }

                // Draw Highway Road Base (Wide Dark Asphalt)
                drawPath(
                    path = fullPath,
                    color = Color(0xFF334155),
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw Google Maps Traffic Route Line:
                // Leg 1 (To Home): Blue/Cyan Route
                drawPath(
                    path = leg1Path,
                    color = if (isPatientPickedUp) Color(0xFF475569) else Color(0xFF0284C7),
                    style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
                )

                // Leg 2 (To Hospital): Green Corridor Fast-Track Route
                drawPath(
                    path = leg2Path,
                    color = if (isPatientPickedUp) Color(0xFF10B981) else Color(0xFF0284C7).copy(alpha = 0.5f),
                    style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
                )

                // Animated Dashed Centerline (Google Maps Navigation Style)
                drawPath(
                    path = fullPath,
                    color = Color.White.copy(alpha = 0.85f),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), dashOffset)
                    )
                )

                // Calculate Live Ambulance Position on Route
                val progress = (routeProgressPercent / 100f).coerceIn(0f, 1f)
                val ambPos = if (progress <= 0.5f) {
                    val p = progress * 2f
                    Offset(
                        startAmb.x + (homePickup.x - startAmb.x) * p,
                        startAmb.y + (homePickup.y - startAmb.y) * p
                    )
                } else {
                    val p = (progress - 0.5f) * 2f
                    Offset(
                        homePickup.x + (hospitalDest.x - homePickup.x) * p,
                        homePickup.y + (hospitalDest.y - homePickup.y) * p
                    )
                }

                // 1. PIN: Patient Home Pickup Marker 🏠 (Google Maps Style Green Marker)
                drawCircle(
                    color = Color(0xFF10B981).copy(alpha = 0.25f),
                    radius = 20.dp.toPx() * if (!isPatientPickedUp && progress >= 0.45f) pulseScale else 1f,
                    center = homePickup
                )
                drawCircle(
                    color = Color(0xFF10B981),
                    radius = 9.dp.toPx(),
                    center = homePickup
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = homePickup
                )

                // 2. PIN: Destination Hospital Marker 🏥 (Google Maps Red Hospital Pin)
                drawCircle(
                    color = CrimsonRed.copy(alpha = 0.25f),
                    radius = 20.dp.toPx(),
                    center = hospitalDest
                )
                drawCircle(
                    color = CrimsonRed,
                    radius = 10.dp.toPx(),
                    center = hospitalDest
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = hospitalDest
                )

                // 3. VEHICLE: Live Ambulance Marker 🚑 (With Heading Radar Pulse)
                drawCircle(
                    color = Color(0xFFFBBF24).copy(alpha = 0.35f),
                    radius = 22.dp.toPx() * pulseScale,
                    center = ambPos
                )
                drawCircle(
                    color = Color(0xFF0F172A),
                    radius = 12.dp.toPx(),
                    center = ambPos
                )
                drawCircle(
                    color = Color(0xFFF59E0B),
                    radius = 7.dp.toPx(),
                    center = ambPos
                )
            }

            // Google Maps Navigation Header (Turn-By-Turn Banner)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.94f))
                    .border(1.dp, CardSurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isPatientPickedUp) Color(0xFF10B981) else Color(0xFF0284C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPatientPickedUp) Icons.Default.LocalHospital else Icons.Default.Home,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (!isPatientPickedUp) "EN ROUTE TO PATIENT'S HOUSE" else "PATIENT ONBOARD • HEADING TO HOSPITAL",
                                color = if (!isPatientPickedUp) Color(0xFF38BDF8) else Color(0xFF34D399),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = if (!isPatientPickedUp) "Pickup: ${pickupAddress.take(28)}..." else "Drop: ${selectedHospital.name}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${remainingEtaMinutes} MIN",
                            color = Color(0xFFFBBF24),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Floating Google Map Controls (Compass & Recenter & Cancel)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Google Maps Recenter Button
                IconButton(
                    onClick = { /* Recenter map */ },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A).copy(alpha = 0.9f))
                        .border(1.dp, CardSurfaceBorder, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = "Recenter", tint = AccentCyan, modifier = Modifier.size(18.dp))
                }

                // Emergency Cancel
                IconButton(
                    onClick = onCancelEmergency,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A).copy(alpha = 0.9f))
                        .border(1.dp, CrimsonRed.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel Emergency", tint = CrimsonRed, modifier = Modifier.size(18.dp))
                }
            }

            // Map Pin Badges overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("🏠 Home", color = Color.White, fontSize = 10.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CrimsonRed))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("🏥 Hospital", color = Color.White, fontSize = 10.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("🚑 Ambulance", color = Color.White, fontSize = 10.sp)
                    }
                }
            }
        }

        // 2. CRITICAL ACTION: UBER FORMAT PATIENT PICKUP CONFIRMATION BUTTON
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = if (isPatientPickedUp) Color(0xFF064E3B) else Color(0xFF1E293B)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (isPatientPickedUp) Color(0xFF10B981) else Color(0xFF38BDF8)
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isPatientPickedUp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Ambulance Approaching Home",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "When patient boards the ambulance, tick the button below",
                                    color = SlateGray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Ticking / Confirmation Button
                    Button(
                        onClick = {
                            onConfirmPatientPickup()
                            showPickupConfirmationToast = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("confirm_patient_pickup_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Confirm Pickup",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✓ Confirm Patient Picked Up (मरीज बैठ गया)",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    // Patient confirmed on board
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "✓ Patient Picked Up & Safely Onboard!",
                                color = Color(0xFFD1FAE5),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Green Corridor engaged • En route to ${selectedHospital.name}",
                                color = Color(0xFFA7F3D0),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 3. UBER / OLA STYLE DRIVER & VEHICLE DETAILS SHEET
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .shadow(12.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
            shape = RoundedCornerShape(20.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(CardSurfaceBorder, Color(0xFF1E293B))))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header: Driver Details & Vehicle Info (Ola/Uber Layout)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Driver Profile & Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .border(2.dp, Color(0xFF38BDF8), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Driver Avatar",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ambulance.driverName,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFF59E0B))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "★ ${ambulance.rating}",
                                        color = Color.Black,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Text(
                                text = "Paramedic Lead • ALS EMT Verified",
                                color = SlateGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // UBER/OLA PICKUP OTP CODE
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "START PIN / OTP",
                            color = AccentCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF0284C7), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "8421",
                                color = Color(0xFF38BDF8),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Indian Number Plate Display & Vehicle Model
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Authentic Indian Vehicle Registration Plate Box
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFF94A3B8), RoundedCornerShape(4.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF1E3A8A))
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "IND",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(
                            text = ambulance.vehicleNo,
                            color = Color(0xFF0F172A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = ambulance.name,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${ambulance.type} Unit • Ventilator & O2",
                            color = Color(0xFF34D399),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar with Distance & ETA
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (!isPatientPickedUp) "To Home: ${remainingDistanceKm} km" else "To Hospital: ${remainingDistanceKm} km",
                            color = SlateGray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "ETA: $remainingEtaMinutes minutes",
                            color = Color(0xFFFBBF24),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { (routeProgressPercent / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (isStalled) CrimsonRed else Color(0xFF10B981),
                        trackColor = Color(0xFF1E293B)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // UBER/OLA QUICK ACTION BUTTONS (Call Driver & Share Trip)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onOpenDriverCall,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("uber_call_driver_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Call Driver", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { showShareSnackbar = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("uber_share_trip_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Share Trip", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                if (showShareSnackbar) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF065F46))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "✓ Live tracking link sent to 3 Guardian Angels & household emergency contacts",
                            color = Color(0xFFD1FAE5),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 4. EMERGENCY PRE-ADMISSION & CLINICAL ACTIONS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "HOSPITAL PRE-ADMISSION & CARE ACTIONS",
                color = AccentCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            // Hospital Pre-Admission Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenHospitalForm() }
                    .testTag("open_hospital_preadmission_card"),
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
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
                                .background(if (hospitalFormSubmitted) Color(0xFF10B981).copy(alpha = 0.2f) else CrimsonRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (hospitalFormSubmitted) Icons.Default.CheckCircle else Icons.Default.Description,
                                contentDescription = null,
                                tint = if (hospitalFormSubmitted) Color(0xFF10B981) else CrimsonRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Hospital Pre-Admission Triage Form",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (hospitalFormSubmitted) "✓ Sent to ${selectedHospital.name} Trauma Desk" else "Fill vitals, blood group & insurance for zero wait-time",
                                color = if (hospitalFormSubmitted) Color(0xFF34D399) else SlateGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = SlateGray
                    )
                }
            }

            // Insurance Cashless Pre-Auth Assistance Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenInsuranceChat() }
                    .testTag("open_insurance_chat_card"),
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
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
                                .background(Color(0xFF0284C7).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Insurance Cashless Desk AI",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "TPA pre-auth & cashless hospital network assistant",
                                color = SlateGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = SlateGray
                    )
                }
            }

            // Medical History & EHR Vault Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenMedicalReports() }
                    .testTag("open_medical_vault_card"),
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
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
                                .background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderShared,
                                contentDescription = null,
                                tint = Color(0xFFA78BFA),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Emergency Medical Records & Prescriptions",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${activePatient?.reports?.size ?: 3} reports synced with ER doctors",
                                color = SlateGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = SlateGray
                    )
                }
            }

            // Traffic Simulation Trigger (For Demo purposes)
            OutlinedButton(
                onClick = onTriggerStall,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("simulate_delay_button"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFBBF24)),
                border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.linearGradient(listOf(Color(0xFFB45309), Color(0xFFB45309)))),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Traffic, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Simulate Traffic Congestion & Auto-Reroute", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
