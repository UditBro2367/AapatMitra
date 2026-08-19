package com.example.aapatmitra.model

enum class Language(val code: String, val label: String, val nativeName: String, val flag: String) {
    EN("en", "English", "English", "🇬🇧"),
    HI("hi", "Hindi", "हिन्दी", "🇮🇳"),
    BN("bn", "Bengali", "বাংলা", "🇮🇳"),
    TA("ta", "Tamil", "தமிழ்", "🇮🇳"),
    TE("te", "Telugu", "తెలుగు", "🇮🇳"),
    MR("mr", "Marathi", "मराठी", "🇮🇳"),
    ES("es", "Spanish", "Español", "🇪🇸"),
    FR("fr", "French", "Français", "🇫🇷"),
    DE("de", "German", "Deutsch", "🇩🇪");

    companion object {
        fun fromCode(code: String): Language = entries.firstOrNull { it.code == code } ?: EN
    }
}

data class FamilyMember(
    val id: String,
    val name: String,
    val relation: String, // 'Self', 'Spouse', 'Father', 'Mother', 'Son', 'Daughter', 'Brother', 'Sister', 'Grandfather', 'Grandmother', 'In-law', 'Other'
    val age: Int,
    val gender: String, // 'Male', 'Female', 'Other'
    val phone: String,
    val householdAnchorPhone: String? = null,
    val bloodGroup: String,
    val address: String,
    val allergies: List<String> = emptyList(),
    val chronicConditions: List<String> = emptyList(),
    val emergencyNotes: String = "",
    val primaryDoctorName: String? = null,
    val primaryDoctorPhone: String? = null,
    val insurances: List<InsurancePolicy> = emptyList(),
    val reports: List<MedicalReport> = emptyList(),
    val isDefaultPatient: Boolean = false,
    val avatarColor: String = "blue"
)

data class UserProfile(
    val name: String,
    val email: String = "kandpalu23@gmail.com",
    val age: Int = 34,
    val gender: String = "Male",
    val phone: String = "+91 98765 43210",
    val householdPhone: String? = "+91 98765 43210",
    val bloodGroup: String = "O+",
    val address: String = "Flat 402, Lotus Towers, Golf Course Road, Sector 54, Gurugram",
    val allergies: List<String> = emptyList(),
    val chronicConditions: List<String> = emptyList(),
    val emergencyNotes: String = "",
    val isLoggedIn: Boolean = false
)

data class AngelContact(
    val id: String,
    val name: String,
    val relation: String,
    val phone: String,
    val notifyOnEmergency: Boolean = true,
    val lastAlertStatus: String = "idle", // 'idle', 'calling', 'connected', 'notified', 'sms_sent'
    val lastAlertTime: String? = null
)

data class InsurancePolicy(
    val id: String,
    val provider: String,
    val policyNumber: String,
    val policyHolder: String,
    val sumInsured: String,
    val expiryDate: String,
    val isVerified: Boolean = true,
    val verificationCode: String? = null,
    val tpaDeskPhone: String = "1800-425-2255",
    val coverageType: String = "Cashless", // 'Cashless', 'Reimbursement', 'Both'
    val networkHospitals: List<String> = emptyList(),
    val verificationDate: String? = null,
    val notes: String? = null
)

data class MedicalReport(
    val id: String,
    val title: String,
    val date: String,
    val category: String, // 'Discharge Summary', 'Prescription', 'Lab Report', 'ECG/Cardiology', 'Radiology', 'Other'
    val doctorOrHospital: String,
    val summary: String,
    val tags: List<String> = emptyList()
)

data class Ambulance(
    val id: String,
    val name: String,
    val driverName: String,
    val driverPhone: String,
    val vehicleNo: String,
    val type: String, // 'ALS', 'BLS', 'ICU', 'PatientTransport'
    val hospitalAffiliation: String? = null,
    val isPrivateHospitalOwned: Boolean = false,
    val rating: Double = 4.8,
    val etaMinutes: Int = 5,
    val distanceKm: Double = 2.5,
    val currentLocationName: String = "Nearby Hub",
    val lat: Double = 28.4550,
    val lng: Double = 77.0810,
    val equipment: List<String> = emptyList(),
    val status: String = "available" // 'available', 'dispatched', 'stalled', 'arrived'
)

data class Hospital(
    val id: String,
    val name: String,
    val address: String,
    val distanceKm: Double,
    val emergencyPhone: String,
    val traumaLevel: String,
    val availableEmergencyBeds: Int,
    val icuBedsAvailable: Int,
    val rating: Double,
    val ambulancesOwned: List<String> = emptyList(),
    val specialties: List<String> = emptyList(),
    val lat: Double = 28.4595,
    val lng: Double = 77.0725,
    val acceptsInsurance: List<String> = emptyList()
)

data class PatientVitals(
    val bp: String = "120/80 mmHg",
    val pulse: String = "78 bpm",
    val spO2: String = "98%",
    val temperature: String = "98.6°F"
)

data class HospitalFormData(
    val patientName: String = "",
    val age: Int = 0,
    val gender: String = "",
    val bloodGroup: String = "",
    val primaryComplaint: String = "",
    val painScale: Int = 5,
    val vitals: PatientVitals = PatientVitals(),
    val selectedInsuranceId: String = "",
    val allergies: String = "",
    val attachedReportIds: List<String> = emptyList(),
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val additionalNotes: String = "",
    val submittedAt: String? = null
)

enum class DispatchStatus {
    IDLE,
    SEARCHING,
    DRIVER_ASSIGNED,
    EN_ROUTE_PICKUP,
    PATIENT_PICKED_UP,
    EN_ROUTE_HOSPITAL,
    ARRIVED_HOSPITAL,
    STALLED_REDISPATCHING
}

data class NonEmergencyBooking(
    val id: String,
    val patientName: String,
    val phone: String,
    val serviceType: String, // 'Scheduled Clinic Visit', 'Dialysis Transfer', 'Wheelchair Van', 'Inter-facility Transfer'
    val pickupAddress: String,
    val dropAddress: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val specialAssistance: List<String> = emptyList(),
    val status: String = "confirmed" // 'confirmed', 'pending', 'completed'
)

data class ChatMessage(
    val id: String,
    val sender: String, // 'user', 'assistant', 'system'
    val text: String,
    val timestamp: String,
    val quickReplies: List<String> = emptyList()
)
