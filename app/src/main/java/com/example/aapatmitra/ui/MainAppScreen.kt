package com.example.aapatmitra.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aapatmitra.model.DispatchStatus
import com.example.aapatmitra.ui.components.*
import com.example.aapatmitra.ui.theme.EmergencyDarkBg
import com.example.aapatmitra.viewmodel.AapatMitraViewModel

@Composable
fun MainAppScreen(
    viewModel: AapatMitraViewModel
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLang.collectAsStateWithLifecycle()
    val activeNavTab by viewModel.activeNavTab.collectAsStateWithLifecycle()
    val transportMode by viewModel.transportMode.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val familyMembers by viewModel.familyMembers.collectAsStateWithLifecycle()
    val activePatient by viewModel.activePatient.collectAsStateWithLifecycle()
    val selectedHospital by viewModel.selectedHospital.collectAsStateWithLifecycle()
    val pickupAddress by viewModel.pickupAddress.collectAsStateWithLifecycle()
    val hospitalsList by viewModel.hospitalsList.collectAsStateWithLifecycle()
    val ambulancesList by viewModel.ambulancesList.collectAsStateWithLifecycle()
    val insurances by viewModel.insurances.collectAsStateWithLifecycle()
    val reports by viewModel.reports.collectAsStateWithLifecycle()
    val angelContacts by viewModel.angelContacts.collectAsStateWithLifecycle()

    val dispatchStatus by viewModel.dispatchStatus.collectAsStateWithLifecycle()
    val isEmergencyActive by viewModel.isEmergencyActive.collectAsStateWithLifecycle()
    val activeAmbulance by viewModel.activeAmbulance.collectAsStateWithLifecycle()
    val searchRadiusKm by viewModel.searchRadiusKm.collectAsStateWithLifecycle()
    val currentTierIndex by viewModel.currentTierIndex.collectAsStateWithLifecycle()
    val countdownSeconds by viewModel.countdownSeconds.collectAsStateWithLifecycle()
    val isFastDemoMode by viewModel.isFastDemoMode.collectAsStateWithLifecycle()
    val isPatientPickedUp by viewModel.isPatientPickedUp.collectAsStateWithLifecycle()
    val isStalled by viewModel.isStalled.collectAsStateWithLifecycle()
    val routeProgressPercent by viewModel.routeProgressPercent.collectAsStateWithLifecycle()
    val remainingEtaMinutes by viewModel.remainingEtaMinutes.collectAsStateWithLifecycle()
    val remainingDistanceKm by viewModel.remainingDistanceKm.collectAsStateWithLifecycle()
    val hospitalFormSubmitted by viewModel.hospitalFormSubmitted.collectAsStateWithLifecycle()
    val hospitalFormData by viewModel.hospitalFormData.collectAsStateWithLifecycle()
    val nonEmergencyBookings by viewModel.nonEmergencyBookings.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()

    val isAngelModalOpen by viewModel.isAngelModalOpen.collectAsStateWithLifecycle()
    val isInsuranceVaultOpen by viewModel.isInsuranceVaultOpen.collectAsStateWithLifecycle()
    val isInsuranceChatOpen by viewModel.isInsuranceChatOpen.collectAsStateWithLifecycle()
    val isMedicalReportOpen by viewModel.isMedicalReportOpen.collectAsStateWithLifecycle()
    val isHospitalAdmissionModalOpen by viewModel.isHospitalAdmissionModalOpen.collectAsStateWithLifecycle()
    val isDriverCallModalOpen by viewModel.isDriverCallModalOpen.collectAsStateWithLifecycle()
    val isLanguagePickerOpen by viewModel.isLanguagePickerOpen.collectAsStateWithLifecycle()
    val isHospitalPickerOpen by viewModel.isHospitalPickerOpen.collectAsStateWithLifecycle()

    val verifyingPolicyId by viewModel.verifyingPolicyId.collectAsStateWithLifecycle()
    val verificationCertificatePolicy by viewModel.verificationCertificatePolicy.collectAsStateWithLifecycle()

    // 1. Show Login / Signup Screen at first if not logged in
    if (!isLoggedIn) {
        LoginScreen(
            currentLang = currentLang,
            existingUser = userProfile,
            onLoginSuccess = { userEntity ->
                viewModel.loginUser(userEntity)
            },
            onRegisterNewUser = { name, email, phone, anchorPhone, bloodGroup, address, allergies, chronicConditions, insuranceProvider, policyNumber, sumInsured ->
                viewModel.registerNewUser(
                    name = name,
                    email = email,
                    phone = phone,
                    householdPhone = anchorPhone,
                    bloodGroup = bloodGroup,
                    address = address,
                    allergies = allergies,
                    chronicConditions = chronicConditions,
                    insuranceProvider = insuranceProvider,
                    policyNumber = policyNumber,
                    sumInsured = sumInsured
                )
            }
        )
        return
    }

    // 2. Main Authenticated Application
    Scaffold(
        topBar = {
            AppHeader(
                currentLang = currentLang,
                isEmergencyActive = isEmergencyActive,
                activeHospitalName = selectedHospital.name,
                onOpenAngelPortal = { viewModel.isAngelModalOpen.value = true },
                onOpenLanguagePicker = { viewModel.isLanguagePickerOpen.value = true }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentLang = currentLang,
                activeNavTab = activeNavTab,
                onTabSelected = { tab ->
                    viewModel.setActiveNavTab(tab)
                }
            )
        },
        containerColor = EmergencyDarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(EmergencyDarkBg)
        ) {
            when (activeNavTab) {
                "main" -> {
                    if (transportMode == "non-emergency" && dispatchStatus == DispatchStatus.IDLE) {
                        NonEmergencyTransportView(
                            currentLang = currentLang,
                            activePatient = activePatient,
                            familyMembers = familyMembers,
                            bookings = nonEmergencyBookings,
                            onBookRide = { viewModel.bookNonEmergency(it) },
                            onSwitchToEmergency = { viewModel.setTransportMode("emergency") }
                        )
                    } else {
                        when (dispatchStatus) {
                            DispatchStatus.IDLE -> {
                                SOSHoldButton(
                                    currentLang = currentLang,
                                    transportMode = transportMode,
                                    onModeChanged = { viewModel.setTransportMode(it) },
                                    activePatient = activePatient,
                                    familyMembers = familyMembers,
                                    onSelectPatient = { viewModel.selectPatient(it) },
                                    selectedHospital = selectedHospital,
                                    onOpenHospitalPicker = { viewModel.isHospitalPickerOpen.value = true },
                                    pickupAddress = pickupAddress,
                                    onUpdateAddress = { viewModel.setPickupAddress(it) },
                                    isFastDemoMode = isFastDemoMode,
                                    onToggleFastDemoMode = { viewModel.toggleFastDemoMode() },
                                    onStartDispatch = { viewModel.startEmergencySearch() },
                                    onOpenFamilyPortal = { viewModel.setActiveNavTab("family") }
                                )
                            }
                            DispatchStatus.SEARCHING -> {
                                SearchingDispatchView(
                                    currentLang = currentLang,
                                    selectedHospital = selectedHospital,
                                    searchRadiusKm = searchRadiusKm,
                                    currentTierIndex = currentTierIndex,
                                    countdownSeconds = countdownSeconds,
                                    isFastDemoMode = isFastDemoMode,
                                    onCancelEmergency = { viewModel.cancelEmergency() },
                                    onForceSelectAmbulance = { viewModel.confirmDispatch(it) },
                                    ambulances = ambulancesList
                                )
                            }
                            else -> {
                                if (activeAmbulance != null) {
                                    LiveAmbulanceTrackerView(
                                        currentLang = currentLang,
                                        ambulance = activeAmbulance!!,
                                        selectedHospital = selectedHospital,
                                        activePatient = activePatient,
                                        pickupAddress = pickupAddress,
                                        routeProgressPercent = routeProgressPercent,
                                        remainingEtaMinutes = remainingEtaMinutes,
                                        remainingDistanceKm = remainingDistanceKm,
                                        isPatientPickedUp = isPatientPickedUp,
                                        onConfirmPatientPickup = { viewModel.confirmPatientPickedUp() },
                                        isStalled = isStalled,
                                        onTriggerStall = { viewModel.triggerStallAndRedispatch() },
                                        onOpenDriverCall = { viewModel.isDriverCallModalOpen.value = true },
                                        onOpenHospitalForm = { viewModel.isHospitalAdmissionModalOpen.value = true },
                                        onOpenInsuranceChat = { viewModel.isInsuranceChatOpen.value = true },
                                        onOpenMedicalReports = { viewModel.isMedicalReportOpen.value = true },
                                        onCancelEmergency = { viewModel.cancelEmergency() },
                                        hospitalFormSubmitted = hospitalFormSubmitted
                                    )
                                }
                            }
                        }
                    }
                }
                "family" -> {
                    FamilyPortalView(
                        currentLang = currentLang,
                        familyMembers = familyMembers,
                        activePatient = activePatient,
                        onSelectPatientForEmergency = { member ->
                            viewModel.selectPatient(member)
                            viewModel.setActiveNavTab("main")
                        },
                        onSaveMember = { viewModel.saveFamilyMember(it) },
                        onDeleteMember = { viewModel.deleteFamilyMember(it) }
                    )
                }
                "profile" -> {
                    ProfileVaultView(
                        userProfile = userProfile,
                        insurances = insurances,
                        reports = reports,
                        angelContacts = angelContacts,
                        onOpenInsuranceVault = { viewModel.isInsuranceVaultOpen.value = true },
                        onOpenReportsVault = { viewModel.isMedicalReportOpen.value = true },
                        onOpenAngelPortal = { viewModel.isAngelModalOpen.value = true },
                        onVerifyPolicy = { viewModel.verifyInsurancePolicy(it) },
                        onVerifyAllInsurances = { viewModel.verifyAllInsurancePolicies() },
                        onLogout = { viewModel.logout() }
                    )
                }
            }
        }
    }

    // Modal Dialogs
    if (isHospitalPickerOpen) {
        HospitalPickerDialog(
            hospitals = hospitalsList,
            selectedHospital = selectedHospital,
            onSelectHospital = { viewModel.setSelectedHospital(it) },
            onDismiss = { viewModel.isHospitalPickerOpen.value = false }
        )
    }

    if (isLanguagePickerOpen) {
        LanguagePickerDialog(
            onSelectLanguage = { viewModel.setLanguage(it) },
            onDismiss = { viewModel.isLanguagePickerOpen.value = false }
        )
    }

    if (isAngelModalOpen) {
        AngelGuardianDialog(
            angelContacts = angelContacts,
            onDismiss = { viewModel.isAngelModalOpen.value = false },
            onAddContact = { name, relation, phone ->
                viewModel.addAngelContact(name, relation, phone)
            },
            onRemoveContact = { viewModel.removeAngelContact(it) }
        )
    }

    if (isInsuranceVaultOpen) {
        InsuranceVaultDialog(
            insurances = insurances,
            verifyingPolicyId = verifyingPolicyId,
            onVerifyPolicy = { viewModel.verifyInsurancePolicy(it) },
            onVerifyAll = { viewModel.verifyAllInsurancePolicies() },
            onDismiss = { viewModel.isInsuranceVaultOpen.value = false },
            onOpenChatbot = { viewModel.isInsuranceChatOpen.value = true }
        )
    }

    if (verificationCertificatePolicy != null) {
        InsuranceAuthenticityDialog(
            policy = verificationCertificatePolicy!!,
            onDismiss = { viewModel.dismissVerificationCertificate() }
        )
    }

    if (isInsuranceChatOpen) {
        InsuranceChatbotDialog(
            messages = chatMessages,
            onSendMessage = { viewModel.sendChatMessage(it) },
            onDismiss = { viewModel.isInsuranceChatOpen.value = false }
        )
    }

    if (isMedicalReportOpen) {
        MedicalReportDialog(
            reports = reports,
            onDismiss = { viewModel.isMedicalReportOpen.value = false },
            onAddReport = { viewModel.addMedicalReport(it) }
        )
    }

    if (isDriverCallModalOpen && activeAmbulance != null) {
        DriverCallDialog(
            ambulance = activeAmbulance!!,
            onEndCall = { viewModel.isDriverCallModalOpen.value = false }
        )
    }

    if (isHospitalAdmissionModalOpen) {
        HospitalPreAdmissionDialog(
            initialData = hospitalFormData,
            onDismiss = { viewModel.isHospitalAdmissionModalOpen.value = false },
            onSubmit = { viewModel.submitHospitalForm(it) },
            availableInsurances = insurances,
            availableReports = reports
        )
    }
}
