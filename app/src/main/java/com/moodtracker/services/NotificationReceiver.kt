package com.moodtracker.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "com.moodtracker.SHOW_QUESTION_NOTIFICATION" -> {
                NotificationService.showQuestionNotification(context)
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                // Reschedule notifications after device reboot
                NotificationScheduler.scheduleAllNotifications(context)
            }
        }
    }
}
