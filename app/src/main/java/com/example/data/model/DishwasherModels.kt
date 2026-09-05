package com.example.data.model

import java.util.Locale

enum class DishwasherPowerState {
    OFF,
    STANDBY,
    ON
}

enum class DishwasherOperationState(val displayName: String) {
    INACTIVE("Inactive"),
    READY("Ready"),
    DELAYED_START("Scheduled Delay"),
    RUNNING("Running"),
    PAUSED("Paused"),
    ACTION_REQUIRED("Action Required"),
    FINISHED("Cycle Completed"),
    ABORTING("Aborting"),
    ERROR("Error")
}

enum class WashPhase(val displayName: String) {
    PRE_RINSE("Pre-Rinse Phase"),
    MAIN_WASH("Main Washing Phase"),
    INTERMEDIATE_RINSE("Intermediate Rinse"),
    FINAL_RINSE("Final Heated Rinse"),
    DRYING("Zeolith® Drying Phase"),
    FINISHED("Cycle Completed")
}

data class DishwasherProgram(
    val key: String,
    val name: String,
    val temperature: String,
    val durationMinutes: Int,
    val defaultWaterLiters: Double,
    val defaultEnergyKwh: Double,
    val efficiencyRating: String,
    val description: String
) {
    fun getEstimatedDurationMinutes(options: DishwasherOptions = DishwasherOptions()): Int {
        val baseMinutes = if (options.speedPerfect) {
            when (key) {
                ECO_50.key -> 75       // 195 min reduced to 75 min (62% faster)
                AUTO_45_65.key -> 60   // 150 min reduced to 60 min (60% faster)
                INTENSIVE_70.key -> 75 // 135 min reduced to 75 min (44% faster)
                QUICK_45.key -> 25     // 35 min reduced to 25 min
                SILENCE_50.key -> 95   // 240 min reduced to 95 min (60% faster)
                GLASS_40.key -> 45     // 90 min reduced to 45 min (50% faster)
                else -> (durationMinutes * 0.45).toInt().coerceAtLeast(20)
            }
        } else {
            durationMinutes
        }
        var total = baseMinutes
        if (options.extraDry) total += 10
        if (options.hygienePlus) total += 8
        return total
    }

    fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
    }

    fun getFormattedEstimatedDuration(options: DishwasherOptions = DishwasherOptions()): String {
        return formatDuration(getEstimatedDurationMinutes(options))
    }

    fun getSavedMinutes(options: DishwasherOptions = DishwasherOptions()): Int {
        if (!options.speedPerfect) return 0
        return (durationMinutes - getEstimatedDurationMinutes(options.copy(extraDry = false, hygienePlus = false))).coerceAtLeast(0)
    }

    companion object {
        val ECO_50 = DishwasherProgram(
            key = "Dishcare.Dishwasher.Program.Eco50",
            name = "Eco 50° Cycle",
            temperature = "50°C",
            durationMinutes = 195,
            defaultWaterLiters = 9.4,
            defaultEnergyKwh = 0.82,
            efficiencyRating = "A++",
            description = "Maximum energy & water conservation for normal soil."
        )

        val AUTO_45_65 = DishwasherProgram(
            key = "Dishcare.Dishwasher.Program.Auto2",
            name = "Auto 45-65°",
            temperature = "45-65°C",
            durationMinutes = 150,
            defaultWaterLiters = 11.2,
            defaultEnergyKwh = 1.05,
            efficiencyRating = "A+",
            description = "Sensor-adjusted wash based on dish soiling."
        )

        val INTENSIVE_70 = DishwasherProgram(
            key = "Dishcare.Dishwasher.Program.Intensiv70",
            name = "Intensive 70°",
            temperature = "70°C",
            durationMinutes = 135,
            defaultWaterLiters = 14.2,
            defaultEnergyKwh = 1.35,
            efficiencyRating = "A",
            description = "High pressure & heat for pots, pans and dried grease."
        )

        val QUICK_45 = DishwasherProgram(
            key = "Dishcare.Dishwasher.Program.Quick45",
            name = "Quick 45°",
            temperature = "45°C",
            durationMinutes = 35,
            defaultWaterLiters = 10.0,
            defaultEnergyKwh = 0.80,
            efficiencyRating = "A+",
            description = "Fast clean for lightly soiled glassware and plates."
        )

        val SILENCE_50 = DishwasherProgram(
            key = "Dishcare.Dishwasher.Program.Silence50",
            name = "Silence 50°",
            temperature = "50°C",
            durationMinutes = 240,
            defaultWaterLiters = 9.8,
            defaultEnergyKwh = 0.78,
            efficiencyRating = "A+++",
            description = "Ultra quiet 39dB wash ideal for overnight runs."
        )

        val GLASS_40 = DishwasherProgram(
            key = "Dishcare.Dishwasher.Program.Glass40",
            name = "Glass 40°",
            temperature = "40°C",
            durationMinutes = 90,
            defaultWaterLiters = 10.8,
            defaultEnergyKwh = 0.75,
            efficiencyRating = "A++",
            description = "Gentle care for fine stemware with Zeolith drying."
        )

        val ALL_PROGRAMS = listOf(ECO_50, AUTO_45_65, INTENSIVE_70, QUICK_45, SILENCE_50, GLASS_40)
    }
}

data class DishwasherOptions(
    val halfLoad: Boolean = false,
    val extraDry: Boolean = false,
    val speedPerfect: Boolean = true, // SpeedPerfect mode enabled by default
    val hygienePlus: Boolean = false,
    val intensiveZone: Boolean = false
) {
    fun toSummaryList(): List<String> {
        val list = mutableListOf<String>()
        if (speedPerfect) list.add("SpeedPerfect")
        if (extraDry) list.add("ExtraDry")
        if (halfLoad) list.add("HalfLoad")
        if (hygienePlus) list.add("HygienePlus")
        if (intensiveZone) list.add("IntensiveZone")
        return list
    }
}

data class ApplianceAlert(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val severity: AlertSeverity,
    val isResolved: Boolean = false
)

enum class AlertSeverity {
    INFO,
    WARNING,
    CRITICAL
}

enum class DoorState(val label: String) {
    CLOSED("Door Closed"),
    OPEN("Door Open"),
    LOCKED("Door Locked")
}

data class ApplianceInfo(
    val haId: String = "BOSCH-DISH-00129",
    val name: String = "Bosch Series 6 SuperSilence",
    val brand: String = "Bosch",
    val enumber: String = "SMV6ZCX07E/01",
    val ipAddress: String = "192.168.1.142",
    val firmwareVersion: String = "v3.18.4"
)
