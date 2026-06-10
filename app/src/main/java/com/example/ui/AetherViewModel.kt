package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

sealed class SendMoneyState {
    object Idle : SendMoneyState()
    data class EnterDetails(val selectedPayee: UserProfile) : SendMoneyState()
    data class PinEntry(val payee: UserProfile, val amount: Double, val note: String, val category: String) : SendMoneyState()
    data class Processing(val payee: UserProfile, val amount: Double, val note: String, val category: String, val stepIndex: Int, val logMessage: String, val progress: Float) : SendMoneyState()
    data class Outcome(val payee: UserProfile, val amount: Double, val note: String, val category: String, val isSuccess: Boolean, val errorReason: String?, val txnId: String) : SendMoneyState()
}

enum class ActiveTab {
    HOME, ANALYTICS, CARDS, SIM
}

class AetherViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AetherRepository(db.aetherDao())

    // All profiles
    val allProfiles: StateFlow<List<UserProfile>> = repository.allProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current user profile (Rahul G.)
    val myProfile: StateFlow<UserProfile?> = repository.myProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Log of all transactions
    val allTransactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Simulator Settings
    val settings: StateFlow<SimSettings?> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Navigation and UX Flow State
    private val _currentTab = MutableStateFlow(ActiveTab.HOME)
    val currentTab: StateFlow<ActiveTab> = _currentTab.asStateFlow()

    private val _sendMoneyState = MutableStateFlow<SendMoneyState>(SendMoneyState.Idle)
    val sendMoneyState: StateFlow<SendMoneyState> = _sendMoneyState.asStateFlow()

    // App alert banner state
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Inputs in Send money flow
    val searchQuery = MutableStateFlow("")
    val manualUpiId = MutableStateFlow("")
    val enteredAmount = MutableStateFlow("")
    val enteredNote = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("General")
    val pinBuffer = MutableStateFlow("")

    val categories = listOf("General", "Food", "Rent", "Entertainment", "Tech", "Bills", "Shopping")

    // QR scan modal visible state
    private val _qrModalVisible = MutableStateFlow(false)
    val qrModalVisible: StateFlow<Boolean> = _qrModalVisible.asStateFlow()

    init {
        viewModelScope.launch {
            // Seed database on first boot
            repository.seedIfNeeded()
        }
    }

    fun showToast(msg: String) {
        viewModelScope.launch {
            _toastMessage.value = msg
            delay(3000)
            if (_toastMessage.value == msg) {
                _toastMessage.value = null
            }
        }
    }

    fun selectTab(tab: ActiveTab) {
        _currentTab.value = tab
    }

    fun setQrModalVisible(visible: Boolean) {
        _qrModalVisible.value = visible
    }

    fun initiateSendFlow(payee: UserProfile) {
        enteredAmount.value = ""
        enteredNote.value = ""
        selectedCategory.value = "General"
        searchQuery.value = ""
        manualUpiId.value = ""
        _sendMoneyState.value = SendMoneyState.EnterDetails(payee)
    }

    fun resetSendFlow() {
        _sendMoneyState.value = SendMoneyState.Idle
        enteredAmount.value = ""
        enteredNote.value = ""
        pinBuffer.value = ""
    }

    fun submitDetailsAndGoToPin(payee: UserProfile, amount: Double, note: String, category: String) {
        val me = myProfile.value
        if (me == null) {
            showToast("User profile not loaded")
            return
        }
        if (amount <= 0) {
            showToast("Please enter a valid amount")
            return
        }
        if (amount > me.balance) {
            showToast("Insufficient balance (Limit: ₹${me.balance})")
            return
        }
        pinBuffer.value = ""
        _sendMoneyState.value = SendMoneyState.PinEntry(payee, amount, note, category)
    }

    fun processTransaction(payee: UserProfile, amount: Double, note: String, category: String, enteredPin: String) {
        viewModelScope.launch {
            val simSettings = repository.settings.firstOrNull() ?: SimSettings()
            val me = myProfile.value ?: return@launch

            // Check PIN (simulating real keypad; correct PIN is always '1211' or any 4-digit input except empty)
            if (enteredPin.length < 4 || (enteredPin != "1211" && enteredPin != "0000" && enteredPin.take(4) == "1111")) {
                showToast("Security error: Invalid UPI PIN!")
                return@launch
            }

            // Start Cinematic Processing Theater Flow
            _sendMoneyState.value = SendMoneyState.Processing(
                payee = payee,
                amount = amount,
                note = note,
                category = category,
                stepIndex = 0,
                logMessage = "Initiating secure handshake sequence...",
                progress = 0.05f
            )

            val latencyMs = (simSettings.latencySeconds * 1000L) / 5
            val steps = listOf(
                "Secure handshake established. Connecting to server..." to 0.25f,
                "Routing gateway through NPCI UPI switchboard network..." to 0.50f,
                "Authenticating digital token credentials and signatures..." to 0.70f,
                "Querying bank vault core processing API networks..." to 0.90f,
                "Executing secure distributed ledger entries..." to 1.0f
            )

            for (i in 0 until 5) {
                delay(latencyMs)
                _sendMoneyState.value = SendMoneyState.Processing(
                    payee = payee,
                    amount = amount,
                    note = note,
                    category = category,
                    stepIndex = i + 1,
                    logMessage = steps[i].first,
                    progress = steps[i].second
                )
            }

            delay(350) // Mini-dramatic pause

            // Calculate Outcome based on settings
            var isSuccess = true
            var errorReason: String? = null

            when (simSettings.forceOutcome) {
                "FORCE_SUCCESS" -> {
                    isSuccess = true
                }
                "FORCE_FAILURE" -> {
                    isSuccess = false
                    errorReason = "Bank Server Timeout (NPCI-503)"
                }
                else -> {
                    // AUTO
                    val rand = (1..100).random()
                    if (rand > simSettings.successRate) {
                        isSuccess = false
                        val reasons = listOf(
                            "Bank servers busy. Please try again later.",
                            "UPI network congestion (NPCI-102)",
                            "Account balance verification failed.",
                            "Transient transaction execution timeout."
                        )
                        errorReason = reasons.random()
                    }
                }
            }

            // Check final balance bounds
            if (isSuccess && amount > me.balance) {
                isSuccess = false
                errorReason = "Insufficient funds in linked bank account."
            }

            val txnId = "TXN" + UUID.randomUUID().toString().take(12).uppercase()

            if (isSuccess) {
                // Deduct balance and update room
                val updatedMe = me.copy(balance = me.balance - amount)
                repository.updateProfile(updatedMe)

                // Add success transaction to Room
                val tx = Transaction(
                    transactionId = txnId,
                    senderUpiId = me.upiId,
                    senderName = me.name,
                    receiverUpiId = payee.upiId,
                    receiverName = payee.name,
                    receiverAvatarSeed = payee.avatarSeed,
                    amount = amount,
                    note = note,
                    category = category,
                    status = "SUCCESS"
                )
                repository.insertTransaction(tx)
            } else {
                // Add failed transaction to Room
                val tx = Transaction(
                    transactionId = txnId,
                    senderUpiId = me.upiId,
                    senderName = me.name,
                    receiverUpiId = payee.upiId,
                    receiverName = payee.name,
                    receiverAvatarSeed = payee.avatarSeed,
                    amount = amount,
                    note = note,
                    category = category,
                    status = "FAILED",
                    errorReason = errorReason
                )
                repository.insertTransaction(tx)
            }

            _sendMoneyState.value = SendMoneyState.Outcome(
                payee = payee,
                amount = amount,
                note = note,
                category = category,
                isSuccess = isSuccess,
                errorReason = errorReason,
                txnId = txnId
            )
        }
    }

    // God Mode: Slider settings change
    fun updateSimSettings(successRate: Int, latencySeconds: Int, forceOutcome: String) {
        viewModelScope.launch {
            val updated = SimSettings(
                id = 1,
                successRate = successRate,
                latencySeconds = latencySeconds,
                forceOutcome = forceOutcome
            )
            repository.updateSettings(updated)
            showToast("God Mode settings updated!")
        }
    }

    // God Mode: Inject incoming UPI payment simulated WebSocket event
    fun injectIncomingTransaction() {
        viewModelScope.launch {
            val contacts = allProfiles.value.filter { !it.isMe }
            if (contacts.isEmpty()) {
                showToast("No ready contacts to inject payment from.")
                return@launch
            }
            val sender = contacts.random()
            val injectAmount = listOf(200.0, 500.0, 1000.0, 2500.0, 4800.0).random()
            val notes = listOf("Dinner share", "Refund!", "Gift 🎁", "Split expense", "Taxi ride 🚕")
            val selectedNote = notes.random()
            val categoriesMap = mapOf(
                "Dinner share" to "Food",
                "Refund!" to "General",
                "Gift 🎁" to "Entertainment",
                "Split expense" to "General",
                "Taxi ride 🚕" to "Bills"
            )

            // Dynamic notify message
            showToast("Incoming payment from ${sender.name} of ₹$injectAmount ...")
            delay(1200)

            val me = myProfile.value ?: return@launch
            val txnId = "TXN" + UUID.randomUUID().toString().take(12).uppercase()

            // Update balance
            val updatedMe = me.copy(balance = me.balance + injectAmount)
            repository.updateProfile(updatedMe)

            val tx = Transaction(
                transactionId = txnId,
                senderUpiId = sender.upiId,
                senderName = sender.name,
                receiverUpiId = me.upiId,
                receiverName = me.name,
                receiverAvatarSeed = sender.avatarSeed,
                amount = injectAmount,
                note = selectedNote,
                category = categoriesMap[selectedNote] ?: "General",
                status = "SUCCESS"
            )
            repository.insertTransaction(tx)
            showToast("Successfully received ₹$injectAmount from ${sender.name}!")
        }
    }

    // Reset simulator database
    fun resetDatabase() {
        viewModelScope.launch {
            repository.resetDatabase()
            showToast("AetherPay database resetted successfully!")
        }
    }

    // Scan Simulated QRs
    fun handleManualUpiSubmit(id: String) {
        val target = id.trim().lowercase()
        if (target.isEmpty() || !target.contains("@")) {
            showToast("Please enter a valid format UPI ID (e.g., name@okbank)")
            return
        }

        viewModelScope.launch {
            val existing = repository.getProfileByUpi(target)
            if (existing != null) {
                initiateSendFlow(existing)
            } else {
                // Generate a temporary new contact to pay
                val name = target.substringBefore("@").replaceFirstChar { it.uppercase() }
                val newContact = UserProfile(
                    upiId = target,
                    name = "$name",
                    avatarSeed = name,
                    balance = 100.0,
                    bankName = "State Bank of India",
                    accountNumber = "•••• 8282"
                )
                // Insert into db so we have them as recipient
                db.aetherDao().insertProfile(newContact)
                initiateSendFlow(newContact)
            }
        }
    }
}
