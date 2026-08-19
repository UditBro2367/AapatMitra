package com.example.aapatmitra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aapatmitra.data.entity.UserProfileEntity
import com.example.aapatmitra.model.Language
import com.example.aapatmitra.model.Translations
import com.example.aapatmitra.model.UserProfile
import com.example.aapatmitra.ui.theme.*

@Composable
fun LoginScreen(
    currentLang: Language,
    existingUser: UserProfile?,
    onLoginSuccess: (UserProfileEntity) -> Unit,
    onRegisterNewUser: (
        name: String,
        email: String,
        phone: String,
        householdPhone: String,
        bloodGroup: String,
        address: String,
        allergies: String,
        chronicConditions: String,
        insuranceProvider: String,
        policyNumber: String,
        sumInsured: String
    ) -> Unit
) {
    val t = Translations.get(currentLang)
    var authMode by remember { mutableStateOf("login") } // "login" or "signup"

    // Sign in fields
    var loginEmailOrPhone by remember { mutableStateOf(existingUser?.email?.ifBlank { existingUser.phone } ?: "kandpalu23@gmail.com") }
    var loginOtpCode by remember { mutableStateOf("8421") }
    var isLoginOtpSent by remember { mutableStateOf(false) }

    // Sign up / Registration fields
    var regName by remember { mutableStateOf(existingUser?.name ?: "Rahul Sharma") }
    var regEmail by remember { mutableStateOf(existingUser?.email ?: "kandpalu23@gmail.com") }
    var regPhone by remember { mutableStateOf(existingUser?.phone ?: "+91 98765 43210") }
    var regAnchorPhone by remember { mutableStateOf(existingUser?.householdPhone ?: "+91 98765 43210") }
    var regBloodGroup by remember { mutableStateOf(existingUser?.bloodGroup ?: "O+") }
    var regAddress by remember { mutableStateOf(existingUser?.address ?: "Flat 402, Lotus Towers, Golf Course Road, Sector 54, Gurugram") }
    var regAllergies by remember { mutableStateOf(existingUser?.allergies?.joinToString(", ") ?: "Penicillin, Dust") }
    var regConditions by remember { mutableStateOf(existingUser?.chronicConditions?.joinToString(", ") ?: "Mild Hypertension") }
    var regInsuranceProvider by remember { mutableStateOf("Star Health Insurance") }
    var regPolicyNumber by remember { mutableStateOf("SH-CRIT-99214") }
    var regSumInsured by remember { mutableStateOf("₹5,00,000") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val bloodGroups = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")
    val insuranceProviders = listOf("Star Health Insurance", "HDFC ERGO Health", "Niva Bupa Health", "Ayushman Bharat PM-JAY", "Care Health", "ICICI Lombard")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EmergencyDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Emblem & USP Hero
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(CrimsonRed, CrimsonDark)
                        )
                    )
                    .border(2.dp, CrimsonRedGlow.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Emergency,
                    contentDescription = "AapatMitra Logo",
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "AapatMitra",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )

            Text(
                text = "Emergency SOS • Room SQLite Medical Database",
                color = AccentCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Auth Mode Toggle (Sign In vs Create Account)
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
                        .background(if (authMode == "login") CrimsonRed else Color.Transparent)
                        .clickable {
                            authMode = "login"
                            errorMessage = null
                        }
                        .padding(vertical = 10.dp)
                        .testTag("tab_login"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign In (लॉग इन)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (authMode == "signup") AccentCyan else Color.Transparent)
                        .clickable {
                            authMode = "signup"
                            errorMessage = null
                        }
                        .padding(vertical = 10.dp)
                        .testTag("tab_signup"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Create Account (नया खाता)",
                        color = if (authMode == "signup") Color(0xFF0F172A) else Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Auth Container
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(CardSurfaceBorder, Color(0xFF1E293B))))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (authMode == "login") {
                        // SIGN IN FLOW
                        Text(
                            text = if (!isLoginOtpSent) "Welcome Back" else "Verify Mobile OTP",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // If existing user was previously uploaded/saved to DB, show quick continue card
                        if (existingUser != null && existingUser.name.isNotBlank()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val restoredUser = UserProfileEntity(
                                            id = "primary_user",
                                            name = existingUser.name,
                                            email = existingUser.email,
                                            phone = existingUser.phone,
                                            age = existingUser.age,
                                            gender = existingUser.gender,
                                            householdPhone = existingUser.householdPhone ?: existingUser.phone,
                                            bloodGroup = existingUser.bloodGroup,
                                            address = existingUser.address,
                                            allergies = existingUser.allergies.joinToString(", "),
                                            chronicConditions = existingUser.chronicConditions.joinToString(", "),
                                            emergencyNotes = existingUser.emergencyNotes,
                                            isLoggedIn = true
                                        )
                                        onLoginSuccess(restoredUser)
                                    }
                                    .testTag("saved_profile_quick_login"),
                                color = Color(0xFF0F172A),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = EmeraldGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Saved Database Profile Found",
                                                color = EmeraldGreen,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${existingUser.name} • ${existingUser.phone}",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Email: ${existingUser.email} • Blood: ${existingUser.bloodGroup}",
                                            color = SlateGray,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val restoredUser = UserProfileEntity(
                                                id = "primary_user",
                                                name = existingUser.name,
                                                email = existingUser.email,
                                                phone = existingUser.phone,
                                                age = existingUser.age,
                                                gender = existingUser.gender,
                                                householdPhone = existingUser.householdPhone ?: existingUser.phone,
                                                bloodGroup = existingUser.bloodGroup,
                                                address = existingUser.address,
                                                allergies = existingUser.allergies.joinToString(", "),
                                                chronicConditions = existingUser.chronicConditions.joinToString(", "),
                                                emergencyNotes = existingUser.emergencyNotes,
                                                isLoggedIn = true
                                            )
                                            onLoginSuccess(restoredUser)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(text = "Continue", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        if (!isLoginOtpSent) {
                            OutlinedTextField(
                                value = loginEmailOrPhone,
                                onValueChange = { loginEmailOrPhone = it },
                                label = { Text("Email ID or Mobile Number") },
                                leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null, tint = AccentCyan) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_identifier_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = CardSurfaceBorder
                                ),
                                singleLine = true
                            )
                        } else {
                            OutlinedTextField(
                                value = loginOtpCode,
                                onValueChange = { if (it.length <= 6) loginOtpCode = it },
                                label = { Text("4-Digit Verification Code") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldGreen) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_otp_input"),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = EmeraldGreen,
                                    unfocusedBorderColor = CardSurfaceBorder
                                ),
                                singleLine = true
                            )

                            Text(
                                text = "💡 Demo OTP: 8421",
                                color = EmeraldGreen,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = {
                                if (!isLoginOtpSent) {
                                    if (loginEmailOrPhone.isBlank()) {
                                        errorMessage = "Please enter valid email or phone number"
                                    } else {
                                        errorMessage = null
                                        isLoginOtpSent = true
                                    }
                                } else {
                                    if (loginOtpCode.length >= 4) {
                                        val userEntity = UserProfileEntity(
                                            id = "primary_user",
                                            name = existingUser?.name?.ifBlank { "Rahul Sharma" } ?: "Rahul Sharma",
                                            email = if (loginEmailOrPhone.contains("@")) loginEmailOrPhone.trim() else existingUser?.email ?: "kandpalu23@gmail.com",
                                            phone = if (!loginEmailOrPhone.contains("@")) loginEmailOrPhone.trim() else existingUser?.phone ?: "+91 98765 43210",
                                            age = existingUser?.age ?: 34,
                                            gender = existingUser?.gender ?: "Male",
                                            householdPhone = existingUser?.householdPhone ?: "+91 98765 43210",
                                            bloodGroup = existingUser?.bloodGroup ?: "O+",
                                            address = existingUser?.address ?: "Flat 402, Lotus Towers, Golf Course Road, Sector 54, Gurugram",
                                            allergies = existingUser?.allergies?.joinToString(", ") ?: "Penicillin, Dust",
                                            chronicConditions = existingUser?.chronicConditions?.joinToString(", ") ?: "Mild Hypertension",
                                            emergencyNotes = existingUser?.emergencyNotes ?: "Keep oxygen standby. Cashless insurance linked.",
                                            isLoggedIn = true
                                        )
                                        onLoginSuccess(userEntity)
                                    } else {
                                        errorMessage = "Please enter valid 4-digit code (8421)"
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (!isLoginOtpSent) "Send OTP & Proceed" else "Verify & Sign In",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                    } else {
                        // SIGN UP / REGISTRATION FLOW
                        Text(
                            text = "New User Registration",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Enter patient details, guardian anchor, and insurance to save on main database",
                            color = SlateGray,
                            fontSize = 12.sp
                        )

                        // Name
                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text("Patient Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AccentCyan) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = CardSurfaceBorder
                            ),
                            singleLine = true
                        )

                        // Email
                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Email ID (For Cashless TPA Claims)") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AccentCyan) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_email_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = CardSurfaceBorder
                            ),
                            singleLine = true
                        )

                        // Phone
                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { regPhone = it },
                            label = { Text("Primary Phone Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = AccentCyan) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_phone_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = CardSurfaceBorder
                            ),
                            singleLine = true
                        )

                        // Guardian Anchor Phone
                        OutlinedTextField(
                            value = regAnchorPhone,
                            onValueChange = { regAnchorPhone = it },
                            label = { Text("Guardian / Emergency Anchor Phone") },
                            leadingIcon = { Icon(Icons.Default.FamilyRestroom, contentDescription = null, tint = AmberWarning) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_anchor_phone_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AmberWarning,
                                unfocusedBorderColor = CardSurfaceBorder
                            ),
                            singleLine = true
                        )

                        // Blood Group Selection
                        Column {
                            Text(
                                text = "Blood Group:",
                                color = SlateGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                bloodGroups.take(4).forEach { bg ->
                                    val isSel = regBloodGroup == bg
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) CrimsonRed else Color(0xFF1E293B))
                                            .clickable { regBloodGroup = bg }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = bg, color = Color.White, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                bloodGroups.drop(4).forEach { bg ->
                                    val isSel = regBloodGroup == bg
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) CrimsonRed else Color(0xFF1E293B))
                                            .clickable { regBloodGroup = bg }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = bg, color = Color.White, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }
                        }

                        // Address
                        OutlinedTextField(
                            value = regAddress,
                            onValueChange = { regAddress = it },
                            label = { Text("Home / Pickup Address") },
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = AccentCyan) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("signup_address_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = CardSurfaceBorder
                            ),
                            maxLines = 2
                        )

                        // Insurance Provider Chips
                        Column {
                            Text(
                                text = "Health Insurance Provider:",
                                color = SlateGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                insuranceProviders.forEach { provider ->
                                    val isSel = regInsuranceProvider == provider
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) Color(0xFF0284C7) else Color(0xFF1E293B))
                                            .clickable { regInsuranceProvider = provider }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(text = provider, color = Color.White, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }
                        }

                        // Policy Number & Sum Insured
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = regPolicyNumber,
                                onValueChange = { regPolicyNumber = it },
                                label = { Text("Policy Number") },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("signup_policy_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = CardSurfaceBorder
                                ),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = regSumInsured,
                                onValueChange = { regSumInsured = it },
                                label = { Text("Sum Insured") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("signup_sum_insured_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = CardSurfaceBorder
                                ),
                                singleLine = true
                            )
                        }

                        // Allergies & Chronic Conditions
                        OutlinedTextField(
                            value = regAllergies,
                            onValueChange = { regAllergies = it },
                            label = { Text("Allergies (e.g. Penicillin, Sulfa, Dust)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = CardSurfaceBorder
                            ),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (regName.isBlank() || regPhone.isBlank()) {
                                    errorMessage = "Please enter patient name and mobile number"
                                } else {
                                    errorMessage = null
                                    onRegisterNewUser(
                                        regName,
                                        regEmail,
                                        regPhone,
                                        regAnchorPhone,
                                        regBloodGroup,
                                        regAddress,
                                        regAllergies,
                                        regConditions,
                                        regInsuranceProvider,
                                        regPolicyNumber,
                                        regSumInsured
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("signup_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save to Database & Launch App",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = CrimsonRed,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = SlateGray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Encrypted On-Device Room SQLite Database",
                    color = SlateGray,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
