package com.example.aapatmitra.data.dao

import androidx.room.*
import com.example.aapatmitra.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 'primary_user' LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 'primary_user' LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProfile(user: UserProfileEntity)

    @Query("UPDATE user_profile SET isLoggedIn = :loggedIn WHERE id = 'primary_user'")
    suspend fun setLoginStatus(loggedIn: Boolean)

    @Query("DELETE FROM user_profile")
    suspend fun clearUser()
}

@Dao
interface InsuranceDao {
    @Query("SELECT * FROM insurance_policies ORDER BY id ASC")
    fun getAllInsurances(): Flow<List<InsuranceEntity>>

    @Query("SELECT * FROM insurance_policies ORDER BY id ASC")
    suspend fun getAllInsurancesOnce(): List<InsuranceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsurance(insurance: InsuranceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllInsurances(insurances: List<InsuranceEntity>)

    @Delete
    suspend fun deleteInsurance(insurance: InsuranceEntity)

    @Query("DELETE FROM insurance_policies WHERE id = :id")
    suspend fun deleteInsuranceById(id: String)

    @Query("UPDATE insurance_policies SET isVerified = :verified, verificationCode = :code WHERE id = :id")
    suspend fun updateInsuranceVerification(id: String, verified: Boolean, code: String)

    @Query("UPDATE insurance_policies SET isVerified = 1, verificationCode = 'IRDAI-GENUINE-2026'")
    suspend fun verifyAllInsurances()
}

@Dao
interface AngelContactDao {
    @Query("SELECT * FROM angel_contacts ORDER BY id ASC")
    fun getAllAngelContacts(): Flow<List<AngelContactEntity>>

    @Query("SELECT * FROM angel_contacts ORDER BY id ASC")
    suspend fun getAllAngelContactsOnce(): List<AngelContactEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAngelContact(contact: AngelContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAngelContacts(contacts: List<AngelContactEntity>)

    @Query("DELETE FROM angel_contacts WHERE id = :id")
    suspend fun deleteAngelContactById(id: String)

    @Query("UPDATE angel_contacts SET lastAlertStatus = :status, lastAlertTime = :time")
    suspend fun updateAlertStatusForAll(status: String, time: String)
}

@Dao
interface FamilyMemberDao {
    @Query("SELECT * FROM family_members ORDER BY id ASC")
    fun getAllFamilyMembers(): Flow<List<FamilyMemberEntity>>

    @Query("SELECT * FROM family_members ORDER BY id ASC")
    suspend fun getAllFamilyMembersOnce(): List<FamilyMemberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMember(member: FamilyMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFamilyMembers(members: List<FamilyMemberEntity>)

    @Query("DELETE FROM family_members WHERE id = :id")
    suspend fun deleteFamilyMemberById(id: String)
}

@Dao
interface NonEmergencyBookingDao {
    @Query("SELECT * FROM non_emergency_bookings ORDER BY id DESC")
    fun getAllBookings(): Flow<List<NonEmergencyBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: NonEmergencyBookingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBookings(bookings: List<NonEmergencyBookingEntity>)

    @Query("DELETE FROM non_emergency_bookings WHERE id = :id")
    suspend fun deleteBookingById(id: String)
}
