package com.moodtracker.ui.config

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moodtracker.R
import com.moodtracker.data.models.Question
import com.moodtracker.data.models.QuestionType
import com.moodtracker.data.repository.MoodTrackerRepository
import com.moodtracker.utils.DataUtils
import kotlinx.coroutines.launch

enum class ConfigMode {
    LIST, ADD_QUESTION, EDIT_QUESTION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen(
    repository: MoodTrackerRepository
) {
    var configMode by remember { mutableStateOf(ConfigMode.LIST) }
    var editingQuestion by remember { mutableStateOf<Question?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Question?>(null) }
    
    val questions by repository.getAllQuestions().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    
    when (configMode) {
        ConfigMode.LIST -> {
            QuestionListView(
                questions = questions,
                repository = repository,
                onAddQuestion = { configMode = ConfigMode.ADD_QUESTION },
                onEditQuestion = { question ->
                    editingQuestion = question
                    configMode = ConfigMode.EDIT_QUESTION
                },
                onDeleteQuestion = { question ->
                    showDeleteDialog = question
                }
            )
        }
        ConfigMode.ADD_QUESTION -> {
            QuestionEditView(
                question = null,
                repository = repository,
                onSave = { configMode = ConfigMode.LIST },
                onCancel = { configMode = ConfigMode.LIST }
            )
        }
        ConfigMode.EDIT_QUESTION -> {
            QuestionEditView(
                question = editingQuestion,
                repository = repository,
                onSave = { configMode = ConfigMode.LIST },
                onCancel = { configMode = ConfigMode.LIST }
            )
        }
    }
    
    // Delete confirmation dialog
    showDeleteDialog?.let { question ->
        DeleteQuestionDialog(
            question = question,
            onConfirm = {
                scope.launch {
                    repository.deleteQuestion(question)
                    showDeleteDialog = null
                }
            },
            onDismiss = { showDeleteDialog = null }
        )
    }
}

@Composable
fun QuestionListView(
    questions: List<Question>,
    repository: MoodTrackerRepository,
    onAddQuestion: () -> Unit,
    onEditQuestion: (Question) -> Unit,
    onDeleteQuestion: (Question) -> Unit
) {
    val scope = rememberCoroutineScope()
    
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
                text = stringResource(R.string.config_title),
                style = MaterialTheme.typography.titleLarge
            )
            
            FloatingActionButton(
                onClick = onAddQuestion,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_question))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (questions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.empty_questions),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(questions) { question ->
                    QuestionConfigCard(
                        question = question,
                        onEdit = { onEditQuestion(question) },
                        onDelete = { onDeleteQuestion(question) },
                        onToggleVisibility = { isHidden ->
                            scope.launch {
                                repository.updateQuestionVisibility(question.id, isHidden)
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionEditView(
    question: Question?,
    repository: MoodTrackerRepository,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val isEditing = question != null
    
    var questionText by remember { mutableStateOf(question?.text ?: "") }
    var questionType by remember { mutableStateOf(question?.type ?: QuestionType.TEXT) }
    var optionsText by remember { mutableStateOf(question?.options?.joinToString("\n") ?: "") }
    var showTypeDropdown by remember { mutableStateOf(false) }
    var showDataConsistencyWarning by remember { mutableStateOf(false) }
    
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
            IconButton(onClick = onCancel) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = if (isEditing) stringResource(R.string.edit_question) else stringResource(R.string.add_question),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Question text
        OutlinedTextField(
            value = questionText,
            onValueChange = { questionText = it },
            label = { Text(stringResource(R.string.question_text)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Question type
        ExposedDropdownMenuBox(
            expanded = showTypeDropdown,
            onExpandedChange = { showTypeDropdown = it }
        ) {
            OutlinedTextField(
                value = getQuestionTypeDisplayName(questionType),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.question_type)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeDropdown) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = showTypeDropdown,
                onDismissRequest = { showTypeDropdown = false }
            ) {
                QuestionType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(getQuestionTypeDisplayName(type)) },
                        onClick = {
                            if (isEditing && type != question?.type) {
                                // showDataConsistencyWarning = true
                            }
                            questionType = type
                            showTypeDropdown = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Options for multiple choice
        if (questionType == QuestionType.MULTIPLE_CHOICE) {
            OutlinedTextField(
                value = optionsText,
                onValueChange = { optionsText = it },
                label = { Text(stringResource(R.string.question_options)) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 6,
                placeholder = { Text("Option 1\nOption 2\nOption 3") }
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.cancel))
            }
            
            Button(
                onClick = {
                    scope.launch {
                        val options = if (questionType == QuestionType.MULTIPLE_CHOICE) {
                            optionsText.split("\n").filter { it.isNotBlank() }
                        } else null
                        
                        if (isEditing && question != null) {
                            val updatedQuestion = question.copy(
                                text = questionText,
                                type = questionType,
                                options = options,
                                modifiedAt = DataUtils.getCurrentInstant(),
                                version = question.version + 1
                            )
                            repository.updateQuestion(updatedQuestion)
                        } else {
                            val newQuestion = Question(
                                id = DataUtils.generateId(),
                                text = questionText,
                                type = questionType,
                                options = options,
                                createdAt = DataUtils.getCurrentInstant(),
                                modifiedAt = DataUtils.getCurrentInstant()
                            )
                            repository.insertQuestion(newQuestion)
                        }
                        onSave()
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = questionText.isNotBlank() && 
                         (questionType != QuestionType.MULTIPLE_CHOICE || 
                          optionsText.split("\n").filter { it.isNotBlank() }.size >= 2)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
    
    // Data consistency warning
    if (showDataConsistencyWarning) {
        AlertDialog(
            onDismissRequest = { showDataConsistencyWarning = false },
            title = { Text(stringResource(R.string.data_consistency_warning)) },
            text = { Text(stringResource(R.string.confirm_modify_question)) },
            confirmButton = {
                TextButton(
                    onClick = { showDataConsistencyWarning = false }
                ) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        questionType = question?.type ?: QuestionType.TEXT
                        showDataConsistencyWarning = false
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun QuestionConfigCard(
    question: Question,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleVisibility: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = question.text,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = getQuestionTypeDisplayName(question.type),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    if (question.type == QuestionType.MULTIPLE_CHOICE && !question.options.isNullOrEmpty()) {
                        Text(
                            text = "Options: ${question.options.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Column {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_question))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_question))
                    }
                    TextButton(onClick = { onToggleVisibility(!question.isHidden) }) {
                        Text(if (question.isHidden) "Show" else "Hide")
                    }
                }
            }
            
            if (question.isHidden) {
                Text(
                    text = "Hidden",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun DeleteQuestionDialog(
    question: Question,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_question)) },
        text = { Text(stringResource(R.string.confirm_delete_question)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun getQuestionTypeDisplayName(type: QuestionType): String {
    return when (type) {
        QuestionType.MULTIPLE_CHOICE -> stringResource(R.string.type_multiple_choice)
        QuestionType.YES_NO -> stringResource(R.string.type_yes_no)
        QuestionType.NUMBER -> stringResource(R.string.type_number)
        QuestionType.TEXT -> stringResource(R.string.type_text)
    }
}
