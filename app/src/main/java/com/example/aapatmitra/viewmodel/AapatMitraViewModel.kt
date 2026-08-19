package com.example.aapatmitra.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aapatmitra.data.db.AapatMitraDatabase
import com.example.aapatmitra.data.entity.UserProfileEntity
import com.example.aapatmitra.data.repository.AapatMitraRepository
import com.example.aapatmitra.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AapatMitraViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AapatMitraDatabase.getDatabase(application)
    private val repository = AapatMitraRepository(
        userDao = db.userDao(),
        insuranceDao = db.insuranceDao(),
        angelContactDao = db.angelContactDao(),
        familyMemberDao = db.familyMemberDao(),
        nonEmergencyBookingDao = db.nonEmergencyBookingDao()
    )

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentLang = MutableStateFlow(Language.EN)
    val currentLang: StateFlow<Language> = _currentLang.asStateFlow()

    private val _activeNavTab = MutableStateFlow("main") // "main", "family", "profile"
    val activeNavTab: StateFlow<String> = _activeNavTab.asStateFlow()

    private val _transportMode = MutableStateFlow("emergency") // "emergency", "non-emergency"
    val transportMode: StateFlow<String> = _transportMode.asStateFlow()

    private val _userProfile = MutableStateFlow(MockData.initialUserProfile)
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _familyMembers = MutableStateFlow(MockData.initialFamilyMembers)
    val familyMembers: StateFlow<List<FamilyMember>> = _familyMembers.asStateFlow()

    private val _activePatient = MutableStateFlow<FamilyMember?>(MockData.initialFamilyMembers.firstOrNull { it.isDefaultPatient } ?: MockData.initialFamilyMembers.firstOrNull())
    val activePatient: StateFlow<FamilyMember?> = _activePatient.asStateFlow()

    private val _angelContacts = MutableStateFlow(MockData.initialAngelContacts)
    val angelContacts: StateFlow<List<AngelContact>> = _angelContacts.asStateFlow()

    private val _insurances = MutableStateFlow(MockData.initialInsurances)
    val insurances: StateFlow<List<InsurancePolicy>> = _insurances.asStateFlow()

    private val _reports = MutableStateFlow(MockData.initialReports)
    val reports: StateFlow<List<MedicalReport>> = _reports.asStateFlow()

    private val _pickupAddress = MutableStateFlow("Flat 402, Lotus Towers, Golf Course Road, Sector 54, Gurugram")
    val pickupAddress: StateFlow<String> = _pickupAddress.asStateFlow()

    private val _selectedHospital = MutableStateFlow(MockData.hospitals[0])
    val selectedHospital: StateFlow<Hospital> = _selectedHospital.asStateFlow()

    private val _hospitalsList = MutableStateFlow(MockData.hospitals)
    val hospitalsList: StateFlow<List<Hospital>> = _hospitalsList.asStateFlow()

    private val _ambulancesList = MutableStateFlow(MockData.ambulances)
    val ambulancesList: StateFlow<List<Ambulance>> = _ambulancesList.asStateFlow()

    // Dispatch & Emergency State
    private val _dispatchStatus = MutableStateFlow(DispatchStatus.IDLE)
    val dispatchStatus: StateFlow<DispatchStatus> = _dispatchStatus.asStateFlow()

    private val _isEmergencyActive = MutableStateFlow(false)
    val isEmergencyActive: StateFlow<Boolean> = _isEmergencyActive.asStateFlow()

    private val _activeAmbulance = MutableStateFlow<Ambulance?>(null)
    val activeAmbulance: StateFlow<Ambulance?> = _activeAmbulance.asStateFlow()

    private val _currentTierIndex = MutableStateFlow(0)
    val currentTierIndex: StateFlow<Int> = _currentTierIndex.asStateFlow()

    private val _searchRadiusKm = MutableStateFlow(2.5)
    val searchRadiusKm: StateFlow<Double> = _searchRadiusKm.asStateFlow()

    private val _countdownSeconds = MutableStateFlow(60)
    val countdownSeconds: StateFlow<Int> = _countdownSeconds.asStateFlow()

    private val _isFastDemoMode = MutableStateFlow(false)
    val isFastDemoMode: StateFlow<Boolean> = _isFastDemoMode.asStateFlow()

    private val _isPatientPickedUp = MutableStateFlow(false)
    val isPatientPickedUp: StateFlow<Boolean> = _isPatientPickedUp.asStateFlow()

    private val _isStalled = MutableStateFlow(false)
    val isStalled: StateFlow<Boolean> = _isStalled.asStateFlow()

    private val _routeProgressPercent = MutableStateFlow(15)
    val routeProgressPercent: StateFlow<Int> = _routeProgressPercent.asStateFlow()

    private val _remainingEtaMinutes = MutableStateFlow(4)
    val remainingEtaMinutes: StateFlow<Int> = _remainingEtaMinutes.asStateFlow()

    private val _remainingDistanceKm = MutableStateFlow(1.8)
    val remainingDistanceKm: StateFlow<Double> = _remainingDistanceKm.asStateFlow()

    private val _hospitalFormSubmitted = MutableStateFlow(false)
    val hospitalFormSubmitted: StateFlow<Boolean> = _hospitalFormSubmitted.asStateFlow()

    private val _hospitalFormData = MutableStateFlow(HospitalFormData())
    val hospitalFormData: StateFlow<HospitalFormData> = _hospitalFormData.asStateFlow()

    private val _nonEmergencyBookings = MutableStateFlow(MockData.initialNonEmergencyBookings)
    val nonEmergencyBookings: StateFlow<List<NonEmergencyBooking>> = _nonEmergencyBookings.asStateFlow()

    // Active Dialog Overlays
    val isAngelModalOpen = MutableStateFlow(false)
    val isInsuranceVaultOpen = MutableStateFlow(false)
    val isInsuranceChatOpen = MutableStateFlow(false)
    val isMedicalReportOpen = MutableStateFlow(false)
    val isHospitalAdmissionModalOpen = MutableStateFlow(false)
    val isDriverCallModalOpen = MutableStateFlow(false)
    val isLanguagePickerOpen = MutableStateFlow(false)
    val isAddFamilyMemberOpen = MutableStateFlow(false)
    val isHospitalPickerOpen = MutableStateFlow(false)
    val editingFamilyMember = MutableStateFlow<FamilyMember?>(null)

    // Insurance Verification Result State
    private val _verifyingPolicyId = MutableStateFlow<String?>(null)
    val verifyingPolicyId: StateFlow<String?> = _verifyingPolicyId.asStateFlow()

    private val _verificationCertificatePolicy = MutableStateFlow<InsurancePolicy?>(null)
    val verificationCertificatePolicy: StateFlow<InsurancePolicy?> = _verificationCertificatePolicy.asStateFlow()

    // Chatbot state
    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage("1", "assistant", "Hello! I am your AapatMitra Emergency Insurance & TPA Assistant. How can I assist with your cashless claim or network hospital verification today?", "Now", listOf("Check Cashless Network", "Pre-Authorization Steps", "Required Documents"))
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private var searchTimerJob: Job? = null
    private var trackingSimJob: Job? = null

    init {
        // Initialize Room Database & observe persisted flows
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }

        viewModelScope.launch {
            repository.userProfile.collectLatest { entity ->
                if (entity != null) {
                    _userProfile.value = UserProfile(
                        name = entity.name,
                        email = entity.email,
                        age = entity.age,
                        gender = entity.gender,
                        phone = entity.phone,
                        householdPhone = entity.householdPhone,
                        bloodGroup = entity.bloodGroup,
                        address = entity.address,
                        allergies = if (entity.allergies.isBlank()) emptyList() else entity.allergies.split(",").map { it.trim() },
                        chronicConditions = if (entity.chronicConditions.isBlank()) emptyList() else entity.chronicConditions.split(",").map { it.trim() },
                        emergencyNotes = entity.emergencyNotes,
                        isLoggedIn = entity.isLoggedIn
                    )
                    _isLoggedIn.value = entity.isLoggedIn
                    if (entity.address.isNotBlank()) {
                        _pickupAddress.value = entity.address
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.familyMembers.collectLatest { list ->
                if (list.isNotEmpty()) {
                    _familyMembers.value = list
                    if (_activePatient.value == null || !list.any { it.id == _activePatient.value?.id }) {
                        _activePatient.value = list.firstOrNull { it.isDefaultPatient } ?: list.firstOrNull()
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.insurances.collectLatest { list ->
                if (list.isNotEmpty()) {
                    _insurances.value = list
                }
            }
        }

        viewModelScope.launch {
            repository.angelContacts.collectLatest { list ->
                if (list.isNotEmpty()) {
                    _angelContacts.value = list
                }
            }
        }

        viewModelScope.launch {
            repository.nonEmergencyBookings.collectLatest { list ->
                if (list.isNotEmpty()) {
                    _nonEmergencyBookings.value = list
                }
            }
        }
    }

    fun loginUser(userEntity: UserProfileEntity) {
        viewModelScope.launch {
            repository.saveUserProfile(userEntity.copy(isLoggedIn = true))
            _pickupAddress.value = userEntity.address.ifBlank { _pickupAddress.value }
            _isLoggedIn.value = true
        }
    }

    fun registerNewUser(
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
    ) {
        viewModelScope.launch {
            val userEntity = UserProfileEntity(
                id = "primary_user",
                name = name.trim(),
                email = email.trim(),
                phone = phone.trim(),
                age = 32,
                gender = "Primary",
                householdPhone = householdPhone.trim().ifBlank { phone.trim() },
                bloodGroup = bloodGroup.trim(),
                address = address.trim(),
                allergies = allergies.trim(),
                chronicConditions = chronicConditions.trim(),
                emergencyNotes = "Emergency Anchor: $householdPhone. Insurance: $insuranceProvider ($policyNumber)",
                isLoggedIn = true
            )
            repository.saveUserProfile(userEntity)

            val createdInsurances = if (insuranceProvider.isNotBlank() && policyNumber.isNotBlank()) {
                val ins = InsurancePolicy(
                    id = "ins-${System.currentTimeMillis()}",
                    provider = insuranceProvider,
                    policyNumber = policyNumber,
                    policyHolder = name.trim(),
                    sumInsured = sumInsured.ifBlank { "₹5,00,000" },
                    expiryDate = "Dec 2027",
                    isVerified = true,
                    verificationCode = "CASHLESS-AUTH-${(1000..9999).random()}",
                    tpaDeskPhone = "1800-425-2255",
                    coverageType = "Comprehensive Emergency & Critical Care",
                    networkHospitals = listOf("Medanta The Medicity", "Fortis Memorial Research Institute", "Max Super Speciality Hospital", "Artemis Hospital"),
                    notes = "Cashless Pre-Authorized for emergency triage"
                )
                repository.saveInsurance(ins)
                listOf(ins)
            } else {
                emptyList()
            }

            val primaryMember = FamilyMember(
                id = "fam_primary_self",
                name = name.trim(),
                relation = "Self",
                age = 32,
                gender = "Primary",
                bloodGroup = bloodGroup.trim(),
                address = address.trim(),
                phone = phone.trim(),
                allergies = if (allergies.isBlank()) emptyList() else allergies.split(",").map { it.trim() },
                chronicConditions = if (chronicConditions.isBlank()) emptyList() else chronicConditions.split(",").map { it.trim() },
                insurances = createdInsurances,
                isDefaultPatient = true
            )
            repository.saveFamilyMember(primaryMember)

            if (householdPhone.isNotBlank()) {
                val angel = AngelContact(
                    id = "angel_primary_guardian",
                    name = "Emergency Guardian Anchor",
                    relation = "Family Hub Anchor",
                    phone = householdPhone.trim(),
                    notifyOnEmergency = true,
                    lastAlertStatus = "Connected & Active",
                    lastAlertTime = "Live Synced"
                )
                repository.saveAngelContact(angel)
            }

            _pickupAddress.value = address.trim()
            _activePatient.value = primaryMember
            _isLoggedIn.value = true
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.setLoginStatus(false)
            _isLoggedIn.value = false
        }
    }

    fun setLanguage(lang: Language) {
        _currentLang.value = lang
    }

    fun setActiveNavTab(tab: String) {
        _activeNavTab.value = tab
    }

    fun setTransportMode(mode: String) {
        _transportMode.value = mode
    }

    fun setPickupAddress(addr: String) {
        _pickupAddress.value = addr
    }

    fun setSelectedHospital(hospital: Hospital) {
        _selectedHospital.value = hospital
    }

    fun toggleFastDemoMode() {
        _isFastDemoMode.value = !_isFastDemoMode.value
    }

    fun selectPatient(member: FamilyMember) {
        _activePatient.value = member
        if (member.address.isNotBlank()) {
            _pickupAddress.value = member.address
        }
    }

    fun startEmergencySearch() {
        _dispatchStatus.value = DispatchStatus.SEARCHING
        _currentTierIndex.value = 0
        _searchRadiusKm.value = 2.5
        _countdownSeconds.value = if (_isFastDemoMode.value) 5 else 60
        _isPatientPickedUp.value = false

        searchTimerJob?.cancel()
        searchTimerJob = viewModelScope.launch {
            val tier1Duration = if (_isFastDemoMode.value) 5 else 60
            for (sec in tier1Duration downTo 1) {
                _countdownSeconds.value = sec
                delay(1000)
            }

            // Perform Dispatch Algorithm for Tier 1 (2.5 km)
            val result = DispatchAlgorithm.findBestAmbulance(
                targetHospital = _selectedHospital.value,
                currentRadiusKm = 2.5,
                availableAmbulances = _ambulancesList.value
            )

            if (result.ambulance != null) {
                // Auto-dispatch without user confirmation
                confirmDispatch(result.ambulance)
            } else {
                // Expand to Tier 2 (5.0 km)
                _currentTierIndex.value = 1
                _searchRadiusKm.value = 5.0
                val tier2Duration = if (_isFastDemoMode.value) 3 else 30
                for (sec in tier2Duration downTo 1) {
                    _countdownSeconds.value = sec
                    delay(1000)
                }
                val resultTier2 = DispatchAlgorithm.findBestAmbulance(
                    targetHospital = _selectedHospital.value,
                    currentRadiusKm = 5.0,
                    availableAmbulances = _ambulancesList.value
                )
                if (resultTier2.ambulance != null) {
                    confirmDispatch(resultTier2.ambulance)
                } else {
                    // Expand to Tier 3 (10.0 km)
                    _currentTierIndex.value = 2
                    _searchRadiusKm.value = 10.0
                    delay(1500)
                    val fallbackAmbulance = _ambulancesList.value.firstOrNull() ?: Ambulance(
                        id = "amb-fallback",
                        vehicleNo = "HR-26-EM-9999",
                        name = "Apollo Critical ALS-1",
                        driverName = "Paramedic Vikram",
                        driverPhone = "+91 98765 11223",
                        distanceKm = 4.2,
                        etaMinutes = 7,
                        type = "ALS Intensive Care",
                        rating = 4.9,
                        equipment = listOf("Defibrillator", "Ventilator", "Oxygen", "Multipara Monitor"),
                        hospitalAffiliation = _selectedHospital.value.name
                    )
                    confirmDispatch(fallbackAmbulance)
                }
            }
        }
    }

    fun confirmPatientPickedUp() {
        _isPatientPickedUp.value = true
        _dispatchStatus.value = DispatchStatus.PATIENT_PICKED_UP
        if (_routeProgressPercent.value < 50) {
            _routeProgressPercent.value = 50
        }
        viewModelScope.launch {
            delay(1200)
            _dispatchStatus.value = DispatchStatus.EN_ROUTE_HOSPITAL
        }
    }

    fun confirmDispatch(ambulance: Ambulance) {
        searchTimerJob?.cancel()
        _activeAmbulance.value = ambulance
        _dispatchStatus.value = DispatchStatus.DRIVER_ASSIGNED
        _isEmergencyActive.value = true
        _isPatientPickedUp.value = false
        _remainingEtaMinutes.value = ambulance.etaMinutes
        _remainingDistanceKm.value = ambulance.distanceKm
        _routeProgressPercent.value = 10
        _isStalled.value = false

        // Alert Angel Contacts in Room Database
        viewModelScope.launch {
            repository.updateAngelAlertStatus("notified", "Just now")
        }

        // Initialize Hospital Form Data with active patient
        val patient = _activePatient.value
        if (patient != null) {
            _hospitalFormData.value = HospitalFormData(
                patientName = patient.name,
                age = patient.age,
                gender = patient.gender,
                bloodGroup = patient.bloodGroup,
                allergies = patient.allergies.joinToString(", "),
                additionalNotes = patient.emergencyNotes,
                selectedInsuranceId = patient.insurances.firstOrNull()?.id ?: "",
                attachedReportIds = patient.reports.map { it.id }
            )
        }

        startRouteTrackingSimulation()
    }

    private fun startRouteTrackingSimulation() {
        trackingSimJob?.cancel()
        trackingSimJob = viewModelScope.launch {
            var progress = 10
            while (_isEmergencyActive.value) {
                delay(if (_isFastDemoMode.value) 1500 else 3000)
                if (!_isStalled.value) {
                    if (progress < 50) {
                        // Phase 1: Ambulance -> House (Pickup)
                        progress += 10
                        if (progress >= 50) {
                            progress = 50
                            _dispatchStatus.value = DispatchStatus.EN_ROUTE_PICKUP
                        }
                        _routeProgressPercent.value = progress
                        val phase1Eta = ((50 - progress) * (_activeAmbulance.value?.etaMinutes ?: 4) / 50).coerceAtLeast(1)
                        _remainingEtaMinutes.value = phase1Eta
                        val phase1Dist = String.format("%.1f", ((50 - progress) * (_activeAmbulance.value?.distanceKm ?: 2.0) / 50).coerceAtLeast(0.1)).toDoubleOrNull() ?: 0.5
                        _remainingDistanceKm.value = phase1Dist
                    } else if (progress == 50) {
                        // At Patient's House: Waiting for Patient Pickup Confirmation
                        if (_isPatientPickedUp.value) {
                            progress += 10
                            _routeProgressPercent.value = progress
                            _dispatchStatus.value = DispatchStatus.EN_ROUTE_HOSPITAL
                        }
                    } else if (progress < 100) {
                        // Phase 2: House -> Hospital
                        progress += 10
                        if (progress > 100) progress = 100
                        _routeProgressPercent.value = progress
                        val phase2Eta = ((100 - progress) * 6 / 50).coerceAtLeast(1)
                        _remainingEtaMinutes.value = phase2Eta
                        val phase2Dist = String.format("%.1f", ((100 - progress) * 3.8 / 50).coerceAtLeast(0.1)).toDoubleOrNull() ?: 0.3
                        _remainingDistanceKm.value = phase2Dist

                        if (progress >= 100) {
                            _dispatchStatus.value = DispatchStatus.ARRIVED_HOSPITAL
                            break
                        }
                    }
                }
            }
        }
    }

    fun triggerStallAndRedispatch() {
        _isStalled.value = true
        _dispatchStatus.value = DispatchStatus.STALLED_REDISPATCHING

        viewModelScope.launch {
            delay(4000)
            val backup = _ambulancesList.value.firstOrNull { it.id != _activeAmbulance.value?.id } ?: _ambulancesList.value.last()
            _activeAmbulance.value = backup
            _isStalled.value = false
            _dispatchStatus.value = DispatchStatus.DRIVER_ASSIGNED
            _remainingEtaMinutes.value = backup.etaMinutes
            _remainingDistanceKm.value = backup.distanceKm
            _routeProgressPercent.value = 30
        }
    }

    fun cancelEmergency() {
        searchTimerJob?.cancel()
        trackingSimJob?.cancel()
        _isEmergencyActive.value = false
        _activeAmbulance.value = null
        _dispatchStatus.value = DispatchStatus.IDLE
        _isStalled.value = false
        _routeProgressPercent.value = 0
    }

    fun submitHospitalForm(data: HospitalFormData) {
        _hospitalFormData.value = data.copy(submittedAt = "Just now")
        _hospitalFormSubmitted.value = true
    }

    fun sendChatMessage(text: String) {
        val userMsg = ChatMessage(System.currentTimeMillis().toString(), "user", text, "Now")
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            delay(800)
            val replyText = when {
                text.contains("network", ignoreCase = true) || text.contains("hospital", ignoreCase = true) ->
                    "Great news! Max Super Speciality, Fortis Memorial, and Apollo Hospitals are 100% cashless network partners with instant TPA pre-authorization."
                text.contains("document", ignoreCase = true) || text.contains("pre-auth", ignoreCase = true) ->
                    "Required documents for cashless admission: 1) Health Insurance e-Card, 2) Govt Photo ID (Aadhaar/PAN), 3) Doctor's Emergency Admission Note."
                text.contains("reimburse", ignoreCase = true) ->
                    "If treated at a non-network hospital, collect the original itemized hospital bills, discharge summary, and pharmacy receipts to file claim within 30 days."
                else ->
                    "Your cashless policy is active. TPA Desk is pre-notified of patient arrival. Co-pay is 0% for emergency stabilization."
            }
            val botMsg = ChatMessage((System.currentTimeMillis() + 1).toString(), "assistant", replyText, "Now")
            _chatMessages.value = _chatMessages.value + botMsg
        }
    }

    fun saveFamilyMember(member: FamilyMember) {
        viewModelScope.launch {
            repository.saveFamilyMember(member)
        }
    }

    fun deleteFamilyMember(memberId: String) {
        viewModelScope.launch {
            repository.deleteFamilyMember(memberId)
        }
    }

    fun addAngelContact(name: String, relation: String, phone: String) {
        val newContact = AngelContact(
            id = "angel-${System.currentTimeMillis()}",
            name = name,
            relation = relation,
            phone = phone,
            notifyOnEmergency = true,
            lastAlertStatus = "idle"
        )
        viewModelScope.launch {
            repository.saveAngelContact(newContact)
        }
    }

    fun removeAngelContact(contactId: String) {
        viewModelScope.launch {
            repository.deleteAngelContact(contactId)
        }
    }

    fun addMedicalReport(report: MedicalReport) {
        _reports.value = listOf(report) + _reports.value
    }

    fun bookNonEmergency(booking: NonEmergencyBooking) {
        viewModelScope.launch {
            repository.saveBooking(booking)
        }
    }

    fun verifyInsurancePolicy(policy: InsurancePolicy) {
        viewModelScope.launch {
            _verifyingPolicyId.value = policy.id
            delay(1000) // Verification check with IRDAI & IIB Registry
            val authCode = "IRDAI-IIB-AUTH-${(10000..99999).random()}-2026"
            repository.verifyInsurance(policy.id, verified = true, code = authCode)
            val updated = policy.copy(isVerified = true, verificationCode = authCode)
            _verificationCertificatePolicy.value = updated
            _verifyingPolicyId.value = null
        }
    }

    fun verifyAllInsurancePolicies() {
        viewModelScope.launch {
            _verifyingPolicyId.value = "ALL"
            delay(1200)
            repository.verifyAllInsurances()
            _verifyingPolicyId.value = null
            _verificationCertificatePolicy.value = _insurances.value.firstOrNull()?.copy(isVerified = true, verificationCode = "IRDAI-ALL-GENUINE-2026")
        }
    }

    fun dismissVerificationCertificate() {
        _verificationCertificatePolicy.value = null
    }
}
