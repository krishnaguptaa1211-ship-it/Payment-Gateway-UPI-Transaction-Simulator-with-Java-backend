package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Transaction
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionDetailsDrawer(
    txn: Transaction,
    onClose: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val format = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateString = format.format(Date(txn.timestamp))

    val isMyOutflow = txn.senderUpiId.contains("rahul")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f),
        colors = CardDefaults.cardColors(containerColor = BaseBg),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .clickable(enabled = false, onClick = {}) // Block tap propagation dismissals
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "TRANSACTION AUDIT TRACE",
                    color = Slate100,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close description", tint = Slate400)
                }
            }

            // Big visually striking amount
            val sym = if (isMyOutflow) "-" else "+"
            val color = if (txn.status == "FAILED") DangerRed else if (isMyOutflow) Slate100 else MintTeal
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${sym}₹${String.format("%,.2f", txn.amount)}",
                    color = color,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black
                )
                Text(txn.status, color = if (txn.status == "FAILED") DangerRed else MintTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Divider(color = Color.White.copy(alpha = 0.05f))

            // Audit statistics list
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                AuditDetailsRow("TIMESTAMP", dateString)
                AuditDetailsRow("SENDER UPI ID", txn.senderUpiId)
                AuditDetailsRow("RECEIVER UPI ID", txn.receiverUpiId)
                AuditDetailsRow("CATEGORY", txn.category)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            clipboardManager.setText(AnnotatedString(txn.transactionId))
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TRANSACTION ID", color = Slate500, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(txn.transactionId, color = WarmGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy txn id", tint = WarmGold, modifier = Modifier.size(14.dp))
                    }
                }

                if (txn.note.isNotEmpty()) {
                    AuditDetailsRow("MEMO NOTE", txn.note)
                }
            }

            Divider(color = Color.White.copy(alpha = 0.05f))

            // AUDIT TIMELINE SEC (5 stages)
            Text(
                "NPCI LIFECYCLE AUDIT TRAIL",
                color = Slate300,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.fillMaxWidth()
            )

            val stages = listOf(
                "Distributed handshake security checkpoint initialized." to true,
                "Queried bank routing switchboards and ledger vaults." to true,
                "UPI cryptographic credential verified successfully." to true,
                "Deducted reserves balance update logs generated." to (txn.status == "SUCCESS"),
                (if (txn.status == "SUCCESS") "Ledger payload finalized successfully." else "Payload execution aborted: ${txn.errorReason}") to (txn.status == "SUCCESS")
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                stages.forEachIndexed { i, stage ->
                    val isChecked = stage.second
                    val circleBg = if (isChecked) MintTeal else DangerRed
                    val textCol = if (isChecked) Slate100 else Slate500

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(circleBg.copy(alpha = 0.2f), shape = CircleShape)
                                    .border(1.dp, circleBg, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(modifier = Modifier.size(6.dp).background(circleBg, shape = CircleShape))
                            }
                            if (i < stages.size - 1) {
                                Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(alpha = 0.1f)))
                            }
                        }

                        Column {
                            Text(
                                "Stage ${i + 1}: ${if (i == 4 && txn.status != "SUCCESS") "Execution Aborted" else "Authorized verified"}",
                                color = if (isChecked) MintTeal else DangerRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                stage.first,
                                color = textCol,
                                fontSize = 10.sp,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuditDetailsRow(label: String, valStr: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Slate500, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(valStr, color = Slate300, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
