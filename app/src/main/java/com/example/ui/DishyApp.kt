package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.DishwasherOperationState
import com.example.data.model.DishwasherPowerState
import com.example.ui.components.DishwasherBackground
import com.example.ui.screens.AlertsAndSettingsScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.DishyTheme
import com.example.ui.theme.Slate400
import com.example.ui.theme.SleekBluePillBg
import com.example.ui.theme.SleekBluePillText

enum class NavigationTab(val title: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Home),
    STATS("Stats", Icons.Default.BarChart),
    ALERTS("Alerts", Icons.Default.Notifications)
}

@Composable
fun DishyApp(
    viewModel: DishyViewModel = viewModel()
) {
    val context = LocalContext.current
    val themeMode by viewModel.themeMode.collectAsState()
    val realtimeState by viewModel.realtimeState.collectAsState()
    val monthlyAnalytics by viewModel.monthlyAnalytics.collectAsState()
    val monthlyCyclesBuckets by viewModel.monthlyCyclesBuckets.collectAsState()
    val cycleHistory by viewModel.cycleHistory.collectAsState()
    val credentials by viewModel.credentials.collectAsState()

    var currentTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }

    val isPoweredOn = realtimeState.powerState == DishwasherPowerState.ON
    val isRunning = isPoweredOn && realtimeState.operationState == DishwasherOperationState.RUNNING

    DishyTheme(themeMode = themeMode) {
        DishwasherBackground(
            themeMode = themeMode,
            isPoweredOn = isPoweredOn,
            isRunning = isRunning
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                bottomBar = {
                    SleekBottomNavigation(
                        currentTab = currentTab,
                        onTabSelected = { currentTab = it }
                    )
                }
            ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    NavigationTab.DASHBOARD -> DashboardScreen(
                        realtimeState = realtimeState,
                        monthlyAnalytics = monthlyAnalytics,
                        currentThemeMode = themeMode,
                        onToggleTheme = { viewModel.toggleThemeMode() },
                        onStartCycle = { viewModel.startCycle(realtimeState.currentProgram, realtimeState.options) },
                        onPauseCycle = { viewModel.pauseCycle() },
                        onResumeCycle = { viewModel.resumeCycle() },
                        onStopCycle = { viewModel.stopCycle() },
                        onSelectProgram = { program, options -> viewModel.startCycle(program, options) },
                        onScheduleCycle = { delayMinutes ->
                            viewModel.scheduleCycle(realtimeState.currentProgram, delayMinutes, realtimeState.options)
                        },
                        onOpenSettings = { currentTab = NavigationTab.ALERTS },
                        onNavigateToStats = { currentTab = NavigationTab.STATS },
                        onTogglePower = { viewModel.togglePower() },
                        onToggleDoor = { viewModel.toggleDoorState() },
                        onSyncStatus = { viewModel.syncRealtimeStatus() }
                    )
                    NavigationTab.STATS -> AnalyticsScreen(
                        monthlyAnalytics = monthlyAnalytics,
                        monthlyCyclesBuckets = monthlyCyclesBuckets,
                        cycleHistory = cycleHistory,
                        onExportCsv = { viewModel.shareExportData(context, "csv") },
                        onExportJson = { viewModel.shareExportData(context, "json") },
                        onFetchLastYearHistory = { viewModel.fetchLastYearCycleHistory() }
                    )
                    NavigationTab.ALERTS -> AlertsAndSettingsScreen(
                        realtimeState = realtimeState,
                        credentials = credentials,
                        currentThemeMode = themeMode,
                        onSetThemeMode = { viewModel.setThemeMode(it) },
                        onToggleConnection = { viewModel.toggleConnection() },
                        onTriggerTestAlert = { viewModel.triggerTestAlert(it) },
                        onDismissAlert = { viewModel.dismissAlert(it) },
                        onSaveCredentials = { clientId, clientSecret, token, envUrl ->
                            viewModel.updateCredentials(clientId, clientSecret, token, envUrl)
                        },
                        onVerifyCredentials = { clientId, clientSecret, token, envUrl ->
                            viewModel.verifyCredentials(clientId, clientSecret, token, envUrl)
                        }
                    )
                }
            }
        }
    }
}
}

@Composable
fun SleekBottomNavigation(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .testTag("sleek_bottom_navigation"),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationTab.entries.forEach { tab ->
            val isSelected = currentTab == tab
            val interactionSource = remember { MutableInteractionSource() }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onTabSelected(tab) }
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .testTag("tab_${tab.title.lowercase()}")
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 56.dp, height = 32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) SleekBluePillBg else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = if (isSelected) SleekBluePillText else Slate400,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = tab.title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.8.sp
                    ),
                    color = if (isSelected) SleekBluePillText else Slate400
                )
            }
        }
    }
}
