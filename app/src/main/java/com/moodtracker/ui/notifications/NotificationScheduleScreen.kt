package com.moodtracker.ui.notifications

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moodtracker.R
import com.moodtracker.data.models.NotificationSchedule
import com.moodtracker.data.repository.MoodTrackerRepository
import com.moodtracker.services.NotificationScheduler
import com.moodtracker.utils.DataUtils
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScheduleScreen(
    repository: MoodTrackerRepository,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val schedules by repository.getAllSchedules().collectAsState(initial = emptyList())
    
    var showAddDialog by remember { mutableStateOf(false) }
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
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notification Schedule",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Schedule")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Next notification info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                if (nextNotificationTime != null) {
                    Text(
                        text = "Next Notification",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = nextNotificationTime!!,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    timeUntilNext?.let { time ->
                        Text(
                            text = "in $time",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text(
                        text = "No notifications scheduled",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Add a schedule to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        // Schedule list
        Text(
            text = "Daily Schedule",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (schedules.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No schedules configured",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Add notification times to get daily reminders",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(schedules.sortedBy { it.timeOfDay }) { schedule ->
                    ScheduleItem(
                        schedule = schedule,
                        onToggleEnabled = { enabled ->
                            scope.launch {
                                repository.updateScheduleEnabled(schedule.id, enabled)
                                // Reschedule notifications
                                NotificationScheduler.scheduleAllNotifications(context)
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
                            scope.launch {
                                repository.updateSchedule(schedule.copy(timeOfDay = newTime))
                                // Reschedule notifications
                                NotificationScheduler.scheduleAllNotifications(context)
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
                scope.launch {
                    val newSchedule = NotificationSchedule(
                        id = DataUtils.generateId(),
                        timeOfDay = timeOfDay,
                        isEnabled = true
                    )
                    repository.insertSchedule(newSchedule)
                    // Reschedule notifications
                    NotificationScheduler.scheduleAllNotifications(context)
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
fun ScheduleItem(
    schedule: NotificationSchedule,
    onToggleEnabled: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: (String) -> Unit
) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = DataUtils.formatTimeOfDay(schedule.timeOfDay),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
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
            
            IconButton(onClick = { showEditDialog = true }) {
                Icon(Icons.Default.Notifications, contentDescription = "Edit time")
            }
            
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete schedule")
            }
        }
    }
    
    if (showEditDialog) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val timeString = String.format("%02d:%02d", hourOfDay, minute)
                onEdit(timeString)
                showEditDialog = false
            },
            schedule.timeOfDay.split(":")[0].toInt(),
            schedule.timeOfDay.split(":")[1].toInt(),
            true
        ).show()
    }
}

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
                Spacer(modifier = Modifier.height(16.dp))
                
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
                    Spacer(modifier = Modifier.width(8.dp))
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
