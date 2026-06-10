package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MintTeal
import com.example.ui.theme.WarmGold

@Composable
fun AetherAvatar(
    seed: String,
    name: String,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    // Generate constant visually distinct color ranges based on seed name
    val gradients = listOf(
        listOf(ElectricViolet, Color(0xFFC084FC)),
        listOf(MintTeal, Color(0xFF2DD4BF)),
        listOf(WarmGold, Color(0xFFFBBF24)),
        listOf(Color(0xFF3B82F6), Color(0xFF60A5FA)),
        listOf(Color(0xFFEC4899), Color(0xFFF472B6))
    )

    val gradientIndex = (seed.hashCode() % gradients.size + gradients.size) % gradients.size
    val activeGradient = gradients[gradientIndex]

    // Associated avatars emojis for a friendly touch
    val emojis = mapOf(
        "Rahul" to "🧔",
        "Priya" to "👩",
        "Arjun" to "🎓",
        "Sneha" to "👩",
        "Vikram" to "🤵"
    )

    val emoji = emojis[seed] ?: "⚡"

    Box(
        modifier = modifier
            .size(size)
            .background(
                brush = Brush.linearGradient(activeGradient),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                fontSize = (size.value * 0.42f).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
