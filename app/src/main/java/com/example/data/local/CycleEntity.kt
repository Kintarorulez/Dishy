package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wash_cycles")
data class CycleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val programKey: String,
    val programName: String,
    val timestamp: Long,
    val durationMinutes: Int,
    val waterLiters: Double,
    val energyKwh: Double,
    val efficiencyRating: String,
    val status: String,
    val isScheduled: Boolean = false,
    val scheduledEpochSeconds: Long? = null
)
