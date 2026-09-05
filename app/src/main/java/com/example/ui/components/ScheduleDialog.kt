package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DishwasherOptions
import com.example.data.model.DishwasherProgram
import com.example.ui.theme.SleekBlue
import com.example.ui.theme.SleekBluePillBg
import com.example.ui.theme.SleekBluePillText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ScheduleDialog(
    currentProgram: DishwasherProgram,
    options: DishwasherOptions = DishwasherOptions(),
    onDismiss: () -> Unit,
    onScheduleConfirmed: (delayMinutes: Int) -> Unit
) {
    var selectedDelayMinutes by remember { mutableIntStateOf(120) } // default 2h (off-peak)

    val delayOptions = listOf(
        30 to "30 min",
        60 to "1 hour",
        120 to "2 hours (Off-peak)",
        240 to "4 hours",
        480 to "8 hours (Overnight)"
    )

    val estDurationMinutes = currentProgram.getEstimatedDurationMinutes(options)
    val formattedDuration = currentProgram.formatDuration(estDurationMinutes)

    val targetTime = System.currentTimeMillis() + (selectedDelayMinutes * 60 * 1000L)
    val finishTime = targetTime + (estDurationMinutes * 60 * 1000L)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val targetTimeString = timeFormat.format(Date(targetTime))
    val finishTimeString = timeFormat.format(Date(finishTime))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Schedule Cycle",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Bosch Series 6 • ${currentProgram.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "Select delayed start time for energy-efficient off-peak washing:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    delayOptions.forEach { (minutes, label) ->
                        val isSelected = minutes == selectedDelayMinutes
                        val bg = if (isSelected) SleekBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        val textColor = if (isSelected) SleekBlue else MaterialTheme.colorScheme.onSurface

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(bg)
                                .clickable { selectedDelayMinutes = minutes }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = textColor
                            )
                            if (isSelected) {
                                Text(
                                    text = "Selected",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = SleekBlue
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Est. Cycle Duration:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formattedDuration + if (options.speedPerfect) " (SpeedPerfect)" else "",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (options.speedPerfect) SleekBlue else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                        Text(
                            text = "Starts at $targetTimeString • Completes at $finishTimeString",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onScheduleConfirmed(selectedDelayMinutes)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SleekBluePillBg,
                    contentColor = SleekBluePillText
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("confirm_schedule_button")
            ) {
                Text("Schedule Start", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
