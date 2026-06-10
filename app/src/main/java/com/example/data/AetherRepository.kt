package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class AetherRepository(private val dao: AetherDao) {

    val allProfiles: Flow<List<UserProfile>> = dao.getAllProfiles()
    val myProfile: Flow<UserProfile?> = dao.getMyProfileFlow()
    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()
    val settings: Flow<SimSettings?> = dao.getSettingsFlow()

    suspend fun getMyProfileDirect(): UserProfile? = dao.getMyProfile()

    suspend fun getProfileByUpi(upiId: String): UserProfile? = dao.getProfile(upiId)

    suspend fun insertTransaction(transaction: Transaction): Long = dao.insertTransaction(transaction)

    suspend fun updateProfile(profile: UserProfile) = dao.updateProfile(profile)

    suspend fun updateSettings(settings: SimSettings) = dao.insertSettings(settings)

    suspend fun seedIfNeeded() {
        val currentMe = dao.getMyProfile()
        if (currentMe == null) {
            // Seed Me - Rahul
            val me = UserProfile(
                upiId = "rahul@oksbi",
                name = "Rahul G.",
                avatarSeed = "Rahul",
                balance = 142850.00,
                bankName = "HDFC Bank",
                accountNumber = "•••• 9901",
                dailyLimit = 200000.0,
                isMe = true
            )
            dao.insertProfile(me)

            // Seed other contacts/payees
            val contacts = listOf(
                UserProfile("priya@okhdfc", "Priya Sharma", "Priya", 95500.0, "ICICI Bank", "•••• 3456"),
                UserProfile("arjun@okaxis", "Arjun Varma", "Arjun", 12000.0, "Axis Bank", "•••• 1122"),
                UserProfile("sneha@okpaytm", "Sneha Patel", "Sneha", 450.0, "SBI Bank", "•••• 8877"),
                UserProfile("vikram@okicici", "Vikram Singh", "Vikram", 3500.0, "HDFC Bank", "•••• 5544")
            )
            contacts.forEach { dao.insertProfile(it) }

            // Seed default simulation settings
            dao.insertSettings(SimSettings())

            // Seed some realistic transactions
            val initialTx = listOf(
                Transaction(
                    transactionId = "TXN" + UUID.randomUUID().toString().take(12).uppercase(),
                    senderUpiId = "priya@okhdfc",
                    senderName = "Priya Sharma",
                    receiverUpiId = "rahul@oksbi",
                    receiverName = "Rahul G.",
                    receiverAvatarSeed = "Priya",
                    amount = 1200.00,
                    note = "Dinner split 🍕",
                    category = "Food",
                    timestamp = System.currentTimeMillis() - 3600000 * 2, // 2 hours ago
                    status = "SUCCESS"
                ),
                Transaction(
                    transactionId = "TXN" + UUID.randomUUID().toString().take(12).uppercase(),
                    senderUpiId = "rahul@oksbi",
                    senderName = "Rahul G.",
                    receiverUpiId = "arjun@okaxis",
                    receiverName = "Arjun Varma",
                    receiverAvatarSeed = "Arjun",
                    amount = 4500.00,
                    note = "Rent share",
                    category = "Rent",
                    timestamp = System.currentTimeMillis() - 3600000 * 24, // 1 day ago
                    status = "SUCCESS"
                ),
                Transaction(
                    transactionId = "TXN" + UUID.randomUUID().toString().take(12).uppercase(),
                    senderUpiId = "sneha@okpaytm",
                    senderName = "Sneha Patel",
                    receiverUpiId = "rahul@oksbi",
                    receiverName = "Rahul G.",
                    receiverAvatarSeed = "Sneha",
                    amount = 850.00,
                    note = "Movie tickets 🍿",
                    category = "Entertainment",
                    timestamp = System.currentTimeMillis() - 3600000 * 5, // 5 hours ago
                    status = "SUCCESS"
                ),
                Transaction(
                    transactionId = "TXN" + UUID.randomUUID().toString().take(12).uppercase(),
                    senderUpiId = "rahul@oksbi",
                    senderName = "Rahul G.",
                    receiverUpiId = "vikram@okicici",
                    receiverName = "Vikram Singh",
                    receiverAvatarSeed = "Vikram",
                    amount = 15000.00,
                    note = "Laptop repair",
                    category = "Tech",
                    timestamp = System.currentTimeMillis() - 3600000 * 48, // 2 days ago
                    status = "FAILED",
                    errorReason = "Incorrect UPI PIN entered"
                )
            )
            initialTx.forEach { dao.insertTransaction(it) }
        }
    }

    suspend fun resetDatabase() {
        dao.clearTransactions()
        dao.clearProfiles()
        seedIfNeeded()
    }
}
