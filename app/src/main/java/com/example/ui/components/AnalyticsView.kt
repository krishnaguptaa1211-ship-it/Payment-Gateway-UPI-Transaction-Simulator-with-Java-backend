package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Transaction
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalyticsView(
    modifier: Modifier = Modifier,
    transactions: List<Transaction>
) {
    // Extract totals
    val successTxns = transactions.filter { it.status == "SUCCESS" }
    val totalSent = successTxns.filter { it.senderUpiId.contains("rahul") }.sumOf { it.amount }
    val totalReceived = successTxns.filter { it.receiverUpiId.contains("rahul") }.sumOf { it.amount }
    
    // Sort transactions by category to find distribution
    val categoryTotals = successTxns
        .filter { it.senderUpiId.contains("rahul") } // outbound spending
        .groupBy { it.category }
        .mapValues { it.value.sumOf { tx -> tx.amount } }

    val totalOutbound = categoryTotals.values.sum()
    
    // Fallback if no outbound spending yet
    val displayCategoryTotals = if (categoryTotals.isEmpty()) {
        mapOf("General" to 1000.0, "Food" to 1200.0, "Entertainment" to 850.0, "Rent" to 4500.0)
    } else {
        categoryTotals
    }
    
    val totalSpendForChart = displayCategoryTotals.values.sum()

    // Chart entry angles animation
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(transactions) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Core Statistics Cards Layout
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(CardBg.copy(alpha = 0.6f), shape = RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text("Outflow (Sent)", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("₹${String.format("%,.0f", totalSent)}", color = Slate100, fontSize = 18.sp, fontWeight = FontWeight.Black)
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(CardBg.copy(alpha = 0.6f), shape = RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text("Inflow (Received)", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("₹${String.format("%,.0f", totalReceived)}", color = MintTeal, fontSize = 18.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        // Gorgeous custom animated Segmented Donut Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f)
                .background(CardBg.copy(alpha = 0.6f), shape = RoundedCornerShape(32.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(32.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "SPENDING DISTRIBUTION",
                    color = Slate300,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val colors = listOf(ElectricViolet, MintTeal, WarmGold, Color.Cyan, Color.Magenta, InfoBlue)
                    val categoriesList = displayCategoryTotals.keys.toList()
                    val valuesList = displayCategoryTotals.values.toList()

                    // Canvas drawing
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 32f
                            val canvasSize = size.minDimension - strokeWidth
                            var startAngle = -90f

                            valuesList.forEachIndexed { idx, valAmount ->
                                val sweep = ((valAmount / totalSpendForChart) * 360f).toFloat() * animProgress.value
                                drawArc(
                                    color = colors[idx % colors.size],
                                    startAngle = startAngle,
                                    sweepAngle = sweep,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round),
                                    size = Size(canvasSize, canvasSize),
                                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                                )
                                startAngle += sweep
                            }
                        }

                        // Center statistics texts
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Out", color = Slate400, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text(
                                "₹${String.format("%,.0f", totalSent)}",
                                color = Slate100,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Legend values
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        categoriesList.forEachIndexed { index, category ->
                            val color = colors[index % colors.size]
                            val amount = valuesList[index]
                            val percent = (amount / totalSpendForChart * 100).toInt()
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(modifier = Modifier.size(10.dp).background(color, shape = CircleShape))
                                Text(
                                    "$category ($percent%)",
                                    color = Slate300,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Cash flow Bar charts side by side
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(CardBg.copy(alpha = 0.6f), shape = RoundedCornerShape(32.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), shape = RoundedCornerShape(32.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "FLOW HISTORY (SIMULATED)",
                    color = Slate300,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                // Render dynamic bar structures
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
                    val monthlyIn = listOf(35000f, 42000f, 61000f, 25000f, 72000f, totalReceived.toFloat().coerceAtLeast(30000f))
                    val monthlyOut = listOf(28000f, 31000f, 45000f, 19000f, 54000f, totalSent.toFloat().coerceAtLeast(15000f))

                    val maxVal = (monthlyIn + monthlyOut).maxOrNull() ?: 100000f

                    months.forEachIndexed { i, month ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                // IN BAR (mint color)
                                val inHeight = (monthlyIn[i] / maxVal) * animProgress.value
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(inHeight)
                                        .background(
                                            brush = Brush.verticalGradient(listOf(MintTeal, MintTeal.copy(alpha = 0.2f))),
                                            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                        )
                                )
                                // OUT BAR (purple color)
                                val outHeight = (monthlyOut[i] / maxVal) * animProgress.value
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(outHeight)
                                        .background(
                                            brush = Brush.verticalGradient(listOf(ElectricViolet, ElectricViolet.copy(alpha = 0.2f))),
                                            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                        )
                                )
                            }
                            Text(month, color = Slate400, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
