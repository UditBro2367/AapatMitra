package com.example.aapatmitra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aapatmitra.model.*
import com.example.aapatmitra.ui.theme.*

@Composable
fun NonEmergencyTransportView(
    currentLang: Language,
    activePatient: FamilyMember?,
    familyMembers: List<FamilyMember>,
    bookings: List<NonEmergencyBooking>,
    onBookRide: (NonEmergencyBooking) -> Unit,
    onSwitchToEmergency: () -> Unit
) {
    val t = Translations.get(currentLang)
    var selectedServiceType by remember { mutableStateOf("Wheelchair Van") }
    var pickupAddr by remember { mutableStateOf(activePatient?.address ?: "Flat 402, Lotus Towers, Golf Course Road, Gurugram") }
    var dropAddr by remember { mutableStateOf("Medanta The Medicity, Sector 38, Gurugram") }
    var scheduledDate by remember { mutableStateOf("Tomorrow, 10:30 AM") }
    var hasWheelchairRamp by remember { mutableStateOf(true) }
    var hasOxygenSupport by remember { mutableStateOf(false) }

    val serviceTypes = listOf("Wheelchair Van", "Stretcher Ambulance", "Clinic Shuttle")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Banner
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
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = t.nonEmergencyTransport,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(onClick = onSwitchToEmergency) {
                        Text("Switch to SOS", color = CrimsonRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Service Type Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            serviceTypes.forEach { type ->
                val isSelected = selectedServiceType == type
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Color(0xFF0284C7) else CardDarkBg)
                        .clickable { selectedServiceType = type }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = type,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Booking Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = pickupAddr,
                    onValueChange = { pickupAddr = it },
                    label = { Text("Pickup Address") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )

                OutlinedTextField(
                    value = dropAddr,
                    onValueChange = { dropAddr = it },
                    label = { Text("Destination Clinic / Hospital") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )

                OutlinedTextField(
                    value = scheduledDate,
                    onValueChange = { scheduledDate = it },
                    label = { Text("Date & Time") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )

                Button(
                    onClick = {
                        val booking = NonEmergencyBooking(
                            id = "neb-${System.currentTimeMillis()}",
                            patientName = activePatient?.name ?: "Rahul Sharma",
                            phone = activePatient?.phone ?: "+91 98765 43210",
                            serviceType = selectedServiceType,
                            pickupAddress = pickupAddr,
                            dropAddress = dropAddr,
                            scheduledDate = scheduledDate,
                            scheduledTime = "10:30 AM",
                            specialAssistance = listOfNotNull(
                                if (hasWheelchairRamp) "Wheelchair Ramp Support" else null,
                                if (hasOxygenSupport) "Oxygen Concentrator" else null
                            ),
                            status = "confirmed"
                        )
                        onBookRide(booking)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("confirm_non_emergency_booking_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = t.bookScheduledRide, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Existing Bookings
        Text(
            text = "CONFIRMED SCHEDULED RIDES",
            color = AccentCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bookings, key = { it.id }) { b ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${b.serviceType} • ${b.patientName}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = b.status.uppercase(), color = Color(0xFF34D399), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(text = "Drop: ${b.dropAddress}", color = SlateGray, fontSize = 11.sp)
                        Text(text = "Scheduled: ${b.scheduledDate}", color = Color(0xFF38BDF8), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
