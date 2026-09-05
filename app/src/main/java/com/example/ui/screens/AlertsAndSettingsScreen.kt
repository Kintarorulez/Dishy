package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.ApplianceAlert
import com.example.data.remote.ApprovalStatus
import com.example.data.remote.DishwasherRealtimeState
import com.example.data.remote.HomeConnectCredentials
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Rose50
import com.example.ui.theme.Rose600
import com.example.ui.theme.SleekBlue
import com.example.ui.theme.SleekBluePillBg
import com.example.ui.theme.SleekBluePillText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlertsAndSettingsScreen(
    realtimeState: DishwasherRealtimeState,
    credentials: HomeConnectCredentials,
    currentThemeMode: AppThemeMode,
    onSetThemeMode: (AppThemeMode) -> Unit,
    onToggleConnection: () -> Unit,
    onTriggerTestAlert: (String) -> Unit,
    onDismissAlert: (String) -> Unit,
    onSaveCredentials: (clientId: String, clientSecret: String, token: String, envUrl: String) -> Unit,
    onVerifyCredentials: (clientId: String, clientSecret: String, token: String, envUrl: String) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    var clientIdInput by remember(credentials.clientId) { mutableStateOf(credentials.clientId) }
    var clientSecretInput by remember(credentials.clientSecret) { mutableStateOf(credentials.clientSecret) }
    var accessTokenInput by remember(credentials.accessToken) { mutableStateOf(credentials.accessToken) }
    var envUrlInput by remember(credentials.environmentUrl) { mutableStateOf(credentials.environmentUrl) }

    var hasPostNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPostNotificationPermission = isGranted
        val msg = if (isGranted) "Push notifications enabled for Dishy!" else "Notifications permission denied."
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "Settings & Alerts",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("settings_title")
            )
            Text(
                text = "Appliance Connection, Themes & Push Notifications",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 1. Theme Switcher Section (Light, Dim, Dark)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Display Theme",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Choose your preferred ambient look: Light, Dim, or Dark",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeOptionCard(
                        title = "Light",
                        icon = Icons.Default.LightMode,
                        isSelected = currentThemeMode == AppThemeMode.LIGHT,
                        onClick = { onSetThemeMode(AppThemeMode.LIGHT) },
                        modifier = Modifier.weight(1f)
                    )

                    ThemeOptionCard(
                        title = "Dim",
                        icon = Icons.Default.Nightlight,
                        isSelected = currentThemeMode == AppThemeMode.DIM,
                        onClick = { onSetThemeMode(AppThemeMode.DIM) },
                        modifier = Modifier.weight(1f)
                    )

                    ThemeOptionCard(
                        title = "Dark",
                        icon = Icons.Default.DarkMode,
                        isSelected = currentThemeMode == AppThemeMode.DARK,
                        onClick = { onSetThemeMode(AppThemeMode.DARK) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 2. Realtime Connection & Appliance Status
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Appliance Connection",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (realtimeState.isConnected) "Realtime Home Connect Stream Active" else "Disconnected (Offline)",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (realtimeState.isConnected) Emerald600 else Rose600
                        )
                    }

                    Switch(
                        checked = realtimeState.isConnected,
                        onCheckedChange = { onToggleConnection() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Emerald500
                        ),
                        modifier = Modifier.testTag("connection_toggle_switch")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DetailRow(label = "Model", value = "${realtimeState.applianceName} (${realtimeState.modelNumber})")
                    DetailRow(label = "Door State", value = realtimeState.doorState)
                    DetailRow(label = "Power", value = realtimeState.powerState.name)
                    DetailRow(label = "Operation State", value = realtimeState.operationState.name)
                    DetailRow(label = "Protocol", value = realtimeState.connectionMode)
                }
            }
        }

        // 3. Push Notifications & Error Alert System
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = SleekBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Push Notifications",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPostNotificationPermission) {
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                            colors = ButtonDefaults.buttonColors(containerColor = SleekBluePillBg, contentColor = SleekBluePillText),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("enable_notifications_button")
                        ) {
                            Text("Enable", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Emerald500.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Active", fontSize = 11.sp, color = Emerald500, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = "Receive alerts when cycle finishes or if error occurs (low rinse aid, salt empty, water inlet check).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = "Test Alerts Simulation:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onTriggerTestAlert("rinse_aid") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_rinse_aid_alert_button")
                    ) {
                        Text("Rinse Aid", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { onTriggerTestAlert("salt") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_salt_alert_button")
                    ) {
                        Text("Salt Low", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { onTriggerTestAlert("water_tap") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_water_alert_button")
                    ) {
                        Text("Water Tap", fontSize = 11.sp)
                    }
                }

                if (realtimeState.activeAlerts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Active Alerts (${realtimeState.activeAlerts.size}):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Rose600
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        realtimeState.activeAlerts.forEach { alert ->
                            ActiveAlertItem(alert = alert, onDismiss = { onDismissAlert(alert.id) })
                        }
                    }
                }
            }
        }

        // 4. Home Connect API Credentials & Integration
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = SleekBlue)
                    Text(
                        text = "Home Connect API Credentials",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Configure your Bosch Home Connect developer credentials to synchronize with your physical appliance via https://api.home-connect.com.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                // Approval Status Indicator Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (credentials.approvalStatus) {
                            ApprovalStatus.APPROVED -> Emerald500.copy(alpha = 0.12f)
                            ApprovalStatus.CHECKING -> SleekBlue.copy(alpha = 0.12f)
                            ApprovalStatus.INVALID -> Rose50
                            ApprovalStatus.PENDING -> Color(0xFFFEF3C7)
                        }
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (credentials.approvalStatus) {
                            ApprovalStatus.APPROVED -> Emerald500.copy(alpha = 0.35f)
                            ApprovalStatus.CHECKING -> SleekBlue.copy(alpha = 0.35f)
                            ApprovalStatus.INVALID -> Rose600.copy(alpha = 0.35f)
                            ApprovalStatus.PENDING -> Color(0xFFF59E0B).copy(alpha = 0.35f)
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("approval_status_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = when (credentials.approvalStatus) {
                                        ApprovalStatus.APPROVED -> Icons.Default.Verified
                                        ApprovalStatus.CHECKING -> Icons.Default.Sync
                                        ApprovalStatus.INVALID -> Icons.Default.ErrorOutline
                                        ApprovalStatus.PENDING -> Icons.Default.Security
                                    },
                                    contentDescription = null,
                                    tint = when (credentials.approvalStatus) {
                                        ApprovalStatus.APPROVED -> Emerald600
                                        ApprovalStatus.CHECKING -> SleekBlue
                                        ApprovalStatus.INVALID -> Rose600
                                        ApprovalStatus.PENDING -> Color(0xFFD97706)
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Approval Status",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (credentials.approvalStatus) {
                                            ApprovalStatus.APPROVED -> Emerald500
                                            ApprovalStatus.CHECKING -> SleekBlue
                                            ApprovalStatus.INVALID -> Rose600
                                            ApprovalStatus.PENDING -> Color(0xFFD97706)
                                        }
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                    .testTag("approval_status_badge")
                            ) {
                                Text(
                                    text = credentials.approvalStatus.label.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = credentials.approvalDetails,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("approval_details_text")
                        )

                        val verifyTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                        val verifyTimeStr = verifyTimeFormat.format(Date(credentials.lastVerifiedTimestamp))
                        Text(
                            text = "Last Verified: $verifyTimeStr",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                onVerifyCredentials(clientIdInput, clientSecretInput, accessTokenInput, envUrlInput)
                                Toast.makeText(context, "Verifying credentials with Home Connect portal...", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("verify_approval_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Verify ID & Secret Approval", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                // Unmasked Client ID
                OutlinedTextField(
                    value = clientIdInput,
                    onValueChange = { clientIdInput = it },
                    label = { Text("Client ID (Unmasked)") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(clientIdInput))
                            Toast.makeText(context, "Client ID copied to clipboard", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Client ID", modifier = Modifier.size(18.dp), tint = SleekBlue)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("client_id_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Unmasked Client Secret
                OutlinedTextField(
                    value = clientSecretInput,
                    onValueChange = { clientSecretInput = it },
                    label = { Text("Client Secret (Unmasked)") },
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    trailingIcon = {
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(clientSecretInput))
                            Toast.makeText(context, "Client Secret copied to clipboard", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Client Secret", modifier = Modifier.size(18.dp), tint = SleekBlue)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("client_secret_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = envUrlInput,
                    onValueChange = { envUrlInput = it },
                    label = { Text("Client Environment URL") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("env_url_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = accessTokenInput,
                    onValueChange = { accessTokenInput = it },
                    label = { Text("OAuth Bearer Access Token (Optional)") },
                    placeholder = { Text("Bearer token from Home Connect authorization") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("access_token_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        onSaveCredentials(clientIdInput, clientSecretInput, accessTokenInput, envUrlInput)
                        onVerifyCredentials(clientIdInput, clientSecretInput, accessTokenInput, envUrlInput)
                        Toast.makeText(context, "Home Connect configuration saved & syncing!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekBluePillBg, contentColor = SleekBluePillText),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_credentials_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Sync Home Connect", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val lastSyncTimeStr = timeFormat.format(Date(realtimeState.lastSyncedTimestamp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .testTag("settings_sync_status")
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (realtimeState.isSyncing) SleekBlue else Emerald500)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${realtimeState.syncStatusMessage} (Last: $lastSyncTimeStr)",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Application Version & Creator Credit
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Dishy v${com.example.BuildConfig.VERSION_NAME} (Build ${com.example.BuildConfig.BUILD_NUMBER})",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.testTag("settings_version_text")
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Application created by Liran Stern",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("created_by_credit_line")
            )
        }
    }
}

@Composable
fun ThemeOptionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) SleekBlue else MaterialTheme.colorScheme.outlineVariant
    val bg = if (isSelected) SleekBlue.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val contentColor = if (isSelected) SleekBlue else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = contentColor
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun ActiveAlertItem(alert: ApplianceAlert, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Rose50)
            .border(1.dp, Rose600.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = alert.title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Rose600)
            Text(text = alert.message, style = MaterialTheme.typography.bodySmall, color = Color(0xFF1F2937))
        }
        IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Rose600, modifier = Modifier.size(18.dp))
        }
    }
}
