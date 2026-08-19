package com.example.aapatmitra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.window.Dialog
import com.example.aapatmitra.model.InsurancePolicy
import com.example.aapatmitra.ui.theme.*

@Composable
fun InsuranceVaultDialog(
    insurances: List<InsurancePolicy>,
    verifyingPolicyId: String?,
    onVerifyPolicy: (InsurancePolicy) -> Unit,
    onVerifyAll: () -> Unit,
    onDismiss: () -> Unit,
    onOpenChatbot: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
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
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cashless Insurance Database",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SlateGray)
                    }
                }

                Divider(color = CardSurfaceBorder, modifier = Modifier.padding(vertical = 8.dp))

                // Verify All Master Button
                Button(
                    onClick = onVerifyAll,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("verify_all_insurances_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = verifyingPolicyId == null
                ) {
                    if (verifyingPolicyId == "ALL") {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verifying via IRDAI Registry...", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    } else {
                        Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verify All Insurances in Database", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Policies List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(insurances, key = { it.id }) { policy ->
                        val isThisPolicyVerifying = verifyingPolicyId == policy.id
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                            shape = RoundedCornerShape(14.dp),
                            border = if (policy.isVerified) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)) else null
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = policy.provider,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Policy: ${policy.policyNumber}",
                                            color = SlateGray,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (policy.isVerified) Color(0xFF10B981).copy(alpha = 0.2f)
                                                else Color(0xFFFBBF24).copy(alpha = 0.2f)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = if (policy.isVerified) "✓ 100% GENUINE" else "UNVERIFIED",
                                            color = if (policy.isVerified) Color(0xFF34D399) else Color(0xFFFBBF24),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Sum Insured: ${policy.sumInsured}", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = "Expires: ${policy.expiryDate}", color = SlateGray, fontSize = 11.sp)
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "24x7 TPA Desk: ${policy.tpaDeskPhone}",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp
                                )

                                if (policy.networkHospitals.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Network: ${policy.networkHospitals.joinToString(", ")}",
                                        color = SlateGray,
                                        fontSize = 10.sp,
                                        maxLines = 2
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // VERIFICATION BUTTON FOR THIS SPECIFIC INSURANCE
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = { onVerifyPolicy(policy) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(38.dp)
                                            .testTag("verify_policy_button_${policy.id}"),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = if (policy.isVerified) Color(0xFF34D399) else Color(0xFF38BDF8)
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = androidx.compose.ui.graphics.SolidColor(
                                                if (policy.isVerified) Color(0xFF10B981) else Color(0xFF0284C7)
                                            )
                                        ),
                                        enabled = verifyingPolicyId == null
                                    ) {
                                        if (isThisPolicyVerifying) {
                                            CircularProgressIndicator(
                                                color = Color.White,
                                                modifier = Modifier.size(14.dp),
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "Checking IIB Registry...", fontSize = 11.sp)
                                        } else {
                                            Icon(
                                                imageVector = if (policy.isVerified) Icons.Default.Verified else Icons.Default.Shield,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (policy.isVerified) "View IRDAI Genuine Certificate" else "Verify Insurance Authenticity",
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

                Spacer(modifier = Modifier.height(8.dp))

                // Chatbot shortcut button
                Button(
                    onClick = {
                        onDismiss()
                        onOpenChatbot()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("open_insurance_ai_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ask Insurance AI Claim Companion", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
