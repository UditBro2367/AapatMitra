package com.example.aapatmitra.data.repository

import com.example.aapatmitra.data.dao.*
import com.example.aapatmitra.data.entity.*
import com.example.aapatmitra.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AapatMitraRepository(
    private val userDao: UserDao,
    private val insuranceDao: InsuranceDao,
    private val angelContactDao: AngelContactDao,
    private val familyMemberDao: FamilyMemberDao,
    private val nonEmergencyBookingDao: NonEmergencyBookingDao
) {
    val userProfile: Flow<UserProfileEntity?> = userDao.getUserProfile()

    val insurances: Flow<List<InsurancePolicy>> = insuranceDao.getAllInsurances().map { list ->
        list.map { it.toModel() }
    }

    val angelContacts: Flow<List<AngelContact>> = angelContactDao.getAllAngelContacts().map { list ->
        list.map { it.toModel() }
    }

    val familyMembers: Flow<List<FamilyMember>> = familyMemberDao.getAllFamilyMembers().map { list ->
        list.map { it.toModel() }
    }

    val nonEmergencyBookings: Flow<List<NonEmergencyBooking>> = nonEmergencyBookingDao.getAllBookings().map { list ->
        list.map { it.toModel() }
    }

    suspend fun saveUserProfile(user: UserProfileEntity) {
        userDao.insertOrUpdateUserProfile(user)
    }

    suspend fun setLoginStatus(isLoggedIn: Boolean) {
        userDao.setLoginStatus(isLoggedIn)
    }

    suspend fun saveInsurance(insurance: InsurancePolicy) {
        insuranceDao.insertInsurance(insurance.toEntity())
    }

    suspend fun verifyInsurance(id: String, verified: Boolean = true, code: String = "IRDAI-GENUINE-2026") {
        insuranceDao.updateInsuranceVerification(id, verified, code)
    }

    suspend fun verifyAllInsurances() {
        insuranceDao.verifyAllInsurances()
    }

    suspend fun deleteInsurance(id: String) {
        insuranceDao.deleteInsuranceById(id)
    }

    suspend fun saveAngelContact(contact: AngelContact) {
        angelContactDao.insertAngelContact(contact.toEntity())
    }

    suspend fun deleteAngelContact(id: String) {
        angelContactDao.deleteAngelContactById(id)
    }

    suspend fun updateAngelAlertStatus(status: String, time: String) {
        angelContactDao.updateAlertStatusForAll(status, time)
    }

    suspend fun saveFamilyMember(member: FamilyMember) {
        familyMemberDao.insertFamilyMember(member.toEntity())
    }

    suspend fun deleteFamilyMember(id: String) {
        familyMemberDao.deleteFamilyMemberById(id)
    }

    suspend fun saveBooking(booking: NonEmergencyBooking) {
        nonEmergencyBookingDao.insertBooking(booking.toEntity())
    }

    suspend fun seedInitialDataIfEmpty() {
        val existingUser = userDao.getUserProfileOnce()
        if (existingUser == null) {
            userDao.insertOrUpdateUserProfile(
                UserProfileEntity(
                    id = "primary_user",
                    name = "Rahul Sharma",
                    email = "rahul.sharma@example.com",
                    phone = "+91 98765 43210",
                    age = 34,
                    gender = "Male",
                    householdPhone = "+91 98765 43210",
                    bloodGroup = "O+",
                    address = "Flat 402, Lotus Towers, Golf Course Road, Sector 54, Gurugram",
                    allergies = "Penicillin, Dust",
                    chronicConditions = "Mild Hypertension",
                    emergencyNotes = "Keep oxygen concentrator standby. Allergic to Sulfa drugs.",
                    isLoggedIn = false // start at Login / Signup screen
                )
            )
        }

        val existingInsurances = insuranceDao.getAllInsurancesOnce()
        if (existingInsurances.isEmpty()) {
            val defaultInsurances = MockData.initialInsurances.map { it.toEntity() }
            insuranceDao.insertAllInsurances(defaultInsurances)
        }

        val existingAngels = angelContactDao.getAllAngelContactsOnce()
        if (existingAngels.isEmpty()) {
            val defaultAngels = MockData.initialAngelContacts.map { it.toEntity() }
            angelContactDao.insertAllAngelContacts(defaultAngels)
        }

        val existingFamily = familyMemberDao.getAllFamilyMembersOnce()
        if (existingFamily.isEmpty()) {
            val defaultFamily = MockData.initialFamilyMembers.map { it.toEntity() }
            familyMemberDao.insertAllFamilyMembers(defaultFamily)
        }
    }
}

// Extension mappers
fun InsuranceEntity.toModel(): InsurancePolicy {
    return InsurancePolicy(
        id = id,
        provider = provider,
        policyNumber = policyNumber,
        policyHolder = policyHolder,
        sumInsured = sumInsured,
        expiryDate = expiryDate,
        isVerified = isVerified,
        verificationCode = verificationCode,
        tpaDeskPhone = tpaDeskPhone,
        coverageType = coverageType,
        networkHospitals = networkHospitals.split(",").map { it.trim() },
        notes = notes
    )
}

fun InsurancePolicy.toEntity(): InsuranceEntity {
    return InsuranceEntity(
        id = id,
        provider = provider,
        policyNumber = policyNumber,
        policyHolder = policyHolder,
        sumInsured = sumInsured,
        expiryDate = expiryDate,
        isVerified = isVerified,
        verificationCode = verificationCode,
        tpaDeskPhone = tpaDeskPhone,
        coverageType = coverageType,
        networkHospitals = networkHospitals.joinToString(", "),
        notes = notes
    )
}

fun AngelContactEntity.toModel(): AngelContact {
    return AngelContact(
        id = id,
        name = name,
        relation = relation,
        phone = phone,
        notifyOnEmergency = notifyOnEmergency,
        lastAlertStatus = lastAlertStatus,
        lastAlertTime = lastAlertTime
    )
}

fun AngelContact.toEntity(): AngelContactEntity {
    return AngelContactEntity(
        id = id,
        name = name,
        relation = relation,
        phone = phone,
        notifyOnEmergency = notifyOnEmergency,
        lastAlertStatus = lastAlertStatus,
        lastAlertTime = lastAlertTime
    )
}

fun FamilyMemberEntity.toModel(): FamilyMember {
    return FamilyMember(
        id = id,
        name = name,
        relation = relation,
        age = age,
        gender = gender,
        phone = phone,
        householdAnchorPhone = householdAnchorPhone,
        bloodGroup = bloodGroup,
        address = address,
        allergies = if (allergies.isBlank()) emptyList() else allergies.split(",").map { it.trim() },
        chronicConditions = if (chronicConditions.isBlank()) emptyList() else chronicConditions.split(",").map { it.trim() },
        emergencyNotes = emergencyNotes,
        primaryDoctorName = primaryDoctorName,
        primaryDoctorPhone = primaryDoctorPhone,
        isDefaultPatient = isDefaultPatient,
        avatarColor = avatarColor,
        insurances = if (!insuranceProvider.isNullOrBlank()) {
            listOf(
                InsurancePolicy(
                    id = "ins-${id}",
                    provider = insuranceProvider,
                    policyNumber = insurancePolicyNumber ?: "POL-UNKNOWN",
                    policyHolder = name,
                    sumInsured = "₹10,00,000",
                    expiryDate = "31 Dec 2027",
                    isVerified = true
                )
            )
        } else emptyList()
    )
}

fun FamilyMember.toEntity(): FamilyMemberEntity {
    return FamilyMemberEntity(
        id = id,
        name = name,
        relation = relation,
        age = age,
        gender = gender,
        phone = phone,
        householdAnchorPhone = householdAnchorPhone,
        bloodGroup = bloodGroup,
        address = address,
        allergies = allergies.joinToString(", "),
        chronicConditions = chronicConditions.joinToString(", "),
        emergencyNotes = emergencyNotes,
        primaryDoctorName = primaryDoctorName,
        primaryDoctorPhone = primaryDoctorPhone,
        insuranceProvider = insurances.firstOrNull()?.provider,
        insurancePolicyNumber = insurances.firstOrNull()?.policyNumber,
        isDefaultPatient = isDefaultPatient,
        avatarColor = avatarColor
    )
}

fun NonEmergencyBookingEntity.toModel(): NonEmergencyBooking {
    return NonEmergencyBooking(
        id = id,
        patientName = patientName,
        phone = phone,
        serviceType = serviceType,
        pickupAddress = pickupAddress,
        dropAddress = dropAddress,
        scheduledDate = scheduledDate,
        scheduledTime = scheduledTime,
        specialAssistance = if (specialAssistance.isBlank()) emptyList() else specialAssistance.split(",").map { it.trim() },
        status = status
    )
}

fun NonEmergencyBooking.toEntity(): NonEmergencyBookingEntity {
    return NonEmergencyBookingEntity(
        id = id,
        patientName = patientName,
        phone = phone,
        serviceType = serviceType,
        pickupAddress = pickupAddress,
        dropAddress = dropAddress,
        scheduledDate = scheduledDate,
        scheduledTime = scheduledTime,
        specialAssistance = specialAssistance.joinToString(", "),
        status = status
    )
}
