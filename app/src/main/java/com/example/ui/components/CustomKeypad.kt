package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBg
import com.example.ui.theme.CardBgGlass
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MintTeal
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400

@Composable
fun CustomPINKeypad(
    modifier: Modifier = Modifier,
    pin: String,
    onPinChange: (String) -> Unit,
    onConfirm: () -> Unit,
    shakeTrigger: Boolean = false
) {
    val items = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "back", "0", "check"
    )

    // Optional shake math offset using simple spring physics simulation
    val shakeOffset = remember { Animatable(0f) }
    
    LaunchedEffect(shakeTrigger) {
        if (shakeTrigger) {
            val steps = listOf(-25f, 20f, -15f, 10f, -5f, 0f)
            steps.forEach { offset ->
                shakeOffset.animateTo(
                    targetValue = offset,
                    animationSpec = tween(70, easing = LinearEasing)
                )
            }
        }
    }

    Column(
        modifier = modifier
            .offset(x = shakeOffset.value.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Render current masked DOTS representing typed PIN
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            for (i in 0..3) {
                val filled = i < pin.length
                val color = if (filled) MintTeal else CardBgGlass
                val borderCol = if (filled) MintTeal else Slate400.copy(alpha = 0.4f)
                
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(color, shape = CircleShape)
                        .background(Color.Transparent, shape = CircleShape)
                        .padding(2.dp)
                )
            }
        }

        // Custom 3x4 Grid for keypad layout
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (row in 0..3) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        if (index < items.size) {
                            val buttonValue = items[index]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1.8f)
                                    .background(
                                        color = if (buttonValue == "check") ElectricViolet else CardBg.copy(alpha = 0.8f),
                                        shape = RoundedCornerShape(18.dp)
                                    )
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(color = Color.White),
                                        onClick = {
                                            when (buttonValue) {
                                                "back" -> {
                                                    if (pin.isNotEmpty()) onPinChange(pin.dropLast(1))
                                                }
                                                "check" -> {
                                                    if (pin.length >= 4) {
                                                        onConfirm()
                                                    }
                                                }
                                                else -> {
                                                    if (pin.length < 4) onPinChange(pin + buttonValue)
                                                }
                                            }
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                when (buttonValue) {
                                    "back" -> {
                                        Icon(
                                            imageVector = Icons.Default.Backspace,
                                            contentDescription = "Backspace",
                                            tint = Slate100,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    "check" -> {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Confirm",
                                            tint = Color.White,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                    else -> {
                                        Text(
                                            text = buttonValue,
                                            color = Slate100,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
