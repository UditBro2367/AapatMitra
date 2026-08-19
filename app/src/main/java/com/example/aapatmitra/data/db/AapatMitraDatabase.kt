package com.example.aapatmitra.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aapatmitra.data.dao.*
import com.example.aapatmitra.data.entity.*

@Database(
    entities = [
        UserProfileEntity::class,
        InsuranceEntity::class,
        AngelContactEntity::class,
        FamilyMemberEntity::class,
        NonEmergencyBookingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AapatMitraDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun insuranceDao(): InsuranceDao
    abstract fun angelContactDao(): AngelContactDao
    abstract fun familyMemberDao(): FamilyMemberDao
    abstract fun nonEmergencyBookingDao(): NonEmergencyBookingDao

    companion object {
        @Volatile
        private var INSTANCE: AapatMitraDatabase? = null

        fun getDatabase(context: Context): AapatMitraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AapatMitraDatabase::class.java,
                    "aapatmitra_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
