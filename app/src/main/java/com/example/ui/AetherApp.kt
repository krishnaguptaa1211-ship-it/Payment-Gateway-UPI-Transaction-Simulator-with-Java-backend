package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Transaction
import com.example.data.UserProfile
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AetherApp(
    viewModel: AetherViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val sendMoneyState by viewModel.sendMoneyState.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    
    val profilesList by viewModel.allProfiles.collectAsStateWithLifecycle()
    val myProfile by viewModel.myProfile.collectAsStateWithLifecycle()
    val transactionsList by viewModel.allTransactions.collectAsStateWithLifecycle()
    val settingsState by viewModel.settings.collectAsStateWithLifecycle()
    val qrModalVisible by viewModel.qrModalVisible.collectAsStateWithLifecycle()
    
    val scope = rememberCoroutineScope()
    var selectedTxnForDrawer by remember { mutableStateOf<Transaction?>(null) }
    
    val searchQueryVal by viewModel.searchQuery.collectAsStateWithLifecycle()
    val manualUpiVal by viewModel.manualUpiId.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BaseBg)
    ) {
        // Aesthetic ambient aura background glow circles
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(300.dp)
                .align(Alignment.TopStart)
                .background(
                    Brush.radialGradient(
                        colors = listOf(ElectricViolet.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(350.dp)
                .align(Alignment.BottomEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(MintTeal.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                // Verified user profile header bar
                if (sendMoneyState is SendMoneyState.Idle) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 44.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(44.dp)) {
                                AetherAvatar(seed = "Rahul", name = "Rahul G.", size = 44.dp)
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(MintTeal, shape = CircleShape)
                                        .border(2.dp, BaseBg, shape = CircleShape)
                                        .align(Alignment.BottomEnd)
                                )
                            }
                            Column {
                                Text(
                                    "VERIFIED ID CODE",
                                    color = Slate500,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                                Text(
                                    "rahul@oksbi",
                                    color = Slate100,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Notifications indicator or title branding
                        IconButton(
                            onClick = { viewModel.showToast("Secure AetherPay Gateway active 🔐") },
                            modifier = Modifier
                                .size(44.dp)
                                .background(CardBg.copy(alpha = 0.6f), shape = RoundedCornerShape(16.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(16.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Shield",
                                tint = MintTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            },
            bottomBar = {
                if (sendMoneyState is SendMoneyState.Idle) {
                    CustomBottomNavBar(
                        selectedTab = currentTab,
                        onTabChange = { viewModel.selectTab(it) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // View routes mapping based on active selected tab
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220)) with fadeOut(animationSpec = tween(220))
                    }
                ) { tab ->
                    when (tab) {
                        ActiveTab.HOME -> {
                            HomeScreenContent(
                                viewModel = viewModel,
                                myProfile = myProfile,
                                profilesList = profilesList,
                                transactionsList = transactionsList,
                                searchQuery = searchQueryVal,
                                manualUpi = manualUpiVal,
                                onTxnClick = { selectedTxnForDrawer = it }
                            )
                        }
                        ActiveTab.ANALYTICS -> {
                            AnalyticsView(transactions = transactionsList)
                        }
                        ActiveTab.CARDS -> {
                            CardsView(balance = myProfile?.balance ?: 0.0)
                        }
                        ActiveTab.SIM -> {
                            SimControlsView(viewModel = viewModel, settings = settingsState)
                        }
                    }
                }
            }
        }

        // FULLSCREEN SEND MONEY FLOW OVERLAYS
        AnimatedVisibility(
            visible = sendMoneyState !is SendMoneyState.Idle,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            SendMoneyOverlayContainer(
                sendState = sendMoneyState,
                viewModel = viewModel,
                onClose = { viewModel.resetSendFlow() }
            )
        }

        // CUSTOM HISTORIC TRANSACTION DETAILS LIFE TIMELINE DRAWER
        AnimatedVisibility(
            visible = selectedTxnForDrawer != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { selectedTxnForDrawer = null },
                contentAlignment = Alignment.BottomCenter
            ) {
                selectedTxnForDrawer?.let { txn ->
                    TransactionDetailsDrawer(
                        txn = txn,
                        onClose = { selectedTxnForDrawer = null }
                    )
                }
            }
        }

        // CONTACTLESS QR EXPERIENCE DRAWER
        AnimatedVisibility(
            visible = qrModalVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { viewModel.setQrModalVisible(false) },
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(modifier = Modifier.clickable(enabled = false, onClick = {})) {
                    QrExperienceView(
                        viewModel = viewModel,
                        profilesList = profilesList,
                        onClose = { viewModel.setQrModalVisible(false) }
                    )
                }
            }
        }

        // DYNAMICS TOAST DIALOG STATE OVERLAY
        AnimatedVisibility(
            visible = toastMessage != null,
            enter = slideInVertically(initialOffsetY = { -150 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -150 }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp)
        ) {
            toastMessage?.let { msg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(MintTeal, shape = CircleShape)
                        )
                        Text(
                            text = msg,
                            color = Slate100,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomBottomNavBar(
    selectedTab: ActiveTab,
    onTabChange: (ActiveTab) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                ActiveTab.HOME to "Home" to Icons.Default.Home,
                ActiveTab.ANALYTICS to "Growth" to Icons.AutoMirrored.Filled.TrendingUp,
                ActiveTab.CARDS to "Cards" to Icons.Default.CreditCard,
                ActiveTab.SIM to "Sim" to Icons.Default.Settings
            )

            tabs.forEach { tabItem ->
                val (tab, label) = tabItem.first
                val icon = tabItem.second
                val isSelected = selectedTab == tab
                val tint = if (isSelected) MintTeal else Slate500

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabChange(tab) }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = tint,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = label,
                        color = tint,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    viewModel: AetherViewModel,
    myProfile: UserProfile?,
    profilesList: List<UserProfile>,
    transactionsList: List<Transaction>,
    searchQuery: String,
    manualUpi: String,
    onTxnClick: (Transaction) -> Unit
) {
    val balance = myProfile?.balance ?: 142850.0

    // Animate available balance counter on load
    var displayedBalance by remember { mutableStateOf(0.0) }
    LaunchedEffect(balance) {
        displayedBalance = balance
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Available balance primary glass banner card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                // Background Glow Glow
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(130.dp)
                        .align(Alignment.Center)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ElectricViolet.copy(alpha = 0.22f), MintTeal.copy(alpha = 0.15f))
                            ),
                            shape = RoundedCornerShape(32.dp)
                        )
                )

                // Main card body
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBg.copy(alpha = 0.65f), shape = RoundedCornerShape(32.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(32.dp))
                        .padding(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Available Balance", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text("₹", color = WarmGold, fontSize = 28.sp, fontWeight = FontWeight.Black)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = String.format("%,.2f", displayedBalance),
                                        color = Slate100,
                                        fontSize = 34.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = (-1).sp
                                    )
                                }
                            }

                            // Dynamic VIP Account Status
                            Box(
                                modifier = Modifier
                                    .background(ElectricViolet.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp))
                                    .border(1.dp, ElectricViolet, shape = RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("VIP PRESET", color = ElectricViolet, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        // Linked bank card details sub-panel
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.25f), shape = RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ACCOUNT LINKED", color = Slate500, fontSize = 7.sp, fontWeight = FontWeight.Black)
                                Text("HDFC Bank •••• 9901", color = Slate300, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Box(modifier = Modifier.width(1.dp).height(20.dp).background(Color.White.copy(alpha = 0.1f)))
                            Column(horizontalAlignment = Alignment.End) {
                                Text("DAILY GATEWAY LIMIT", color = Slate500, fontSize = 7.sp, fontWeight = FontWeight.Black)
                                Text("₹2,00,000.00", color = Slate300, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // Quick Fintech Actions grid (Send, Request, SCAN QR, Bills)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick 1: Quick initiate Flow
                QuickActionButton(
                    icon = Icons.AutoMirrored.Filled.Send,
                    label = "Send Pay",
                    color = ElectricViolet,
                    onClick = {
                        val otherContact = profilesList.firstOrNull { !it.isMe }
                        if (otherContact != null) viewModel.initiateSendFlow(otherContact)
                    }
                )

                // Quick 2: Request Simulated payment
                QuickActionButton(
                    icon = Icons.Default.Add,
                    label = "Request",
                    color = CardBg,
                    tint = MintTeal,
                    onClick = { viewModel.injectIncomingTransaction() }
                )

                // Quick 3: Trigger scan viewport modal
                QuickActionButton(
                    icon = Icons.Default.QrCode,
                    label = "Scan QR",
                    color = CardBg,
                    tint = WarmGold,
                    onClick = { viewModel.setQrModalVisible(true) }
                )

                // Quick 4: Dummy Bills alert trigger
                QuickActionButton(
                    icon = Icons.Default.ReceiptLong,
                    label = "Bills Pay",
                    color = CardBg,
                    tint = Slate400,
                    onClick = { viewModel.showToast("Utility Bills features simulated locally! 💡") }
                )
            }
        }

        // Interactive search box & manual UPI entry
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "MANUAL UPI DIRECT GATEWAY",
                    color = Slate300,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBg.copy(alpha = 0.6f), shape = RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(16.dp))
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AlternateEmail,
                        contentDescription = "Search contact",
                        tint = Slate400,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(20.dp)
                    )
                    
                    var manualInputText by remember { mutableStateOf("") }

                    TextField(
                        value = manualInputText,
                        onValueChange = { manualInputText = it },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = { Text("Enter UPI ID (e.g., arjun@okaxis)", color = Slate500, fontSize = 13.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Slate100, fontSize = 14.sp),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            viewModel.handleManualUpiSubmit(manualInputText)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Verify", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Horizontal contacts scrolling checklist
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "FAVORITE RECIPIENTS",
                    color = Slate300,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val clients = profilesList.filter { !it.isMe }
                    clients.forEach { contact ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .clickable { viewModel.initiateSendFlow(contact) }
                                .padding(vertical = 4.dp)
                        ) {
                            AetherAvatar(seed = contact.avatarSeed, name = contact.name, size = 54.dp)
                            Text(
                                text = contact.name.substringBefore(" "),
                                color = Slate100,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = contact.upiId,
                                color = Slate500,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
            }
        }

        // Recent Activity transaction ledger lists
        item {
            Text(
                "RECENT LEDGER ACTIVITY",
                color = Slate300,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        val itemsData = transactionsList.take(15)
        if (itemsData.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions seeded or found! Go to Sim tab to seed.", color = Slate500)
                }
            }
        } else {
            items(itemsData) { tx ->
                TransactionItemRow(
                    tx = tx,
                    onTxnClick = { onTxnClick(tx) }
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(color, shape = RoundedCornerShape(18.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            color = Slate300,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TransactionItemRow(
    tx: Transaction,
    onTxnClick: () -> Unit
) {
    val isIncoming = tx.receiverUpiId.contains("rahul")
    val avatarLetters = if (isIncoming) tx.senderName.take(1) else tx.receiverName.take(1)
    val name = if (isIncoming) tx.senderName else tx.receiverName
    val verb = if (isIncoming) "Received" else "Sent to"
    
    val format = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateString = format.format(Date(tx.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBg.copy(alpha = 0.4f), shape = RoundedCornerShape(20.dp))
            .border(1.dp, Color.White.copy(alpha = 0.02f), shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable { onTxnClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile initial visual badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (tx.status == "FAILED") DangerRed.copy(alpha = 0.15f) else ElectricViolet.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarLetters,
                    color = if (tx.status == "FAILED") DangerRed else ElectricViolet,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Column {
                Text(
                    text = name,
                    color = Slate100,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$dateString • $verb",
                    color = Slate500,
                    fontSize = 10.sp
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            val symbol = if (isIncoming) "+" else "-"
            val color = if (tx.status == "FAILED") DangerRed else if (isIncoming) MintTeal else Slate100
            Text(
                text = "${symbol}₹${String.format("%,.2f", tx.amount)}",
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = tx.status,
                color = if (tx.status == "FAILED") DangerRed.copy(alpha = 0.7f) else if (tx.status == "PENDING") WarmGold else Slate500,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
