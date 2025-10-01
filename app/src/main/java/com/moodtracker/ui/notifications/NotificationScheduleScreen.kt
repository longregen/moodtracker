package com.moodtracker.ui.notifications

import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.moodtracker.data.models.NotificationSchedule
import com.moodtracker.data.repository.MoodTrackerRepository
import com.moodtracker.services.NotificationScheduler
import com.moodtracker.ui.components.CardSubtitle
import com.moodtracker.ui.components.CardTitle
import com.moodtracker.ui.components.CommonCard
import com.moodtracker.ui.components.InfoCard
import com.moodtracker.ui.components.SectionTitle
import com.moodtracker.ui.components.StandardScreenLayout
import com.moodtracker.ui.theme.Spacing
import com.moodtracker.utils.DataUtils
import com.moodtracker.utils.NotificationPermissionHelper
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScheduleScreen(
    repository: MoodTrackerRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val schedules by repository.getAllSchedules().collectAsState(initial = emptyList())
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var nextNotificationTime by remember { mutableStateOf<String?>(null) }
    var timeUntilNext by remember { mutableStateOf<String?>(null) }
    
    // Calculate next notification time
    LaunchedEffect(schedules) {
        val enabledSchedules = schedules.filter { it.isEnabled }
        if (enabledSchedules.isNotEmpty()) {
            val now = Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
            val currentTime = "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"
            
            // Find next notification time
            val sortedTimes = enabledSchedules.map { it.timeOfDay }.sorted()
            val nextTime = sortedTimes.find { it > currentTime } ?: sortedTimes.firstOrNull()
            
            nextTime?.let { time ->
                nextNotificationTime = DataUtils.formatTimeOfDay(time)
                
                // Calculate time until next notification
                val timeParts = time.split(":")
                val nextHour = timeParts[0].toInt()
                val nextMinute = timeParts[1].toInt()
                
                var nextDateTime = now.date.atTime(nextHour, nextMinute)
                if (time <= currentTime) {
                    // Next notification is tomorrow
                    nextDateTime = nextDateTime.date.plus(1, DateTimeUnit.DAY).atTime(nextHour, nextMinute)
                }
                
                val duration = nextDateTime.toInstant(kotlinx.datetime.TimeZone.currentSystemDefault()) - 
                              now.toInstant(kotlinx.datetime.TimeZone.currentSystemDefault())
                val hours = duration.inWholeHours
                val minutes = (duration.inWholeMinutes % 60)
                
                timeUntilNext = when {
                    hours > 0 -> "${hours}h ${minutes}m"
                    minutes > 0 -> "${minutes}m"
                    else -> "Less than 1 minute"
                }
            }
        } else {
            nextNotificationTime = null
            timeUntilNext = null
        }
    }
    
    StandardScreenLayout(
        title = "Notification Schedule",
        headerActions = {
            IconButton(onClick = { 
                if (!NotificationPermissionHelper.hasNotificationPermission(context)) {
                    showPermissionDialog = true
                } else {
                    showAddDialog = true
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Schedule")
            }
        }
    ) {
        if (nextNotificationTime != null) {
            InfoCard(
                title = "Next Notification",
                value = nextNotificationTime!!,
                subtitle = timeUntilNext?.let { "in $it" },
                icon = Icons.Default.Notifications
            )
        } else {
            InfoCard(
                title = "No notifications scheduled",
                value = "Add a schedule to get started",
                icon = Icons.Default.Notifications
            )
        }
        
        Spacer(modifier = Modifier.height(Spacing.medium))
        
        SectionTitle(text = "Daily Schedule")
        
        Spacer(modifier = Modifier.height(Spacing.small))
        
        if (schedules.isEmpty()) {
            CommonCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(Spacing.emptyStateIconSize),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(Spacing.medium))
                    CardTitle(
                        text = "No schedules configured",
                        modifier = Modifier.fillMaxWidth()
                    )
                    CardSubtitle(
                        text = "Add notification times to get daily reminders",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.cardSpacing)
            ) {
                items(schedules.sortedBy { it.timeOfDay }) { schedule ->
                    ScheduleItem(
                        schedule = schedule,
                        onToggleEnabled = { enabled ->
                            if (!NotificationPermissionHelper.hasNotificationPermission(context)) {
                                showPermissionDialog = true
                            } else {
                                scope.launch {
                                    repository.updateScheduleEnabled(schedule.id, enabled)
                                    NotificationScheduler.scheduleAllNotifications(context)
                                }
                            }
                        },
                        onDelete = {
                            scope.launch {
                                repository.deleteSchedule(schedule)
                                // Reschedule notifications
                                NotificationScheduler.scheduleAllNotifications(context)
                            }
                        },
                        onEdit = { newTime ->
                            if (!NotificationPermissionHelper.hasNotificationPermission(context)) {
                                showPermissionDialog = true
                            } else {
                                scope.launch {
                                    repository.updateSchedule(schedule.copy(timeOfDay = newTime))
                                    NotificationScheduler.scheduleAllNotifications(context)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
    
    // Add schedule dialog
    if (showAddDialog) {
        AddScheduleDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { timeOfDay ->
                if (!NotificationPermissionHelper.hasNotificationPermission(context)) {
                    showPermissionDialog = true
                    showAddDialog = false
                } else {
                    scope.launch {
                        val newSchedule = NotificationSchedule(
                            id = DataUtils.generateId(),
                            timeOfDay = timeOfDay,
                            isEnabled = true
                        )
                        repository.insertSchedule(newSchedule)
                        NotificationScheduler.scheduleAllNotifications(context)
                        showAddDialog = false
                    }
                }
            }
        )
    }
    
    // Permission dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Notification Permission Required") },
            text = { 
                Text(
                    "To schedule notifications, please grant notification permission in your device settings.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        NotificationPermissionHelper.requestNotificationPermission(context as Activity)
                        showPermissionDialog = false
                    }
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun ScheduleItem(
    schedule: NotificationSchedule,
    onToggleEnabled: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: (String) -> Unit
) {
    val context = LocalContext.current
    
    CommonCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        val timeParts = schedule.timeOfDay.split(":")
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                val timeString = String.format("%02d:%02d", hourOfDay, minute)
                                onEdit(timeString)
                            },
                            timeParts[0].toInt(),
                            timeParts[1].toInt(),
                            true
                        ).show()
                    }
            ) {
                Text(
                    text = DataUtils.formatTimeOfDay(schedule.timeOfDay),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (schedule.isEnabled) "Active" else "Disabled",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (schedule.isEnabled) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = schedule.isEnabled,
                onCheckedChange = onToggleEnabled
            )
            
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete schedule")
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun AddScheduleDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedTime by remember { mutableStateOf("09:00") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Notification Time") },
        text = {
            Column {
                Text("Select a time for daily notifications:")
                Spacer(modifier = Modifier.height(Spacing.medium))
                
                OutlinedButton(
                    onClick = {
                        val timeParts = selectedTime.split(":")
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                            },
                            timeParts[0].toInt(),
                            timeParts[1].toInt(),
                            true
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null)
                    Spacer(modifier = Modifier.width(Spacing.small))
                    Text(DataUtils.formatTimeOfDay(selectedTime))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedTime) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
