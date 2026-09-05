package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CycleEntity
import com.example.ui.MonthlyAnalytics
import com.example.ui.MonthlyCycleBucket
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Purple600
import com.example.ui.theme.SleekBlue
import com.example.ui.theme.SleekBluePillBg
import com.example.ui.theme.SleekBluePillText
import com.example.ui.theme.SleekDarkCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(
    monthlyAnalytics: MonthlyAnalytics,
    monthlyCyclesBuckets: List<MonthlyCycleBucket> = emptyList(),
    cycleHistory: List<CycleEntity>,
    onExportCsv: () -> Unit,
    onExportJson: () -> Unit,
    onFetchLastYearHistory: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var previewFormat by remember { mutableStateOf<String?>(null) }
    var previewText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        onFetchLastYearHistory()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
            .testTag("analytics_screen_content"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
            Column {
                Text(
                    text = "Analytics & Usage",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("analytics_title")
                )
                Text(
                    text = "Resource Efficiency & Cycle History",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Export Actions (CSV / JSON)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(SleekBluePillBg)
                        .clickable { onExportCsv() }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("export_csv_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.FileDownload,
                            contentDescription = "Export CSV",
                            tint = SleekBluePillText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "CSV",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SleekBluePillText
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onExportJson() }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("export_json_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Code,
                            contentDescription = "Export JSON",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "JSON",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Overall Cycle History for the Last Year Banner & Fetch Control
    item {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("overall_last_year_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = SleekBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "Overall Cycle History (Last Year)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "12-month aggregated consumption and usage",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = { onFetchLastYearHistory() },
                        modifier = Modifier.testTag("fetch_last_year_button")
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Fetch Last Year History",
                            tint = SleekBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Total Cycles (Last Year)
                    Column {
                        Text(
                            text = "${cycleHistory.size}",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Cycles in Last Year",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Total Water (Last Year)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${String.format(Locale.getDefault(), "%.0f", cycleHistory.sumOf { it.waterLiters })} L",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = SleekBlue
                        )
                        Text(
                            text = "Total Water",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Total Energy (Last Year)
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${String.format(Locale.getDefault(), "%.1f", cycleHistory.sumOf { it.energyKwh })} kWh",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFD97706)
                        )
                        Text(
                            text = "Total Energy",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Primary Monthly Analytics Dark Card (Sleek Interface style)
    item {
            Card(
                shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = SleekDarkCard),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
                .testTag("analytics_summary_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Monthly Performance",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Emerald500.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "A++ HIGH EFFICIENCY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = Emerald500
                        )
                    }
                }

                // 2x2 grid of key monthly metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Cycles Current Month
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${monthlyAnalytics.cyclesThisMonth}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 34.sp
                            ),
                            color = Color.White,
                            modifier = Modifier.testTag("cycles_this_month_value")
                        )
                        Text(
                            text = "Cycles in ${monthlyAnalytics.currentMonthName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                    }

                    // Monthly Overall Average
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f", monthlyAnalytics.monthlyAverage),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 34.sp
                            ),
                            color = Color.White,
                            modifier = Modifier.testTag("monthly_average_value")
                        )
                        Text(
                            text = "Monthly Overall Avg",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.12f))
                )

                // Secondary Resource Metrics: Water & Energy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Opacity,
                            contentDescription = null,
                            tint = Color(0xFF67E8F9),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "${String.format(Locale.getDefault(), "%.1f", monthlyAnalytics.totalWaterLitersThisMonth)} L",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Avg ${String.format(Locale.getDefault(), "%.1f", monthlyAnalytics.avgWaterPerCycle)} L/cycle",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Color(0xFFFDE047),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${String.format(Locale.getDefault(), "%.2f", monthlyAnalytics.totalKwhThisMonth)} kWh",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Avg ${String.format(Locale.getDefault(), "%.2f", monthlyAnalytics.avgKwhPerCycle)} kWh/cycle",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }

    // Monthly Cycles Graph (Cycles per Calendric Month including current one)
    item {
            MonthlyCyclesGraph(
                monthlyBuckets = monthlyCyclesBuckets,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Section Title: Cycle History Log (Last Year)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Last Year Cycle History (${cycleHistory.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("cycle_history_title")
                )

                Text(
                    text = "12-Month Rolling Log",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Cycle History List items
        items(cycleHistory, key = { it.id }) { cycle ->
            CycleHistoryItem(cycle = cycle)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Visualizes cycles per calendric month including the current one.
 */
@Composable
fun MonthlyCyclesGraph(
    monthlyBuckets: List<MonthlyCycleBucket>,
    modifier: Modifier = Modifier
) {
    var selectedBucket by remember(monthlyBuckets) {
        mutableStateOf(monthlyBuckets.find { it.isCurrentMonth } ?: monthlyBuckets.lastOrNull())
    }

    val maxCount = remember(monthlyBuckets) {
        (monthlyBuckets.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)
    }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("monthly_cycles_graph_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Cycles per Month (Last Year)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("graph_title")
                    )
                    Text(
                        text = "Wash frequency across the past 12 months",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Total cycles this year badge
                val totalYearCycles = monthlyBuckets.sumOf { it.count }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleekBlue.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("graph_total_badge")
                ) {
                    Text(
                        text = "$totalYearCycles Total (12 Mo)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = SleekBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Month details banner
            selectedBucket?.let { sel ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("selected_month_banner"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = sel.monthFullName,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (sel.isCurrentMonth) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Emerald500)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .testTag("current_month_indicator")
                            ) {
                                Text(
                                    text = "CURRENT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Text(
                        text = "${sel.count} cycles • ${String.format(Locale.getDefault(), "%.0f", sel.totalWaterLiters)}L • ${String.format(Locale.getDefault(), "%.1f", sel.totalEnergyKwh)}kWh",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Graph Bars Container (Scrollable horizontally if needed)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(165.dp)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp)
                    .testTag("monthly_cycles_graph"),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                monthlyBuckets.forEach { bucket ->
                    val isSelected = selectedBucket?.month == bucket.month && selectedBucket?.year == bucket.year
                    val barHeightFraction = (bucket.count.toFloat() / maxCount.toFloat()).coerceIn(0.10f, 1f)
                    val barHeight = (105 * barHeightFraction).dp

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedBucket = bucket }
                            .testTag("month_bar_${bucket.monthShortName.lowercase()}")
                    ) {
                        // Count badge above bar
                        Text(
                            text = "${bucket.count}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = if (bucket.isCurrentMonth || isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = when {
                                isSelected -> SleekBlue
                                bucket.isCurrentMonth -> Emerald600
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Styled Bar
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(barHeight)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                                .background(
                                    when {
                                        isSelected -> SleekBlue
                                        bucket.isCurrentMonth -> Emerald500
                                        else -> SleekBlue.copy(alpha = 0.35f)
                                    }
                                )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Month short label
                        Text(
                            text = bucket.monthShortName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = if (bucket.isCurrentMonth || isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = when {
                                isSelected -> SleekBlue
                                bucket.isCurrentMonth -> Emerald600
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )

                        // Dot indicator if current month
                        if (bucket.isCurrentMonth) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(Emerald500)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(7.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CycleHistoryItem(cycle: CycleEntity) {
    val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(cycle.timestamp))

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cycle_item_${cycle.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = cycle.programName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (cycle.isScheduled) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SleekBluePillBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Scheduled",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = SleekBluePillText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "$dateString • ${cycle.durationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "${String.format(Locale.getDefault(), "%.1f", cycle.waterLiters)} L",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Purple600
                    )
                    Text(
                        text = "${String.format(Locale.getDefault(), "%.2f", cycle.energyKwh)} kWh",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Emerald600
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = cycle.efficiencyRating,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
