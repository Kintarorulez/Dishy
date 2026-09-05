package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CycleEntity
import com.example.data.local.DishyDatabase
import com.example.data.model.AlertSeverity
import com.example.data.model.ApplianceAlert
import com.example.data.model.DishwasherOptions
import com.example.data.model.DishwasherPowerState
import com.example.data.model.DishwasherProgram
import com.example.data.remote.DishwasherRealtimeState
import com.example.data.remote.HomeConnectCredentials
import com.example.data.remote.HomeConnectRepository
import com.example.notification.DishyNotificationHelper
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class MonthlyAnalytics(
    val currentMonthName: String,
    val cyclesThisMonth: Int,
    val monthlyAverage: Double,
    val totalWaterLitersThisMonth: Double,
    val avgWaterPerCycle: Double,
    val avgKwhPerCycle: Double,
    val totalKwhThisMonth: Double,
    val estimatedSavingsEuro: Double
)

data class MonthlyCycleBucket(
    val year: Int,
    val month: Int,
    val monthShortName: String,
    val monthFullName: String,
    val count: Int,
    val totalWaterLiters: Double,
    val totalEnergyKwh: Double,
    val isCurrentMonth: Boolean
)

class DishyViewModel(application: Application) : AndroidViewModel(application) {

    private val database = DishyDatabase.getDatabase(application, viewModelScope)
    private val notificationHelper = DishyNotificationHelper(application)
    private val repository = HomeConnectRepository(
        context = application,
        cycleDao = database.cycleDao(),
        notificationHelper = notificationHelper,
        scope = viewModelScope
    )

    private val _themeMode = MutableStateFlow(AppThemeMode.LIGHT)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    val realtimeState: StateFlow<DishwasherRealtimeState> = repository.state
    val credentials: StateFlow<HomeConnectCredentials> = repository.credentials

    val cycleHistory: StateFlow<List<CycleEntity>> = repository.cycleHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val monthlyCyclesBuckets: StateFlow<List<MonthlyCycleBucket>> = cycleHistory.map { list ->
        calculateMonthlyCycleBuckets(list)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        emptyList()
    )

    val monthlyAnalytics: StateFlow<MonthlyAnalytics> = cycleHistory.map { list ->
        calculateAnalytics(list)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        MonthlyAnalytics(
            currentMonthName = "Current Month",
            cyclesThisMonth = 18,
            monthlyAverage = 23.4,
            totalWaterLitersThisMonth = 169.2,
            avgWaterPerCycle = 9.4,
            avgKwhPerCycle = 0.82,
            totalKwhThisMonth = 14.76,
            estimatedSavingsEuro = 11.70
        )
    )

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun toggleThemeMode() {
        _themeMode.value = when (_themeMode.value) {
            AppThemeMode.LIGHT -> AppThemeMode.DIM
            AppThemeMode.DIM -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.LIGHT
        }
    }

    fun togglePower() {
        repository.togglePower()
    }

    fun setPowerState(powerState: DishwasherPowerState) {
        repository.setPowerState(powerState)
    }

    fun toggleDoor() {
        repository.toggleDoorState()
    }

    fun toggleDoorState() {
        repository.toggleDoorState()
    }

    fun setDoorOpen(isOpen: Boolean) {
        repository.setDoorState(isOpen)
    }

    fun setDoorState(open: Boolean) {
        repository.setDoorState(open)
    }

    fun startCycle(program: DishwasherProgram, options: DishwasherOptions = DishwasherOptions()) {
        repository.startCycle(program, options)
    }

    fun pauseCycle() {
        repository.pauseCycle()
    }

    fun resumeCycle() {
        repository.resumeCycle()
    }

    fun stopCycle() {
        repository.stopCycle()
    }

    fun scheduleCycle(program: DishwasherProgram, delayMinutes: Int, options: DishwasherOptions = DishwasherOptions()) {
        repository.scheduleCycle(program, delayMinutes, options)
    }

    fun toggleConnection() {
        repository.toggleConnectionStatus()
    }

    fun triggerTestAlert(type: String) {
        when (type) {
            "door_open" -> repository.setDoorState(true)
            "rinse_aid" -> repository.triggerTestAlert(
                "Rinse Aid Low",
                "Dishwasher rinse aid reservoir is almost empty. Top up for streak-free drying.",
                com.example.data.model.AlertSeverity.WARNING
            )
            "salt" -> repository.triggerTestAlert(
                "Special Salt Depleted",
                "Water softener salt level is low. Refill salt compartment to prevent limescale.",
                com.example.data.model.AlertSeverity.WARNING
            )
            "water_tap" -> repository.triggerTestAlert(
                "Check Water Tap (E:15)",
                "AquaStop sensor detected unusual flow rate. Ensure water supply tap is fully open.",
                com.example.data.model.AlertSeverity.CRITICAL
            )
            else -> repository.triggerTestAlert(
                "Filter Cleaning Reminder",
                "Recommended maintenance: Rinse the triple filter unit under running water.",
                com.example.data.model.AlertSeverity.INFO
            )
        }
    }

    fun triggerTestAlert(alert: ApplianceAlert) {
        repository.triggerTestAlert(alert.title, alert.message, alert.severity)
    }

    fun dismissAlert(id: String) {
        repository.dismissAlert(id)
    }

    fun updateCredentials(clientId: String, clientSecret: String, token: String, envUrl: String) {
        repository.updateCredentials(clientId, clientSecret, token, envUrl)
    }

    fun verifyCredentials(clientId: String, clientSecret: String, token: String, envUrl: String) {
        viewModelScope.launch {
            repository.verifyCredentials(clientId, clientSecret, token, envUrl)
        }
    }

    fun fetchLastYearCycleHistory() {
        viewModelScope.launch {
            repository.fetchLastYearCycleHistory()
        }
    }

    fun syncRealtimeStatus() {
        viewModelScope.launch {
            repository.syncRealtimeStatus()
        }
    }

    fun shareExportData(context: Context, format: String) {
        val cycles = cycleHistory.value
        val isCsv = format.equals("csv", ignoreCase = true)
        val content = if (isCsv) {
            repository.exportCyclesAsCsv(cycles)
        } else {
            repository.exportCyclesAsJson(cycles)
        }
        val mimeType = if (isCsv) "text/csv" else "application/json"
        val filename = if (isCsv) "dishy_bosch_cycles.csv" else "dishy_bosch_cycles.json"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_TITLE, filename)
            type = mimeType
        }
        val shareIntent = Intent.createChooser(sendIntent, "Export Dishy Cycles ($format)")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    private fun calculateAnalytics(list: List<CycleEntity>): MonthlyAnalytics {
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)
        val monthNames = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        val currentMonthName = monthNames.getOrElse(currentMonth) { "Current Month" }

        val thisMonthCycles = list.filter {
            val c = Calendar.getInstance()
            c.timeInMillis = it.timestamp
            c.get(Calendar.MONTH) == currentMonth && c.get(Calendar.YEAR) == currentYear
        }

        val cyclesCount = if (thisMonthCycles.isNotEmpty()) thisMonthCycles.size else 24
        val totalWater = if (thisMonthCycles.isNotEmpty()) {
            thisMonthCycles.sumOf { it.waterLiters }
        } else {
            cyclesCount * 9.4
        }
        val totalKwh = if (thisMonthCycles.isNotEmpty()) {
            thisMonthCycles.sumOf { it.energyKwh }
        } else {
            cyclesCount * 0.82
        }

        val avgWater = if (cyclesCount > 0) totalWater / cyclesCount else 9.4
        val avgKwh = if (cyclesCount > 0) totalKwh / cyclesCount else 0.82

        // Monthly overall average (average count across all distinct recorded months)
        val monthBuckets = list.groupBy {
            val c = Calendar.getInstance()
            c.timeInMillis = it.timestamp
            "${c.get(Calendar.YEAR)}-${c.get(Calendar.MONTH)}"
        }
        val monthlyAvgCount = if (monthBuckets.isNotEmpty()) {
            list.size.toDouble() / monthBuckets.size
        } else {
            22.4
        }

        return MonthlyAnalytics(
            currentMonthName = currentMonthName,
            cyclesThisMonth = cyclesCount,
            monthlyAverage = monthlyAvgCount,
            totalWaterLitersThisMonth = totalWater,
            avgWaterPerCycle = avgWater,
            avgKwhPerCycle = avgKwh,
            totalKwhThisMonth = totalKwh,
            estimatedSavingsEuro = (cyclesCount * 0.65)
        )
    }

    private fun calculateMonthlyCycleBuckets(list: List<CycleEntity>): List<MonthlyCycleBucket> {
        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)
        val currentMonth = cal.get(Calendar.MONTH) // 0-based: 0 = Jan, 8 = Sep
        val monthShortNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val monthFullNames = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        val cyclesByMonth = list.groupBy { cycle ->
            val c = Calendar.getInstance().apply { timeInMillis = cycle.timestamp }
            Pair(c.get(Calendar.YEAR), c.get(Calendar.MONTH))
        }

        val buckets = mutableListOf<MonthlyCycleBucket>()
        // Generate buckets for the last 12 rolling months (1 full year of cycle history)
        for (offset in 11 downTo 0) {
            val c = Calendar.getInstance().apply {
                add(Calendar.MONTH, -offset)
            }
            val y = c.get(Calendar.YEAR)
            val m = c.get(Calendar.MONTH)
            val isCurrent = (offset == 0)

            val matchingCycles = cyclesByMonth[Pair(y, m)] ?: emptyList()
            val count = matchingCycles.size
            val totalWater = matchingCycles.sumOf { it.waterLiters }
            val totalEnergy = matchingCycles.sumOf { it.energyKwh }

            buckets.add(
                MonthlyCycleBucket(
                    year = y,
                    month = m,
                    monthShortName = monthShortNames.getOrElse(m) { "M${m + 1}" },
                    monthFullName = "${monthFullNames.getOrElse(m) { "Month ${m + 1}" }} $y",
                    count = count,
                    totalWaterLiters = totalWater,
                    totalEnergyKwh = totalEnergy,
                    isCurrentMonth = isCurrent
                )
            )
        }
        return buckets
    }
}
