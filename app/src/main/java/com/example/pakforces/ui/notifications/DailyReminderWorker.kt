package com.example.pakforces.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.pakforces.MainActivity
import com.example.pakforces.data.repo.UserPreferences
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val prefs = UserPreferences(applicationContext)
        val enabled = try { prefs.dailyReminder.first() } catch (_: Exception) { true }
        if (!enabled) return Result.success()

        // Duolingo-style motivational messages (Roman Urdu)
        val messages = listOf(
            "Aaj ka challenge pending hai! 10 questions sirf 5 minute mein. Streak banaye rakhein! 🔥",
            "Daily practice = success. PAF/Army/Navy test clear karne ke liye roz practice karein. 💪",
            "Aapka streak khatre mein hai! Aa jayein aur challenge complete karein. ⚡",
            "Her expert ek din beginner tha. Aaj shuru karein — 10 sawaal solve karein. 🎯",
            "Dimagh Gym hai! Aaj 10 sawaal solve kar ke exercise dein. 🧠",
            "Aap aaj abhi tak practice nahi ki! Streak barqarar rakhein — 5 min lagte hain. ⏰",
            "Kamyabi ka raasta: roz thoda thoda. Aaj ka 5-minute challenge complete karein. 🚀"
        )
        val msg = messages.random()
        showNotification(applicationContext, "Pak Forces Prep", msg)
        return Result.success()
    }

    private fun showNotification(context: Context, title: String, body: String) {
        val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "pak_forces_daily"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Daily Reminder", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Rozana practice ki yaad-dihani"
                enableVibration(true)
                enableLights(true)
            }
            mgr.createNotificationChannel(channel)
        }
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val notif = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
        mgr.notify(1001, notif)
    }

    companion object {
        private const val WORK_NAME = "pak_forces_daily_reminder"

        /** Schedule a daily reminder at the given hour (24h). Uses 6 PM by default. */
        fun schedule(context: Context, hour: Int = 18) {
            val now = Calendar.getInstance()
            val target = (now.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(now)) add(Calendar.DAY_OF_MONTH, 1)
            }
            val delayMin = ((target.timeInMillis - now.timeInMillis) / 60000L).coerceAtLeast(15)

            val req = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(delayMin, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, req
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
