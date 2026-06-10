package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MintTeal
import com.example.ui.theme.WarmGold
import kotlin.math.sin

data class ConfettiParticle(
    val x: Float,
    var y: Float,
    val size: Float,
    val color: Color,
    val speedY: Float,
    val speedX: Float,
    val angleFrequency: Float,
    var rotation: Float = 0f
)

@Composable
fun ConfettiAndVfxCanvas(
    modifier: Modifier = Modifier,
    activeSuccess: Boolean = false,
    activeProcessing: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ConfettiTransition")
    
    // Smooth looping animation progress for continuous tick updates
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiTick"
    )

    // Confetti particles pool
    val particles = remember { mutableStateListOf<ConfettiParticle>() }
    
    // Seed new particles only at the start of activeSuccess
    LaunchedEffect(activeSuccess) {
        if (activeSuccess) {
            particles.clear()
            val colors = listOf(MintTeal, ElectricViolet, WarmGold, Color.Cyan, Color.Magenta)
            for (i in 0..120) {
                particles.add(
                    ConfettiParticle(
                        x = (0..100).random().toFloat() / 100f,
                        y = -10f - (0..300).random().toFloat(),
                        size = (10..22).random().toFloat(),
                        color = colors.random(),
                        speedY = (8..18).random().toFloat(),
                        speedX = (-4..4).random().toFloat(),
                        angleFrequency = (2..6).random().toFloat() / 10f
                    )
                )
            }
        } else {
            particles.clear()
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        if (activeSuccess && particles.isNotEmpty()) {
            particles.forEach { p ->
                // Adjust x-position dynamically based on sine wave logic
                val waveX = (sin(animProgress * Math.PI * p.angleFrequency).toFloat()) * 35.0f * p.speedX
                val posX = (p.x * width) + waveX
                
                // Fall velocity calculation
                p.y += p.speedY
                if (p.y > height) {
                    p.y = -20f // Loop back to top for continuous drop
                }
                
                // Increment particle rot
                p.rotation += 3f

                // Draw rotated rectangular/square confetti pieces
                drawContext.canvas.save()
                drawContext.canvas.translate(posX, p.y)
                drawContext.canvas.rotate(p.rotation)
                drawRect(
                    color = p.color,
                    topLeft = Offset(-p.size / 2, -p.size / 2),
                    size = androidx.compose.ui.geometry.Size(p.size, p.size * 0.6f)
                )
                drawContext.canvas.restore()
            }
        }

        if (activeProcessing) {
            // Draw cinematic floating micro-glow data packets
            val pulse = sin(animProgress * Math.PI * 2).toFloat()
            val colorsList = listOf(ElectricViolet, MintTeal)
            
            // Draw a curved neon connecting visual trace path 
            val pX1 = width * 0.25f
            val pY1 = height * 0.4f
            val pX2 = width * 0.75f
            val pY2 = height * 0.4f

            // Background connecting neon arc line
            drawLine(
                color = ElectricViolet.copy(alpha = 0.22f),
                start = Offset(pX1, pY1),
                end = Offset(pX2, pY2),
                strokeWidth = 6f
            )

            // Animated light data packets traversing the connection
            for (i in 0..4) {
                val shift = (animProgress + (i * 0.2f)) % 1f
                val dotX = pX1 + (pX2 - pX1) * shift
                val dotY = pY1 + sin(shift * Math.PI).toFloat() * -50f // Parabolic arc
                
                drawCircle(
                    color = MintTeal,
                    radius = 8f + (4f * (1f - shift)),
                    center = Offset(dotX, dotY),
                    alpha = 0.85f
                )
                
                // Glow aura circles
                drawCircle(
                    color = MintTeal.copy(alpha = 0.25f),
                    radius = 20f + (8f * pulse),
                    center = Offset(dotX, dotY)
                )
            }
        }
    }
}
