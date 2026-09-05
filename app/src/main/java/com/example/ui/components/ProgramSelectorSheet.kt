package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DishwasherOptions
import com.example.data.model.DishwasherProgram
import com.example.ui.theme.Emerald500
import com.example.ui.theme.SleekBlue
import com.example.ui.theme.SleekBluePillBg
import com.example.ui.theme.SleekBluePillText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramSelectorSheet(
    sheetState: SheetState,
    currentProgram: DishwasherProgram,
    currentOptions: DishwasherOptions,
    onDismiss: () -> Unit,
    onProgramSelected: (DishwasherProgram, DishwasherOptions) -> Unit
) {
    var selectedProgram by remember { mutableStateOf(currentProgram) }
    var options by remember { mutableStateOf(currentOptions) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        modifier = Modifier.testTag("program_selector_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dishwasher Programs",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Bosch Home Connect Wash Profiles",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Programs list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                items(DishwasherProgram.ALL_PROGRAMS) { program ->
                    val isSelected = program.key == selectedProgram.key
                    val borderColor = if (isSelected) SleekBlue else MaterialTheme.colorScheme.outline
                    val bgColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(bgColor)
                            .border(1.5.dp, borderColor, RoundedCornerShape(20.dp))
                            .clickable { selectedProgram = program }
                            .padding(14.dp)
                            .testTag("program_item_${program.name.replace(" ", "_")}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = program.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Emerald500.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = program.efficiencyRating,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        color = Emerald500
                                    )
                                }
                            }
                            Text(
                                text = program.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val estMinutes = program.getEstimatedDurationMinutes(options)
                                val formattedEst = program.formatDuration(estMinutes)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "⏱ $formattedEst",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (options.speedPerfect) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (options.speedPerfect) SleekBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (options.speedPerfect) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${program.durationMinutes}m",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                                Text(
                                    text = "💧 ${program.defaultWaterLiters} L",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "⚡ ${program.defaultEnergyKwh} kWh",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(SleekBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = androidx.compose.ui.graphics.Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cycle Options",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (options.speedPerfect) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SleekBlue.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "⚡ SpeedPerfect Enabled",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = SleekBlue
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = options.speedPerfect,
                    onClick = { options = options.copy(speedPerfect = !options.speedPerfect) },
                    label = {
                        Text(
                            text = if (options.speedPerfect) "SpeedPerfect (On)" else "SpeedPerfect",
                            fontSize = 12.sp,
                            fontWeight = if (options.speedPerfect) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                FilterChip(
                    selected = options.extraDry,
                    onClick = { options = options.copy(extraDry = !options.extraDry) },
                    label = { Text("Extra Dry", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.WbSunny, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                FilterChip(
                    selected = options.halfLoad,
                    onClick = { options = options.copy(halfLoad = !options.halfLoad) },
                    label = { Text("Half Load", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Start / Apply Button
            val estDurationText = selectedProgram.formatDuration(selectedProgram.getEstimatedDurationMinutes(options))
            Button(
                onClick = {
                    onProgramSelected(selectedProgram, options)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SleekBluePillBg,
                    contentColor = SleekBluePillText
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("apply_and_start_program_button")
            ) {
                Text(
                    text = "Apply & Start ${selectedProgram.name} • $estDurationText",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
