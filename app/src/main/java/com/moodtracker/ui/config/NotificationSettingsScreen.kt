package com.moodtracker.ui.config

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moodtracker.R
import com.moodtracker.data.models.NotificationSchedule
import com.moodtracker.data.repository.MoodTrackerRepository
import com.moodtracker.services.NotificationScheduler
import com.moodtracker.utils.DataUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    repository: MoodTrackerRepository
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val schedules by repository.getAllSchedules().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Notification Schedule",
                style = MaterialTheme.typography.titleLarge
            )
            
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Schedule")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (schedules.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No notification schedules configured",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(schedules) { schedule ->
                    NotificationScheduleCard(
                        schedule = schedule,
                        onToggleEnabled = { isEnabled ->
                            scope.launch {
                                repository.updateScheduleEnabled(schedule.id, isEnabled)
                                if (isEnabled) {
                                    NotificationScheduler.scheduleNotification(context, schedule)
                                } else {
                                    NotificationScheduler.cancelNotification(context, schedule.id)
                                }
                            }
                        },
                        onDelete = {
                            scope.launch {
                                NotificationScheduler.cancelNotification(context, schedule.id)
                                repository.deleteSchedule(schedule)
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
            onAdd = { timeOfDay ->
                scope.launch {
                    val newSchedule = NotificationSchedule(
                        id = DataUtils.generateId(),
                        timeOfDay = timeOfDay,
                        isEnabled = true
                    )
                    repository.insertSchedule(newSchedule)
                    NotificationScheduler.scheduleNotification(context, newSchedule)
                    showAddDialog = false
                }
            }
        )
    }
}

@Composable
fun NotificationScheduleCard(
    schedule: NotificationSchedule,
    onToggleEnabled: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = DataUtils.formatTimeOfDay(schedule.timeOfDay),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (schedule.isEnabled) "Enabled" else "Disabled",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (schedule.isEnabled) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row {
                Switch(
                    checked = schedule.isEnabled,
                    onCheckedChange = onToggleEnabled
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun AddScheduleDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var hour by remember { mutableIntStateOf(9) }
    var minute by remember { mutableIntStateOf(0) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Notification Time") },
        text = {
            Column {
                Text("Select time for daily notifications:")
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour picker
                    OutlinedTextField(
                        value = hour.toString().padStart(2, '0'),
                        onValueChange = { newValue ->
                            newValue.toIntOrNull()?.let { h ->
                                if (h in 0..23) hour = h
                            }
                        },
                        label = { Text("Hour") },
                        modifier = Modifier.width(80.dp)
                    )
                    
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    // Minute picker
                    OutlinedTextField(
                        value = minute.toString().padStart(2, '0'),
                        onValueChange = { newValue ->
                            newValue.toIntOrNull()?.let { m ->
                                if (m in 0..59) minute = m
                            }
                        },
                        label = { Text("Minute") },
                        modifier = Modifier.width(80.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val timeOfDay = String.format("%02d:%02d", hour, minute)
                    onAdd(timeOfDay)
                }
            ) {
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
