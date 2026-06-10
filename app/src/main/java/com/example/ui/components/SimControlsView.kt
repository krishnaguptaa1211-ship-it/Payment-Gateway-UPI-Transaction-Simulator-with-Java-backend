package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SimSettings
import com.example.ui.AetherViewModel
import com.example.ui.theme.*

@Composable
fun SimControlsView(
    modifier: Modifier = Modifier,
    viewModel: AetherViewModel,
    settings: SimSettings?
) {
    val currentSuccessRate = settings?.successRate ?: 85
    val currentLatency = settings?.latencySeconds ?: 3
    val currentForceOutcome = settings?.forceOutcome ?: "AUTO"

    var sliderSuccess by remember(currentSuccessRate) { mutableStateOf(currentSuccessRate.toFloat()) }
    var sliderLatency by remember(currentLatency) { mutableStateOf(currentLatency.toFloat()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "GOD MODE / SIMULATOR PANEL",
            color = Slate400,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )

        // SLIDER 1: SUCCESS RATE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg.copy(alpha = 0.6f), shape = RoundedCornerShape(24.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Gateway Success Rate", color = Slate100, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("${sliderSuccess.toInt()}%", color = MintTeal, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Text("Controls the baseline auto roll transactions result rate", color = Slate500, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Slider(
                    value = sliderSuccess,
                    onValueChange = { sliderSuccess = it },
                    onValueChangeFinished = {
                        viewModel.updateSimSettings(
                            sliderSuccess.toInt(),
                            sliderLatency.toInt(),
                            currentForceOutcome
                        )
                    },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        activeTrackColor = MintTeal,
                        inactiveTrackColor = CardBg,
                        thumbColor = MintTeal
                    )
                )
            }
        }

        // SLIDER 2: CORE NETWORK LATENCY (SPEED)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg.copy(alpha = 0.6f), shape = RoundedCornerShape(24.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Bank Server Latency", color = Slate100, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("${sliderLatency.toInt()}s", color = WarmGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Text("Cinematic delays simulating distributed banking handshakes", color = Slate500, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Slider(
                    value = sliderLatency,
                    onValueChange = { sliderLatency = it },
                    onValueChangeFinished = {
                        viewModel.updateSimSettings(
                            sliderSuccess.toInt(),
                            sliderLatency.toInt(),
                            currentForceOutcome
                        )
                    },
                    valueRange = 1f..10f,
                    colors = SliderDefaults.colors(
                        activeTrackColor = WarmGold,
                        inactiveTrackColor = CardBg,
                        thumbColor = WarmGold
                    )
                )
            }
        }

        // FORCE TARGET OUTCOMES CHOICES
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg.copy(alpha = 0.6f), shape = RoundedCornerShape(24.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Inject Execution Override", color = Slate100, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Overrides the success rate matrix with forced states", color = Slate500, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))
                
                val outcomes = listOf(
                    "AUTO" to "Auto Roll (Uses Success Rate)",
                    "FORCE_SUCCESS" to "Force absolute SUCCESS state",
                    "FORCE_FAILURE" to "Force distributed failure timeouts"
                )

                outcomes.forEach { option ->
                    val isSelected = currentForceOutcome == option.first
                    val borderCol = if (isSelected) ElectricViolet else Color.White.copy(alpha = 0.05f)
                    val bgCol = if (isSelected) ElectricViolet.copy(alpha = 0.12f) else Color.Transparent

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bgCol, shape = RoundedCornerShape(12.dp))
                            .border(1.dp, borderCol, shape = RoundedCornerShape(12.dp))
                            .clickable {
                                viewModel.updateSimSettings(
                                    currentSuccessRate,
                                    currentLatency,
                                    option.first
                                )
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(option.second, color = if (isSelected) Slate100 else Slate400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                viewModel.updateSimSettings(
                                    currentSuccessRate,
                                    currentLatency,
                                    option.first
                                )
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = ElectricViolet,
                                unselectedColor = Slate500
                            )
                        )
                    }
                }
            }
        }

        // ACTION BOXES (Incoming simulations / Reset DB)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Action 1: Receive incoming money
            Button(
                onClick = { viewModel.injectIncomingTransaction() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MintTeal
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.FlashOn, contentDescription = "Flash receive", tint = BaseBg)
                    Text("Simulate Inbound Pay", color = BaseBg, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Action 2: Reset app data seed
            Button(
                onClick = { viewModel.resetDatabase() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DangerRed),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Cached, contentDescription = "Reset db", tint = DangerRed)
                    Text("Reset Seed Data", color = DangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
