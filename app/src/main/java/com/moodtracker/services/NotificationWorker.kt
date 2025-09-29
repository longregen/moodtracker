package com.moodtracker.services

import android.content.Context
import androidx.work.*
import com.moodtracker.data.database.MoodTrackerDatabase
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = MoodTrackerDatabase.getDatabase(applicationContext)
            val notificationDao = database.notificationDao()
            
            // Get enabled schedules
            val enabledSchedules = notificationDao.getEnabledSchedules().first()
            
            // Schedule notifications for each enabled schedule
            enabledSchedules.forEach { schedule ->
                NotificationScheduler.scheduleNotification(applicationContext, schedule)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        private const val WORK_NAME = "notification_scheduler_work"
        
        fun schedulePeriodicWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(false)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(false)
                .build()

            val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                1, TimeUnit.DAYS
            )
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
        }
        
        fun cancelWork(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
