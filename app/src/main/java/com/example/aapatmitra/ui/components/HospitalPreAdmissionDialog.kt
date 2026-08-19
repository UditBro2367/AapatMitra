package com.example.aapatmitra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import com.example.aapatmitra.model.*
import com.example.aapatmitra.ui.theme.*

@Composable
fun HospitalPreAdmissionDialog(
    initialData: HospitalFormData,
    onDismiss: () -> Unit,
    onSubmit: (HospitalFormData) -> Unit,
    availableInsurances: List<InsurancePolicy>,
    availableReports: List<MedicalReport>
) {
    var patientName by remember { mutableStateOf(initialData.patientName) }
    var ageText by remember { mutableStateOf(if (initialData.age > 0) initialData.age.toString() else "42") }
    var gender by remember { mutableStateOf(initialData.gender) }
    var bloodGroup by remember { mutableStateOf(initialData.bloodGroup) }
    var primaryComplaint by remember { mutableStateOf(initialData.primaryComplaint.ifBlank { "Severe chest tightness & breathlessness" }) }
    var painScale by remember { mutableFloatStateOf(initialData.painScale.toFloat()) }
    var bp by remember { mutableStateOf(initialData.vitals.bp) }
    var pulse by remember { mutableStateOf(initialData.vitals.pulse) }
    var spO2 by remember { mutableStateOf(initialData.vitals.spO2) }
    var temp by remember { mutableStateOf(initialData.vitals.temperature) }
    var allergies by remember { mutableStateOf(initialData.allergies) }
    var additionalNotes by remember { mutableStateOf(initialData.additionalNotes) }
    var selectedInsuranceId by remember {
        mutableStateOf(initialData.selectedInsuranceId ?: availableInsurances.firstOrNull()?.id ?: "")
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp)),
            color = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = CrimsonRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ER Pre-Admission Triage",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SlateGray)
                    }
                }

                HorizontalDivider(color = CardSurfaceBorder, modifier = Modifier.padding(vertical = 8.dp))

                // Scrollable Form Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Patient Basic Info
                    OutlinedTextField(
                        value = patientName,
                        onValueChange = { patientName = it },
                        label = { Text("Patient Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("triage_patient_name"),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = ageText,
                            onValueChange = { ageText = it },
                            label = { Text("Age") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        OutlinedTextField(
                            value = bloodGroup,
                            onValueChange = { bloodGroup = it },
                            label = { Text("Blood Group") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }

                    // 🛡️ INSURANCE SELECTION & OPTION SECTION IN THE FORM
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Insurance & Cashless Pre-Approval", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            if (availableInsurances.isNotEmpty()) {
                                availableInsurances.forEach { policy ->
                                    val isSelected = selectedInsuranceId == policy.id
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) Color(0xFF065F46) else Color(0xFF0F172A))
                                            .clickable { selectedInsuranceId = policy.id }
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(policy.provider, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("No: ${policy.policyNumber} • Cover: ${policy.sumInsured}", color = SlateGray, fontSize = 10.sp)
                                        }
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedInsuranceId = policy.id },
                                            colors = RadioButtonDefaults.colors(selectedColor = EmeraldGreen)
                                        )
                                    }
                                }
                            } else {
                                Text("Star Health Policy: SH-FAMILY-9921 (Cashless Pre-Auth Active)", color = EmeraldGreen, fontSize = 11.sp)
                            }
                        }
                    }

                    // Primary Emergency Complaint
                    OutlinedTextField(
                        value = primaryComplaint,
                        onValueChange = { primaryComplaint = it },
                        label = { Text("Chief Complaint / Symptoms") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )

                    // Pain Scale Slider (1-10)
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Pain Severity Scale", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "${painScale.toInt()} / 10", color = CrimsonGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = painScale,
                            onValueChange = { painScale = it },
                            valueRange = 1f..10f,
                            steps = 8,
                            colors = SliderDefaults.colors(thumbColor = CrimsonRed, activeTrackColor = CrimsonRed)
                        )
                    }

                    // Live Vitals
                    Text(
                        text = "LIVE VITALS TRANSMISSION",
                        color = AccentCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = bp,
                            onValueChange = { bp = it },
                            label = { Text("Blood Pressure") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        OutlinedTextField(
                            value = pulse,
                            onValueChange = { pulse = it },
                            label = { Text("Heart Rate") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = spO2,
                            onValueChange = { spO2 = it },
                            label = { Text("SpO2 %") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        OutlinedTextField(
                            value = temp,
                            onValueChange = { temp = it },
                            label = { Text("Temperature") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }

                    // Allergies & Notes
                    OutlinedTextField(
                        value = allergies,
                        onValueChange = { allergies = it },
                        label = { Text("Known Allergies & Contraindications") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )

                    OutlinedTextField(
                        value = additionalNotes,
                        onValueChange = { additionalNotes = it },
                        label = { Text("Special ER Instructions / Doctor Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Submit Button
                Button(
                    onClick = {
                        val result = HospitalFormData(
                            patientName = patientName,
                            age = ageText.toIntOrNull() ?: 0,
                            gender = gender,
                            bloodGroup = bloodGroup,
                            primaryComplaint = primaryComplaint,
                            painScale = painScale.toInt(),
                            vitals = PatientVitals(bp = bp, pulse = pulse, spO2 = spO2, temperature = temp),
                            allergies = allergies,
                            additionalNotes = additionalNotes,
                            selectedInsuranceId = selectedInsuranceId
                        )
                        onSubmit(result)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_triage_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Transmit Triage to ER Trauma Desk", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
