package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile
import com.example.ui.AetherViewModel
import com.example.ui.theme.*
import kotlin.math.sin

@Composable
fun QrExperienceView(
    viewModel: AetherViewModel,
    profilesList: List<UserProfile>,
    onClose: () -> Unit
) {
    var activeTabIsScan by remember { mutableStateOf(true) }
    val clipboardManager = LocalClipboardManager.current

    // Infinite scan line animation
    val infiniteTransition = rememberInfiniteTransition(label = "ScanLineTransition")
    val laserY by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserLine"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f),
        colors = CardDefaults.cardColors(containerColor = BaseBg),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
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
                    "AETHER CONTACTLESS",
                    color = Slate100,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                }
            }

            // Tab toggles: SCAN QR vs GENERATE MY QR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBg.copy(alpha = 0.7f), shape = RoundedCornerShape(16.dp))
                    .padding(6.dp)
            ) {
                Button(
                    onClick = { activeTabIsScan = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTabIsScan) ElectricViolet else Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Scan pay barcode", color = if (activeTabIsScan) Color.White else Slate500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { activeTabIsScan = false },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!activeTabIsScan) MintTeal else Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("My QR ID", color = if (!activeTabIsScan) BaseBg else Slate500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (activeTabIsScan) {
                // SCAN BARCODE SECTION
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Point the camera at any UPI QR barcode",
                        color = Slate400,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Stylized viewfinder frame
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .background(CardBg.copy(alpha = 0.3f), shape = RoundedCornerShape(24.dp))
                            .border(2.dp, ElectricViolet.copy(alpha = 0.3f), shape = RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Drawing corner brackets and the scan laser line dynamically!
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val r = 36f

                            // Corner guides
                            val strokeW = 8f
                            // Top Left
                            drawArc(Color.Cyan, 180f, 90f, false, Offset(16f, 16f), Size(r, r), style = androidx.compose.ui.graphics.drawscope.Stroke(strokeW))
                            drawLine(Color.Cyan, Offset(16f, 16f + r/2), Offset(16f, 16f + r/2 + 20f), strokeW)
                            drawLine(Color.Cyan, Offset(16f + r/2, 16f), Offset(16f + r/2 + 20f, 16f), strokeW)

                            // Top Right
                            drawArc(Color.Cyan, 270f, 90f, false, Offset(w - 16f - r, 16f), Size(r, r), style = androidx.compose.ui.graphics.drawscope.Stroke(strokeW))
                            drawLine(Color.Cyan, Offset(w - 16f, 16f + r/2), Offset(w - 16f, 16f + r/2 + 20f), strokeW)
                            drawLine(Color.Cyan, Offset(w - 16f - r/2, 16f), Offset(w - 16f - r/2 - 20f, 16f), strokeW)

                            // Bottom Left
                            drawArc(Color.Cyan, 90f, 90f, false, Offset(16f, h - 16f - r), Size(r, r), style = androidx.compose.ui.graphics.drawscope.Stroke(strokeW))
                            drawLine(Color.Cyan, Offset(16f, h - 16f - r/2), Offset(16f, h - 16f - r/2 - 20f), strokeW)
                            drawLine(Color.Cyan, Offset(16f + r/2, h - 16f), Offset(16f + r/2 + 10f, h - 16f), strokeW)

                            // Bottom Right
                            drawArc(Color.Cyan, 0f, 90f, false, Offset(w - 16f - r, h - 16f - r), Size(r, r), style = androidx.compose.ui.graphics.drawscope.Stroke(strokeW))
                            drawLine(Color.Cyan, Offset(w - 16f, h - 16f - r/2), Offset(w - 16f, h - 16f - r/2 - 20f), strokeW)

                            // Laser Beam Line
                            val lineY = h * laserY
                            drawLine(
                                color = MintTeal,
                                start = Offset(24f, lineY),
                                end = Offset(w - 24f, lineY),
                                strokeWidth = 5f
                            )
                        }

                        // Central Icon Indicator
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scanner icon",
                            tint = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(72.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("DEMO SIMULATOR TARGETS", color = Slate500, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)

                    // Inject target buttons to auto-fill the scan flow immediately
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val demoTargets = profilesList.filter { !it.isMe }
                        demoTargets.take(3).forEach { target ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(CardBg, shape = RoundedCornerShape(16.dp))
                                    .border(1.dp, ElectricViolet.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp))
                                    .clickable {
                                        viewModel.initiateSendFlow(target)
                                        viewModel.setQrModalVisible(false)
                                        viewModel.showToast("QR scan success: Authenticated payee ${target.name}!")
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Pay ${target.name.substringBefore(" ")}", color = Slate100, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(target.upiId, color = Slate500, fontSize = 8.sp, maxLines = 1)
                                }
                            }
                        }
                    }
                }
            } else {
                // GENERATE WORK QR ID CHANNEL
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Your static AetherPay UPI QR code",
                        color = Slate400,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // QR Code graphic
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .background(Color.White, shape = RoundedCornerShape(24.dp))
                            .padding(24.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw mock customized QR visual pattern
                            val blockSize = w / 11f
                            val activeCol = Color.Black
                            
                            // Top Left Corner Finder
                            drawRect(activeCol, Offset(0f, 0f), Size(blockSize * 3, blockSize * 3))
                            drawRect(Color.White, Offset(blockSize, blockSize), Size(blockSize, blockSize))

                            // Top Right Corner Finder
                            drawRect(activeCol, Offset(w - blockSize * 3, 0f), Size(blockSize * 3, blockSize * 3))
                            drawRect(Color.White, Offset(w - blockSize * 2, blockSize), Size(blockSize, blockSize))

                            // Bottom Left Corner Finder
                            drawRect(activeCol, Offset(0f, h - blockSize * 3), Size(blockSize * 3, blockSize * 3))
                            drawRect(Color.White, Offset(blockSize, h - blockSize * 2), Size(blockSize, blockSize))

                            // Draw beautiful abstract blocks of QR grid
                            for (row in 0..10) {
                                for (col in 0..10) {
                                    // Skip finding patterns
                                    if ((row in 0..2 && col in 0..2) ||
                                        (row in 0..2 && col in 8..10) ||
                                        (row in 8..10 && col in 0..2)) {
                                        continue
                                    }
                                    
                                    // Pseudo-random deterministic hashing map
                                    val isFilled = ((row * 17 + col * 23) % 2 == 0)
                                    if (isFilled) {
                                        drawRect(
                                            activeCol,
                                            Offset(col * blockSize, row * blockSize),
                                            Size(blockSize, blockSize)
                                        )
                                    }
                                }
                            }

                            // Center brand Aether icon
                            val centerSize = blockSize * 2.5f
                            drawCircle(
                                color = ElectricViolet,
                                radius = centerSize / 1.7f,
                                center = Offset(w/2, h/2)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // UPI address layout with click copy
                    Row(
                        modifier = Modifier
                            .background(CardBg, shape = RoundedCornerShape(16.dp))
                            .clickable {
                                clipboardManager.setText(AnnotatedString("rahul@oksbi"))
                                viewModel.showToast("UPI address copied!")
                            }
                            .padding(vertical = 12.dp, horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("rahul@oksbi", color = Slate100, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = MintTeal, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
