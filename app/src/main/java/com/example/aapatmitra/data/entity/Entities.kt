package com.example.aapatmitra.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "primary_user",
    val name: String,
    val email: String,
    val phone: String,
    val age: Int = 34,
    val gender: String = "Male",
    val householdPhone: String = "+91 98765 43210",
    val bloodGroup: String = "O+",
    val address: String = "Flat 402, Lotus Towers, Golf Course Road, Sector 54, Gurugram",
    val allergies: String = "Penicillin, Dust",
    val chronicConditions: String = "Mild Hypertension",
    val emergencyNotes: String = "Keep oxygen concentrator standby. Allergic to Sulfa drugs.",
    val isLoggedIn: Boolean = false
)

@Entity(tableName = "insurance_policies")
data class InsuranceEntity(
    @PrimaryKey val id: String,
    val provider: String,
    val policyNumber: String,
    val policyHolder: String,
    val sumInsured: String,
    val expiryDate: String,
    val isVerified: Boolean = true,
    val verificationCode: String? = null,
    val tpaDeskPhone: String = "1800-425-2255",
    val coverageType: String = "Cashless",
    val networkHospitals: String = "Medanta, Fortis, Max Healthcare, Artemis",
    val notes: String? = null
)

@Entity(tableName = "angel_contacts")
data class AngelContactEntity(
    @PrimaryKey val id: String,
    val name: String,
    val relation: String,
    val phone: String,
    val notifyOnEmergency: Boolean = true,
    val lastAlertStatus: String = "idle",
    val lastAlertTime: String? = null
)

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey val id: String,
    val name: String,
    val relation: String,
    val age: Int,
    val gender: String,
    val phone: String,
    val householdAnchorPhone: String? = null,
    val bloodGroup: String,
    val address: String,
    val allergies: String = "",
    val chronicConditions: String = "",
    val emergencyNotes: String = "",
    val primaryDoctorName: String? = null,
    val primaryDoctorPhone: String? = null,
    val insuranceProvider: String? = null,
    val insurancePolicyNumber: String? = null,
    val isDefaultPatient: Boolean = false,
    val avatarColor: String = "blue"
)

@Entity(tableName = "non_emergency_bookings")
data class NonEmergencyBookingEntity(
    @PrimaryKey val id: String,
    val patientName: String,
    val phone: String,
    val serviceType: String,
    val pickupAddress: String,
    val dropAddress: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val specialAssistance: String = "",
    val status: String = "confirmed"
)
