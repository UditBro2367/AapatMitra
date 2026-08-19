package com.example.aapatmitra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.aapatmitra.model.*
import com.example.aapatmitra.ui.theme.*

@Composable
fun FamilyPortalView(
    currentLang: Language,
    familyMembers: List<FamilyMember>,
    activePatient: FamilyMember?,
    onSelectPatientForEmergency: (FamilyMember) -> Unit,
    onSaveMember: (FamilyMember) -> Unit,
    onDeleteMember: (String) -> Unit
) {
    val t = Translations.get(currentLang)
    var searchQuery by remember { mutableStateOf("") }
    var showMemberEditor by remember { mutableStateOf(false) }
    var editingMember by remember { mutableStateOf<FamilyMember?>(null) }

    val filteredMembers = familyMembers.filter { member ->
        member.name.contains(searchQuery, ignoreCase = true) ||
        member.relation.contains(searchQuery, ignoreCase = true) ||
        member.phone.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Portal Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = t.familyCircleTitle,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Household Anchor: +91 98765 43210 (Room DB Synced)",
                    color = AccentCyan,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            IconButton(
                onClick = {
                    editingMember = null
                    showMemberEditor = true
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF3B82F6))
                    .testTag("add_family_member_header_button")
            ) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Add Member", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(t.searchFamilyPlaceholder, fontSize = 12.sp, color = SlateGray) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = SlateGray) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("family_search_field"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFF3B82F6)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Family Members List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredMembers, key = { it.id }) { member ->
                val isCurrentSosPatient = activePatient?.id == member.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("family_card_${member.id}"),
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
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (member.relation.lowercase()) {
                                                "self" -> Color(0xFF3B82F6)
                                                "spouse" -> Color(0xFFEC4899)
                                                "father", "mother" -> Color(0xFFF59E0B)
                                                else -> Color(0xFF8B5CF6)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = member.name.firstOrNull()?.toString() ?: "F",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = member.name,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF1E293B))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = member.relation,
                                                color = Color(0xFF94A3B8),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${member.age} yrs • ${member.gender} • Blood: ${member.bloodGroup}",
                                        color = SlateGray,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Edit button
                            IconButton(onClick = {
                                editingMember = member
                                showMemberEditor = true
                            }) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = SlateGray, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Phone & Linked Insurance Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Phone: ${member.phone}", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                            val ins = member.insurances.firstOrNull()
                            if (ins != null) {
                                Text(
                                    text = "🛡️ ${ins.provider}",
                                    color = EmeraldGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Medical highlights
                        if (member.chronicConditions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Chronic: ${member.chronicConditions.joinToString(", ")}",
                                color = Color(0xFFFBBF24),
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }

                        if (member.allergies.isNotEmpty()) {
                            Text(
                                text = "Allergies: ${member.allergies.joinToString(", ")}",
                                color = Color(0xFFF87171),
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons: SOS For Member vs Insurance
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onSelectPatientForEmergency(member) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("sos_member_btn_${member.id}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCurrentSosPatient) CrimsonDark else CrimsonRed
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isCurrentSosPatient) "Selected SOS Patient ✓" else "Set as SOS Patient",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (member.insurances.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF10B981).copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "${member.insurances.size} Policy Active",
                                        color = Color(0xFF34D399),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Member Dialog Form with Insurance Options
    if (showMemberEditor) {
        FamilyMemberEditorDialog(
            member = editingMember,
            onDismiss = { showMemberEditor = false },
            onSave = { updatedMember ->
                onSaveMember(updatedMember)
                showMemberEditor = false
            },
            onDelete = { id ->
                onDeleteMember(id)
                showMemberEditor = false
            }
        )
    }
}

@Composable
fun FamilyMemberEditorDialog(
    member: FamilyMember?,
    onDismiss: () -> Unit,
    onSave: (FamilyMember) -> Unit,
    onDelete: (String) -> Unit
) {
    var name by remember { mutableStateOf(member?.name ?: "") }
    var relation by remember { mutableStateOf(member?.relation ?: "Spouse") }
    var ageText by remember { mutableStateOf(if (member != null && member.age > 0) member.age.toString() else "35") }
    var gender by remember { mutableStateOf(member?.gender ?: "Male") }
    var phone by remember { mutableStateOf(member?.phone ?: "+91 ") }
    var bloodGroup by remember { mutableStateOf(member?.bloodGroup ?: "O+") }
    var allergiesText by remember { mutableStateOf(member?.allergies?.joinToString(", ") ?: "") }
    var chronicText by remember { mutableStateOf(member?.chronicConditions?.joinToString(", ") ?: "") }
    var emergencyNotes by remember { mutableStateOf(member?.emergencyNotes ?: "") }

    // Insurance Form Fields
    var insuranceProvider by remember {
        mutableStateOf(member?.insurances?.firstOrNull()?.provider ?: "Star Health & Allied Insurance")
    }
    var insurancePolicyNo by remember {
        mutableStateOf(member?.insurances?.firstOrNull()?.policyNumber ?: "SH-FAM-998234")
    }
    var sumInsured by remember {
        mutableStateOf(member?.insurances?.firstOrNull()?.sumInsured ?: "₹ 15,00,000")
    }
    var isCashlessVerified by remember {
        mutableStateOf(member?.insurances?.firstOrNull()?.isVerified ?: true)
    }

    val relations = listOf("Self", "Spouse", "Father", "Mother", "Son", "Daughter", "Brother", "Sister")
    val popularInsurers = listOf("Star Health", "HDFC ERGO", "Niva Bupa", "Care Health", "PM-JAY Ayushman", "ICICI Lombard")

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
                    Text(
                        text = if (member == null) "Add Family Member" else "Edit Family Member",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SlateGray)
                    }
                }

                HorizontalDivider(color = CardSurfaceBorder, modifier = Modifier.padding(vertical = 6.dp))

                // Scrollable Form Fields
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("family_form_name"),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )

                    // Relation Row
                    Text(text = "Relation", color = SlateGray, fontSize = 11.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        relations.take(4).forEach { rel ->
                            val isSelected = relation.equals(rel, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF3B82F6) else Color(0xFF1E293B))
                                    .clickable { relation = rel }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(text = rel, color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = ageText,
                            onValueChange = { ageText = it },
                            label = { Text("Age (Yrs)") },
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

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Member Phone (+91)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )

                    // 🛡️ INSURANCE OPTION SECTION
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF10B981))))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Insurance & Cashless Details", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            // Quick provider chips
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                popularInsurers.take(3).forEach { prov ->
                                    val isSelected = insuranceProvider.contains(prov, ignoreCase = true)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) EmeraldGreen else Color(0xFF0F172A))
                                            .clickable { insuranceProvider = prov }
                                            .padding(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = prov, color = Color.White, fontSize = 10.sp)
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = insuranceProvider,
                                onValueChange = { insuranceProvider = it },
                                label = { Text("Insurance Provider (बीमा कंपनी)") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = insurancePolicyNo,
                                    onValueChange = { insurancePolicyNo = it },
                                    label = { Text("Policy Number") },
                                    modifier = Modifier.weight(1.2f),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                                )
                                OutlinedTextField(
                                    value = sumInsured,
                                    onValueChange = { sumInsured = it },
                                    label = { Text("Sum Insured") },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = chronicText,
                        onValueChange = { chronicText = it },
                        label = { Text("Chronic Conditions (Hypertension, Diabetes)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )

                    OutlinedTextField(
                        value = allergiesText,
                        onValueChange = { allergiesText = it },
                        label = { Text("Allergies (Penicillin, Sulfa, Dust)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )

                    OutlinedTextField(
                        value = emergencyNotes,
                        onValueChange = { emergencyNotes = it },
                        label = { Text("Emergency Triage / Doctor Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (member != null) {
                        TextButton(onClick = { onDelete(member.id) }) {
                            Text("Delete", color = Color(0xFFF87171))
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Row {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel", color = SlateGray)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    val linkedInsurance = if (insuranceProvider.isNotBlank()) {
                                        listOf(
                                            InsurancePolicy(
                                                id = "ins-${System.currentTimeMillis()}",
                                                provider = insuranceProvider,
                                                policyNumber = insurancePolicyNo,
                                                policyHolder = name,
                                                sumInsured = sumInsured,
                                                expiryDate = "2027-12-31",
                                                isVerified = isCashlessVerified,
                                                tpaDeskPhone = "1800-425-2255",
                                                coverageType = "Cashless"
                                            )
                                        )
                                    } else emptyList()

                                    val newMember = FamilyMember(
                                        id = member?.id ?: "fam-${System.currentTimeMillis()}",
                                        name = name,
                                        relation = relation,
                                        age = ageText.toIntOrNull() ?: 30,
                                        gender = gender,
                                        phone = phone,
                                        householdAnchorPhone = "+91 98765 43210",
                                        bloodGroup = bloodGroup,
                                        allergies = if (allergiesText.isBlank()) emptyList() else allergiesText.split(",").map { it.trim() },
                                        chronicConditions = if (chronicText.isBlank()) emptyList() else chronicText.split(",").map { it.trim() },
                                        emergencyNotes = emergencyNotes,
                                        address = member?.address ?: "Flat 402, Lotus Towers, Golf Course Road, Sector 54, Gurugram",
                                        insurances = linkedInsurance,
                                        reports = member?.reports ?: emptyList()
                                    )
                                    onSave(newMember)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                        ) {
                            Text("Save Member")
                        }
                    }
                }
            }
        }
    }
}
