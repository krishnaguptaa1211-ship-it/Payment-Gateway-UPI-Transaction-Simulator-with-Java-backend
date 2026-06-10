package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CardsView(
    modifier: Modifier = Modifier,
    balance: Double
) {
    var flipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "cardFlip"
    )

    // Option control states
    var internationalPayEnabled by remember { mutableStateOf(true) }
    var blockTokenState by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "VIRTUAL IN-APP CARDS",
            color = Slate400,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        // Cards interactive flip viewport
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .aspectRatio(1.6f)
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                }
                .clickable { flipped = !flipped }
        ) {
            if (rotation <= 90f) {
                // Front Side Card Layout
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    CardBg.copy(alpha = 0.95f),
                                    BaseBg.copy(alpha = 0.9f)
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(ElectricViolet, MintTeal.copy(alpha = 0.2f))
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Card Header Logo and Bank
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Aether Signature", color = Slate100, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("PREMIUM FINTECH GATEWAY", color = Slate400, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            // Bank Badge Circle
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(
                                        Brush.horizontalGradient(listOf(ElectricViolet, MintTeal)),
                                        shape = CircleShape
                                    )
                            )
                        }

                        // Gold metallic visual chip simulator
                        Box(
                            modifier = Modifier
                                .size(width = 44.dp, height = 30.dp)
                                .background(WarmGold.copy(alpha = 0.85f), shape = RoundedCornerShape(8.dp))
                                .border(1.dp, Color.Black.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                        )

                        // Bank card numbers and expiration details
                        Column {
                            Text(
                                "4542  9901  8282  1211",
                                color = Slate100,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("CARD HOLDER", color = Slate500, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    Text("RAHUL G.", color = Slate300, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("EXPIRES", color = Slate500, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    Text("07 / 32", color = Slate300, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
            } else {
                // Back Side Card Layout (rotated so it reads normal)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    CardBg.copy(alpha = 0.95f),
                                    BaseBg.copy(alpha = 0.9f)
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(MintTeal, ElectricViolet.copy(alpha = 0.2f))
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Magnetic stripe
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .background(Color.Black)
                        )

                        // White signature strip and CVV
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(3f)
                                    .height(30.dp)
                                    .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(30.dp)
                                    .background(WarmGold, shape = RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "112",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Simulated security notes
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Text(
                                "Authorized signature. Not transferable. Built by AetherPay under strict simulation parameters. Pin required for secure operations.",
                                color = Slate500,
                                fontSize = 7.sp,
                                lineHeight = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Tap tip label
        Text(
            text = "Tap virtual card to reveal CVV and details",
            color = Slate500,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Security Options Toggles Panel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg.copy(alpha = 0.6f), shape = RoundedCornerShape(24.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Toggle 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("International Mode", color = Slate100, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Enable worldwide cross-border UPI tunnels", color = Slate500, fontSize = 11.sp)
                    }
                    Switch(
                        checked = internationalPayEnabled,
                        onCheckedChange = { internationalPayEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MintTeal,
                            checkedTrackColor = MintTeal.copy(alpha = 0.3f),
                            uncheckedThumbColor = Slate500,
                            uncheckedTrackColor = CardBg
                        )
                    )
                }

                Divider(color = Color.White.copy(alpha = 0.05f))

                // Toggle 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Lock Virtual Token", color = Slate100, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Freeze all transactions immediately", color = Slate500, fontSize = 11.sp)
                    }
                    Switch(
                        checked = blockTokenState,
                        onCheckedChange = { blockTokenState = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DangerRed,
                            checkedTrackColor = DangerRed.copy(alpha = 0.3f),
                            uncheckedThumbColor = Slate500,
                            uncheckedTrackColor = CardBg
                        )
                    )
                }
            }
        }
    }
}
