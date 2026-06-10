package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AetherDao {
    @Query("SELECT * FROM user_profiles ORDER BY isMe DESC, name ASC")
    fun getAllProfiles(): Flow<List<UserProfile>>

    @Query("SELECT * FROM user_profiles WHERE isMe = 1 LIMIT 1")
    fun getMyProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE isMe = 1 LIMIT 1")
    suspend fun getMyProfile(): UserProfile?

    @Query("SELECT * FROM user_profiles WHERE upiId = :upiId LIMIT 1")
    suspend fun getProfile(upiId: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfile)

    @Update
    suspend fun updateProfile(profile: UserProfile)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Query("SELECT * FROM sim_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<SimSettings?>

    @Query("SELECT * FROM sim_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): SimSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SimSettings)

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM user_profiles")
    suspend fun clearProfiles()
}
