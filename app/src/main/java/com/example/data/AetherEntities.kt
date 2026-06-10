package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val upiId: String,
    val name: String,
    val avatarSeed: String,
    val balance: Double,
    val bankName: String,
    val accountNumber: String,
    val dailyLimit: Double = 200000.0,
    val isMe: Boolean = false
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val transactionId: String,
    val senderUpiId: String,
    val senderName: String,
    val receiverUpiId: String,
    val receiverName: String,
    val receiverAvatarSeed: String,
    val amount: Double,
    val note: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String, // "SUCCESS", "PENDING", "FAILED"
    val errorReason: String? = null
)

@Entity(tableName = "sim_settings")
data class SimSettings(
    @PrimaryKey val id: Int = 1,
    val successRate: Int = 85, // 0 - 100
    val latencySeconds: Int = 3, // 1 - 10
    val forceOutcome: String = "AUTO", // "AUTO", "FORCE_SUCCESS", "FORCE_FAILURE"
    val balanceLimit: Double = 200000.0
)
