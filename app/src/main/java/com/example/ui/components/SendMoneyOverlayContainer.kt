package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile
import com.example.ui.AetherViewModel
import com.example.ui.SendMoneyState
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SendMoneyOverlayContainer(
    sendState: SendMoneyState,
    viewModel: AetherViewModel,
    onClose: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BaseBg)
    ) {
        // Render current Sub-Screens based on state
        when (sendState) {
            is SendMoneyState.EnterDetails -> {
                EnterDetailsScreen(
                    payee = sendState.selectedPayee,
                    viewModel = viewModel,
                    onClose = onClose
                )
            }
            is SendMoneyState.PinEntry -> {
                PinEntryScreen(
                    payee = sendState.payee,
                    amount = sendState.amount,
                    note = sendState.note,
                    category = sendState.category,
                    viewModel = viewModel,
                    onClose = onClose
                )
            }
            is SendMoneyState.Processing -> {
                ProcessingScreen(
                    payee = sendState.payee,
                    amount = sendState.amount,
                    note = sendState.note,
                    category = sendState.category,
                    stepIndex = sendState.stepIndex,
                    logMessage = sendState.logMessage,
                    progress = sendState.progress
                )
            }
            is SendMoneyState.Outcome -> {
                OutcomeRevealScreen(
                    payee = sendState.payee,
                    amount = sendState.amount,
                    note = sendState.note,
                    category = sendState.category,
                    isSuccess = sendState.isSuccess,
                    errorReason = sendState.errorReason,
                    txnId = sendState.txnId,
                    onClose = onClose,
                    viewModel = viewModel
                )
            }
            else -> { /* No-op */ }
        }
    }
}

@Composable
fun EnterDetailsScreen(
    payee: UserProfile,
    viewModel: AetherViewModel,
    onClose: () -> Unit
) {
    val enteredAmt by viewModel.enteredAmount.collectAsState()
    val enteredNote by viewModel.enteredNote.collectAsState()
    val selectedCat by viewModel.selectedCategory.collectAsState()

    val quickAmountChips = listOf(100, 500, 1000, 2000, 5000)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // App header bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Slate400)
            }
            Text("AETHER SECURE GATEWAY", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Black)
            Box(modifier = Modifier.size(48.dp))
        }

        // Contact details layout
        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AetherAvatar(seed = payee.avatarSeed, name = payee.name, size = 52.dp)
                Column {
                    Text(payee.name, color = Slate100, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(payee.upiId, color = Slate500, fontSize = 12.sp)
                }
            }
        }

        // Beautiful formatted Amount Entry
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg.copy(alpha = 0.6f), shape = RoundedCornerShape(24.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("ENTER TRANSFER AMOUNT", color = Slate500, fontSize = 11.sp, fontWeight = FontWeight.Bold)

            // Large numeric input area
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("₹", color = WarmGold, fontSize = 38.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = enteredAmt,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.toDoubleOrNull() != null) {
                            viewModel.enteredAmount.value = input
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholder = {
                        Text(
                            "0",
                            color = Slate500,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Slate100,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.widthIn(min = 150.dp),
                    singleLine = true
                )
            }

            // Quick chips selection row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                quickAmountChips.forEach { value ->
                    Box(
                        modifier = Modifier
                            .background(CardBg, shape = RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(12.dp))
                            .clickable { viewModel.enteredAmount.value = value.toString() }
                            .padding(vertical = 10.dp, horizontal = 16.dp)
                    ) {
                        Text("+ ₹$value", color = Slate300, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Notes and category selector Column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg.copy(alpha = 0.6f), shape = RoundedCornerShape(24.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(24.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Note Field entries
            Text("ADD MEMO NOTE", color = Slate500, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            TextField(
                value = enteredNote,
                onValueChange = { viewModel.enteredNote.value = it },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = { Text("What is this payment for? (e.g. split lunch)", color = Slate500, fontSize = 12.sp) },
                textStyle = androidx.compose.ui.text.TextStyle(color = Slate100, fontSize = 13.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp))
                    .padding(2.dp),
                singleLine = true
            )

            // Custom categorized pills picker
            Text("SELECT CATEGORY", color = Slate500, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.categories.forEach { category ->
                    val isSelected = selectedCat == category
                    val bgCol = if (isSelected) ElectricViolet else Color.Transparent
                    val borderCol = if (isSelected) ElectricViolet else Color.White.copy(alpha = 0.05f)

                    Box(
                        modifier = Modifier
                            .background(bgCol, shape = RoundedCornerShape(12.dp))
                            .border(1.dp, borderCol, shape = RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectedCategory.value = category }
                            .padding(vertical = 10.dp, horizontal = 16.dp)
                    ) {
                        Text(category, color = if (isSelected) Color.White else Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Confirm Action CTR button
        Button(
            onClick = {
                val amt = enteredAmt.toDoubleOrNull() ?: 0.0
                viewModel.submitDetailsAndGoToPin(
                    payee,
                    amt,
                    enteredNote,
                    selectedCat
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(imageVector = Icons.Default.LockOpen, contentDescription = "Secure pay", tint = Color.White)
                Text("Proceed to UPI PIN", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun PinEntryScreen(
    payee: UserProfile,
    amount: Double,
    note: String,
    category: String,
    viewModel: AetherViewModel,
    onClose: () -> Unit
) {
    val pinBuffer by viewModel.pinBuffer.collectAsState()
    var shakeTrigger by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Bar Headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Slate400)
            }
            Text("SECURE UPI PIN KEYBOARD", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Black)
            Box(modifier = Modifier.size(48.dp))
        }

        // Recipient credentials Summary
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("PAYING SECURELY", color = Slate500, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                "₹${String.format("%,.2f", amount)}",
                color = Slate100,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black
            )
            Text("TO ${payee.name.uppercase()}", color = MintTeal, fontSize = 12.sp, fontWeight = FontWeight.Black)
            Text("UPI ID: ${payee.upiId}", color = Slate500, fontSize = 10.sp)
        }

        // Safe secure guidelines bubble
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Safe guidelines", tint = WarmGold, modifier = Modifier.size(18.dp))
                Text(
                    "AetherPay PIN is encrypted under distributed sandboxes. Demo PIN is '1211' or '0000'.",
                    color = Slate400,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }

        // Integrated PIN keypad layout
        CustomPINKeypad(
            pin = pinBuffer,
            onPinChange = { viewModel.pinBuffer.value = it },
            shakeTrigger = shakeTrigger,
            onConfirm = {
                if (pinBuffer.length < 4) {
                    shakeTrigger = true
                } else {
                    viewModel.processTransaction(payee, amount, note, category, pinBuffer)
                }
            }
        )
    }
}

@Composable
fun ProcessingScreen(
    payee: UserProfile,
    amount: Double,
    note: String,
    category: String,
    stepIndex: Int,
    logMessage: String,
    progress: Float
) {
    // Continuous spinning progress circle stroke
    val infiniteTransition = rememberInfiniteTransition(label = "RadarTransition")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarRotate"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Underneath neon tracing data vector simulation
        ConfettiAndVfxCanvas(activeProcessing = true)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Text(
                "AETHER DISTRIBUTED TRANSACTION TRACE",
                color = Slate400,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(top = 20.dp)
            )

            // Interconnected Avatars visualization
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sender: Me
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AetherAvatar(seed = "Rahul", name = "Rahul G.", size = 64.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("rahul@oksbi", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Processing Spinner Circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(CardBg.copy(alpha = 0.5f), shape = CircleShape)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(listOf(ElectricViolet, MintTeal, ElectricViolet)),
                            shape = CircleShape
                        )
                        .rotate(rotation),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.OfflineBolt,
                        contentDescription = "Bolt trace",
                        tint = MintTeal,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Receiver
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AetherAvatar(seed = payee.avatarSeed, name = payee.name, size = 64.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(payee.upiId, color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Live status checklist layout
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Display general progress percent
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MintTeal,
                    trackColor = CardBg
                )

                // Status message and log typing console
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(20.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("CORE CONSOLE", color = Slate500, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            Text("STATUS CODE: 100", color = MintTeal, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                        
                        Divider(color = Color.White.copy(alpha = 0.05f))

                        Text(
                            text = "> $logMessage",
                            color = Slate100,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OutcomeRevealScreen(
    payee: UserProfile,
    amount: Double,
    note: String,
    category: String,
    isSuccess: Boolean,
    errorReason: String?,
    txnId: String,
    onClose: () -> Unit,
    viewModel: AetherViewModel
) {
    var receiptExpGenerated by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isSuccess) {
            // Render beautiful custom high-fidelty confetti particles
            ConfettiAndVfxCanvas(activeSuccess = true)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // BIG SCREEN GLOW REVEAL OVERVIEW
            if (isSuccess) {
                // SUCCESS GLOWING CARD SEC
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .background(MintTeal.copy(alpha = 0.12f), shape = CircleShape)
                        .border(1.dp, MintTeal.copy(alpha = 0.4f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success tick",
                        tint = MintTeal,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Text(
                    "TRANSACTION SECURED",
                    color = MintTeal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            } else {
                // FAILURE CHANNELS SEC
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .background(DangerRed.copy(alpha = 0.12f), shape = CircleShape)
                        .border(1.dp, DangerRed.copy(alpha = 0.4f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error detail",
                        tint = DangerRed,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Text(
                    "TRANSACTION FAILED",
                    color = DangerRed,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }

            // Primary Receipt Lift Card Display
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("OFFICIAL FINTECH RECEIPT", color = Slate500, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    
                    Text(
                        "₹${String.format("%,.2f", amount)}",
                        color = Slate100,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )

                    Divider(color = Color.White.copy(alpha = 0.05f))

                    // Transaction details lists
                    RowItemValue("RECIPIENT", payee.name, Slate100)
                    RowItemValue("RECIPIENT UPI ID", payee.upiId, Slate454)
                    RowItemValue("TRANSACTION ID", txnId, WarmGold)
                    
                    if (note.isNotEmpty()) {
                        RowItemValue("MEMO NOTE", note, Slate100)
                    }
                    RowItemValue("CATEGORY", category, ElectricViolet)

                    if (!isSuccess && errorReason != null) {
                        Divider(color = Color.White.copy(alpha = 0.05f))
                        Text(
                            "REJECTION REASON: $errorReason",
                            color = DangerRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // PDF receipt exporter simulation or actions
            if (isSuccess) {
                var isGeneratingReceipt by remember { mutableStateOf(false) }

                Button(
                    onClick = {
                        isGeneratingReceipt = true
                        viewModel.showToast("Rendering beautiful transaction receipt PDF...")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CardBg.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isGeneratingReceipt) {
                            CircularProgressIndicator(color = MintTeal, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            LaunchedEffect(Unit) {
                                delay(2200)
                                isGeneratingReceipt = false
                                receiptExpGenerated = true
                                viewModel.showToast("Receipt PDF exported successfully to Downloads folder! 📄")
                            }
                        } else {
                            Icon(imageVector = Icons.Default.Download, contentDescription = "PDF receipt", tint = MintTeal)
                        }
                        Text("Export PDF Receipt", color = Slate300, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row: Repeat flow / Back to dashboard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (!isSuccess) {
                    Button(
                        onClick = { viewModel.initiateSendFlow(payee) },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Retry Transaction", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.initiateSendFlow(payee) },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Send Money Again", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = CardBg),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Text("Go to Dashboard", color = Slate100, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

val Slate454 = Color(0xFFCBD5E1)

@Composable
fun RowItemValue(label: String, valStr: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Slate500, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(valStr, color = valueColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}
