package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DishwasherOperationState
import com.example.data.model.DishwasherPowerState
import com.example.data.remote.DishwasherRealtimeState
import com.example.ui.MonthlyAnalytics
import com.example.ui.components.CircularProgressDial
import com.example.ui.components.ProgramSelectorSheet
import com.example.ui.components.ScheduleDialog
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.EfficiencyBgLight
import com.example.ui.theme.EfficiencyBorderLight
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Purple600
import com.example.ui.theme.Purple800
import com.example.ui.theme.Rose50
import com.example.ui.theme.Rose600
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate800
import com.example.ui.theme.SleekBlue
import com.example.ui.theme.SleekBluePillBg
import com.example.ui.theme.SleekBluePillText
import com.example.ui.theme.SleekDarkCard
import com.example.ui.theme.UsageBgLight
import com.example.ui.theme.UsageBorderLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    realtimeState: DishwasherRealtimeState,
    monthlyAnalytics: MonthlyAnalytics,
    currentThemeMode: AppThemeMode,
    onToggleTheme: () -> Unit,
    onStartCycle: () -> Unit,
    onPauseCycle: () -> Unit,
    onResumeCycle: () -> Unit,
    onStopCycle: () -> Unit,
    onSelectProgram: (com.example.data.model.DishwasherProgram, com.example.data.model.DishwasherOptions) -> Unit,
    onScheduleCycle: (delayMinutes: Int) -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateToStats: () -> Unit,
    onTogglePower: () -> Unit = {},
    onToggleDoor: () -> Unit = {},
    onSyncStatus: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showProgramSheet by remember { mutableStateOf(false) }
    var showScheduleDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val scrollState = rememberScrollState()

    val syncTransition = rememberInfiniteTransition(label = "sync_spin")
    val spinAngle by syncTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        ),
        label = "spin_angle"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 24.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Dishy",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("app_title")
                )

                // Realtime Connection status badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .testTag("connection_status_badge")
                ) {
                    val statusDotColor = if (realtimeState.isConnected) Emerald500 else Color.Red
                    val statusText = if (realtimeState.isConnected) {
                        "Connected • ${realtimeState.applianceName}"
                    } else {
                        "Disconnected • Offline"
                    }
                    val statusTextColor = if (realtimeState.isConnected) Emerald600 else Color.Red

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusDotColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusText.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = statusTextColor
                    )
                }

                // Realtime Status Sync Badge & Interactive Manual Refresh
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                        .clickable(onClick = onSyncStatus)
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                        .testTag("realtime_sync_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sync Realtime Running Status",
                        tint = if (realtimeState.isSyncing) SleekBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(12.dp)
                            .rotate(if (realtimeState.isSyncing) spinAngle else 0f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (realtimeState.isSyncing) "Syncing..." else "Realtime Synced",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (realtimeState.isSyncing) SleekBlue else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Top action buttons: Power Toggle, Theme Switcher & Settings
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Power Status with Toggle Button on Top
                val isPowerOn = realtimeState.powerState == DishwasherPowerState.ON
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isPowerOn) Emerald500.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            1.dp,
                            if (isPowerOn) Emerald500.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.outlineVariant,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable(onClick = onTogglePower)
                        .padding(start = 10.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
                        .testTag("power_status_toggle")
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isPowerOn) Emerald500 else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPowerOn) "Power: ON" else "Power: OFF",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = if (isPowerOn) Emerald600 else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("power_status_text")
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    IconButton(
                        onClick = onTogglePower,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("power_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Toggle Power",
                            tint = if (isPowerOn) Emerald600 else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Theme button (Switch between Light, Dim, Dark)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .clickable(onClick = onToggleTheme)
                        .testTag("theme_switcher_button"),
                    contentAlignment = Alignment.Center
                ) {
                    val themeIcon = when (currentThemeMode) {
                        AppThemeMode.LIGHT -> Icons.Default.LightMode
                        AppThemeMode.DIM -> Icons.Default.Nightlight
                        AppThemeMode.DARK -> Icons.Default.DarkMode
                    }
                    Icon(
                        imageVector = themeIcon,
                        contentDescription = "Switch Theme (Current: $currentThemeMode)",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Settings button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .clickable(onClick = onOpenSettings)
                        .testTag("header_settings_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Door Open Alert: "In case door is open - show "Door Open" alert"
        val isDoorOpen = realtimeState.doorState.equals("Open", ignoreCase = true)
        if (isDoorOpen) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Rose50),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, com.example.ui.theme.Rose600),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("door_open_alert")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(com.example.ui.theme.Rose600.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Door Open Alert",
                            tint = com.example.ui.theme.Rose600,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Door Open",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = com.example.ui.theme.Rose600,
                            modifier = Modifier.testTag("door_open_alert_title")
                        )
                        Text(
                            text = "Dishwasher door is currently open. Close door to start or resume wash cycle.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1F2937)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onToggleDoor,
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.Rose600, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("close_door_alert_button")
                    ) {
                        Text("Close Door", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // Current Cycle Status Banner: "When application loads, it should show the current cycle status"
        val isPowerOnState = realtimeState.powerState == DishwasherPowerState.ON
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    !isPowerOnState -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    realtimeState.operationState == DishwasherOperationState.RUNNING -> SleekBlue.copy(alpha = 0.12f)
                    realtimeState.operationState == DishwasherOperationState.PAUSED -> Color(0xFFFEF3C7)
                    realtimeState.operationState == DishwasherOperationState.FINISHED -> Emerald500.copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                }
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                when {
                    !isPowerOnState -> MaterialTheme.colorScheme.outlineVariant
                    realtimeState.operationState == DishwasherOperationState.RUNNING -> SleekBlue.copy(alpha = 0.4f)
                    realtimeState.operationState == DishwasherOperationState.PAUSED -> Color(0xFFF59E0B)
                    realtimeState.operationState == DishwasherOperationState.FINISHED -> Emerald500.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.outlineVariant
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .testTag("current_cycle_status_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    !isPowerOnState -> Color.Gray
                                    realtimeState.operationState == DishwasherOperationState.RUNNING -> SleekBlue
                                    realtimeState.operationState == DishwasherOperationState.PAUSED -> Color(0xFFD97706)
                                    realtimeState.operationState == DishwasherOperationState.FINISHED -> Emerald600
                                    else -> Color.Gray
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CURRENT CYCLE STATUS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                fontSize = 10.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val statusSummary = when {
                            !isPowerOnState -> "Appliance Powered Off (Standby)"
                            realtimeState.operationState == DishwasherOperationState.RUNNING ->
                                "Running: ${realtimeState.currentProgram.name} • ${realtimeState.currentPhase.displayName}"
                            realtimeState.operationState == DishwasherOperationState.PAUSED ->
                                "Paused: ${realtimeState.currentProgram.name}"
                            realtimeState.operationState == DishwasherOperationState.DELAYED_START ->
                                "Scheduled Delay: ${realtimeState.currentProgram.name}"
                            realtimeState.operationState == DishwasherOperationState.FINISHED ->
                                "Cycle Finished: Sparkling Clean"
                            else -> "Ready to Start: ${realtimeState.currentProgram.name}"
                        }
                        Text(
                            text = statusSummary,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("current_cycle_status_summary")
                        )
                    }
                }

                val statusChipText = when {
                    !isPowerOnState -> "OFF"
                    realtimeState.operationState == DishwasherOperationState.RUNNING -> "${realtimeState.remainingSeconds / 60}m Left"
                    realtimeState.operationState == DishwasherOperationState.PAUSED -> "PAUSED"
                    realtimeState.operationState == DishwasherOperationState.FINISHED -> "DONE"
                    else -> "READY"
                }
                val statusChipBg = when {
                    !isPowerOnState -> Color.Gray.copy(alpha = 0.2f)
                    realtimeState.operationState == DishwasherOperationState.RUNNING -> SleekBlue
                    realtimeState.operationState == DishwasherOperationState.PAUSED -> Color(0xFFF59E0B)
                    realtimeState.operationState == DishwasherOperationState.FINISHED -> Emerald600
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                val statusChipTextColor = if (isPowerOnState && (realtimeState.operationState == DishwasherOperationState.RUNNING || realtimeState.operationState == DishwasherOperationState.FINISHED || realtimeState.operationState == DishwasherOperationState.PAUSED)) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onSurface
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(statusChipBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("current_cycle_status_chip")
                ) {
                    Text(
                        text = statusChipText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusChipTextColor
                    )
                }
            }
        }

        // Active Alerts Warning Banner (if any)
        if (realtimeState.activeAlerts.isNotEmpty()) {
            val alert = realtimeState.activeAlerts.first()
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Rose50),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.Rose600.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clickable(onClick = onOpenSettings)
                    .testTag("dashboard_alert_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "⚠️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = com.example.ui.theme.Rose600
                        )
                        Text(
                            text = alert.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = com.example.ui.theme.Slate800,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Main Dishwasher Dial Card (rounded 32dp, Sleek Interface style)
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("main_dial_card")
        ) {
            if (realtimeState.powerState == DishwasherPowerState.OFF) {
                // Power Off Standby View
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 20.dp)
                        .testTag("dial_power_off_state")
                ) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                            .clickable(onClick = onTogglePower)
                            .testTag("dial_power_toggle_target"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = "Turn Power On",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "STANDBY",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Dishwasher is Powered Off",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Power is switched off. Turn on to start washing.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onTogglePower,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SleekBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(54.dp)
                            .testTag("dial_turn_power_on_button")
                    ) {
                        Icon(Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Turn Power On", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 20.dp)
                ) {
                    // Circular Progress Dial with Remaining Time Readout
                    CircularProgressDial(
                        remainingSeconds = realtimeState.remainingSeconds,
                        progressPercent = realtimeState.progressPercent,
                        trackColor = MaterialTheme.colorScheme.outlineVariant,
                        progressColor = SleekBlue
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Program Title & Phase
                    Text(
                        text = realtimeState.currentProgram.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("active_program_name")
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    val phaseText = when (realtimeState.operationState) {
                        DishwasherOperationState.RUNNING -> "Currently: ${realtimeState.currentPhase.displayName}"
                        DishwasherOperationState.PAUSED -> "Status: Cycle Paused"
                        DishwasherOperationState.DELAYED_START -> "Status: Delayed Start Scheduled"
                        DishwasherOperationState.FINISHED -> "Status: Cycle Finished • Sparkling Clean"
                        else -> "Status: Ready to Start"
                    }

                    Text(
                        text = phaseText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("active_phase_text")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Estimated Cycle Duration & SpeedPerfect indicator
                    val estDurationMinutes = realtimeState.currentProgram.getEstimatedDurationMinutes(realtimeState.options)
                    val formattedEstDuration = realtimeState.currentProgram.formatDuration(estDurationMinutes)
                    val isSpeedPerfect = realtimeState.options.speedPerfect

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSpeedPerfect) SleekBlue.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("estimated_duration_badge")
                    ) {
                        Icon(
                            Icons.Default.Speed,
                            contentDescription = null,
                            tint = if (isSpeedPerfect) SleekBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Est. Total: $formattedEstDuration",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isSpeedPerfect) SleekBlue else MaterialTheme.colorScheme.onSurface
                        )
                        if (isSpeedPerfect) {
                            Text(
                                text = "• SpeedPerfect Enabled",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = SleekBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Action Control Buttons: Pause / Resume + Cancel
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (realtimeState.operationState) {
                            DishwasherOperationState.RUNNING -> {
                                // Pause button: Pill styled
                                Button(
                                    onClick = onPauseCycle,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SleekBluePillBg,
                                        contentColor = SleekBluePillText
                                    ),
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .testTag("pause_cycle_button")
                                ) {
                                    Icon(Icons.Default.Pause, contentDescription = "Pause", modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Pause", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }

                                // Cancel / Stop button: Square rounded rose
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Rose50)
                                        .clickable(onClick = onStopCycle)
                                        .testTag("stop_cycle_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Cancel Cycle",
                                        tint = Rose600,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            DishwasherOperationState.PAUSED -> {
                                // Resume button
                                Button(
                                    onClick = onResumeCycle,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SleekBlue,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .testTag("resume_cycle_button")
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Resume", modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Resume Cycle", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }

                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Rose50)
                                        .clickable(onClick = onStopCycle)
                                        .testTag("stop_cycle_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Cancel Cycle",
                                        tint = Rose600,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            else -> {
                                // Start or Select Program
                                Button(
                                    onClick = onStartCycle,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SleekBluePillBg,
                                        contentColor = SleekBluePillText
                                    ),
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                        .testTag("start_cycle_button")
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Start", modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Start Wash", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }

                                // Program Picker shortcut
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { showProgramSheet = true }
                                        .testTag("open_program_picker_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Tune,
                                        contentDescription = "Change Program",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2-Column Grid: Current Usage & Efficiency
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Current Usage Card (Purple themed)
            val usageBg = when (currentThemeMode) {
                AppThemeMode.LIGHT -> UsageBgLight
                AppThemeMode.DIM -> com.example.ui.theme.SleekDimUsageBg
                AppThemeMode.DARK -> com.example.ui.theme.SleekDarkUsageBg
            }
            val usageBorder = when (currentThemeMode) {
                AppThemeMode.LIGHT -> UsageBorderLight
                AppThemeMode.DIM -> com.example.ui.theme.SleekDimUsageBorder
                AppThemeMode.DARK -> com.example.ui.theme.SleekDarkUsageBorder
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(usageBg)
                    .border(1.dp, usageBorder, RoundedCornerShape(24.dp))
                    .padding(14.dp)
                    .testTag("metric_current_usage_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .shadow(1.dp, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Opacity,
                                contentDescription = null,
                                tint = Purple600,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "CURRENT USAGE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = Purple800
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.1f", realtimeState.currentWaterLiters),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = " L",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = Slate400,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    // Progress bar indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(UsageBorderLight)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.65f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Purple600)
                        )
                    }
                }
            }

            // Efficiency Card (Emerald themed)
            val effBg = when (currentThemeMode) {
                AppThemeMode.LIGHT -> EfficiencyBgLight
                AppThemeMode.DIM -> com.example.ui.theme.SleekDimEfficiencyBg
                AppThemeMode.DARK -> com.example.ui.theme.SleekDarkEfficiencyBg
            }
            val effBorder = when (currentThemeMode) {
                AppThemeMode.LIGHT -> EfficiencyBorderLight
                AppThemeMode.DIM -> com.example.ui.theme.SleekDimEfficiencyBorder
                AppThemeMode.DARK -> com.example.ui.theme.SleekDarkEfficiencyBorder
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(effBg)
                    .border(1.dp, effBorder, RoundedCornerShape(24.dp))
                    .padding(14.dp)
                    .testTag("metric_efficiency_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .shadow(1.dp, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Emerald600,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "EFFICIENCY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = Emerald700
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = realtimeState.efficiencyRating,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "High Resource Save",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp
                        ),
                        color = Emerald700
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Monthly Insights Dark Card (Sleek Interface style)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(SleekDarkCard)
                .clickable(onClick = onNavigateToStats)
                .padding(20.dp)
                .testTag("monthly_insights_card")
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Monthly Insights",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = monthlyAnalytics.currentMonthName.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.2.sp
                            ),
                            color = Color.White
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "${monthlyAnalytics.cyclesThisMonth}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "Total Cycles",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = String.format("%.2f", monthlyAnalytics.avgKwhPerCycle),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "Avg. kWh / Cycle",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Controls: Select Program, Schedule Start, and Door Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showProgramSheet = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("quick_select_program_button")
            ) {
                Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Programs", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = { showScheduleDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("quick_schedule_button")
            ) {
                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Schedule", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            val isDoorOpenQuick = realtimeState.doorState.equals("Open", ignoreCase = true)
            Button(
                onClick = onToggleDoor,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDoorOpenQuick) Rose50 else MaterialTheme.colorScheme.surface,
                    contentColor = if (isDoorOpenQuick) com.example.ui.theme.Rose600 else MaterialTheme.colorScheme.onSurface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDoorOpenQuick) com.example.ui.theme.Rose600 else MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("quick_door_toggle_button")
            ) {
                Icon(
                    imageVector = if (isDoorOpenQuick) Icons.Default.Warning else Icons.Default.MeetingRoom,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isDoorOpenQuick) "Door: Open" else "Door: Closed",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    // Program Selector Bottom Sheet
    if (showProgramSheet) {
        ProgramSelectorSheet(
            sheetState = sheetState,
            currentProgram = realtimeState.currentProgram,
            currentOptions = realtimeState.options,
            onDismiss = { showProgramSheet = false },
            onProgramSelected = { program, options ->
                onSelectProgram(program, options)
                showProgramSheet = false
            }
        )
    }

    // Schedule Dialog
    if (showScheduleDialog) {
        ScheduleDialog(
            currentProgram = realtimeState.currentProgram,
            options = realtimeState.options,
            onDismiss = { showScheduleDialog = false },
            onScheduleConfirmed = { delayMinutes ->
                onScheduleCycle(delayMinutes)
                showScheduleDialog = false
            }
        )
    }
}
