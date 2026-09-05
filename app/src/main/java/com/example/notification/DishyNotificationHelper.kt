package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity

class DishyNotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID_CYCLES = "dishy_cycle_channel"
        const val CHANNEL_ID_ALERTS = "dishy_alert_channel"
        private const val NOTIFICATION_ID_CYCLE = 1001
        private const val NOTIFICATION_ID_ALERT = 1002
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val cycleChannel = NotificationChannel(
                CHANNEL_ID_CYCLES,
                "Dishwasher Cycle Status",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when dish cycle completes or changes phase"
                enableVibration(true)
            }

            val alertChannel = NotificationChannel(
                CHANNEL_ID_ALERTS,
                "Appliance Alerts & Maintenance",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent dishwasher alerts, salt/rinse aid refills, and error codes"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(cycleChannel)
            notificationManager.createNotificationChannel(alertChannel)
        }
    }

    fun showCycleCompletedNotification(programName: String, waterLiters: Double, energyKwh: Double) {
        showCycleCompleteNotification(programName, waterLiters, energyKwh)
    }

    fun showScheduledNotification(programName: String, scheduledTime: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 2, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_CYCLES)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("⏱️ Cycle Scheduled: $programName")
            .setContentText("Delayed start set for $scheduledTime (Off-peak)")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_CYCLE + 1, notification)
        } catch (_: SecurityException) {}
    }

    fun showCycleCompleteNotification(programName: String, waterLiters: Double, energyKwh: Double) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_CYCLES)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("✨ Dish Cycle Finished: $programName")
            .setContentText("Dishes are sparkling clean! Consumed ${waterLiters}L water and ${energyKwh} kWh.")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "Your Bosch dishwasher completed the $programName cycle. Total consumption: ${waterLiters} L water, ${energyKwh} kWh electricity. Open Dishy to view resource insights."
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_CYCLE, notification)
        } catch (_: SecurityException) {
            // Permission not granted by user
        }
    }

    fun showAlertNotification(title: String, message: String, isCritical: Boolean = false) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setSmallIcon(if (isCritical) android.R.drawable.ic_dialog_alert else android.R.drawable.ic_dialog_info)
            .setContentTitle("⚠️ Dishy Alert: $title")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(if (isCritical) NotificationCompat.PRIORITY_MAX else NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_ALERT, notification)
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }
}
