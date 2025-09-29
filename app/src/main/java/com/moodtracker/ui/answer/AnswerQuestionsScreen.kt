package com.moodtracker.ui.answer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material.icons.filled.Check

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moodtracker.R
import com.moodtracker.data.models.Answer
import com.moodtracker.data.models.QuestionType
import com.moodtracker.data.repository.MoodTrackerRepository
import com.moodtracker.utils.DataUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnswerQuestionsScreen(
    repository: MoodTrackerRepository,
    onNavigateBack: () -> Unit,
    initialQuestionId: String? = null
) {
    val scope = rememberCoroutineScope()
    val questions by repository.getAllActiveQuestions().collectAsState(initial = emptyList())
    
    var currentQuestionIndex by remember(questions, initialQuestionId) {
        mutableIntStateOf(
            if (initialQuestionId != null) {
                questions.indexOfFirst { it.id == initialQuestionId }.takeIf { it >= 0 } ?: 0
            } else {
                0
            }
        )
    }
    var currentAnswer by remember { mutableStateOf("") }
    var additionalNotes by remember { mutableStateOf("") }
    
    if (questions.isEmpty()) {
        // No questions available
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.empty_questions),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onNavigateBack) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    val currentQuestion = questions[currentQuestionIndex]
    val isLastQuestion = currentQuestionIndex == questions.size - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            
            Text(
                text = stringResource(R.string.question_progress, currentQuestionIndex + 1, questions.size),
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.width(48.dp)) // Balance the back button
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Progress indicator
        LinearProgressIndicator(
            progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Question
        Text(
            text = currentQuestion.text,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Answer input based on question type
        when (currentQuestion.type) {
            QuestionType.MULTIPLE_CHOICE -> {
                MultipleChoiceInput(
                    options = currentQuestion.options ?: emptyList(),
                    selectedOption = currentAnswer,
                    onOptionSelected = { currentAnswer = it }
                )
            }
            QuestionType.YES_NO -> {
                YesNoInput(
                    selectedOption = currentAnswer,
                    onOptionSelected = { currentAnswer = it }
                )
            }
            QuestionType.NUMBER -> {
                NumberInput(
                    value = currentAnswer,
                    onValueChange = { currentAnswer = it }
                )
            }
            QuestionType.TEXT -> {
                TextInput(
                    value = currentAnswer,
                    onValueChange = { currentAnswer = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Additional notes
        OutlinedTextField(
            value = additionalNotes,
            onValueChange = { additionalNotes = it },
            label = { Text(stringResource(R.string.additional_notes)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Spacer(modifier = Modifier.weight(1f))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    // Skip to next question or finish
                    if (isLastQuestion) {
                        onNavigateBack()
                    } else {
                        currentQuestionIndex++
                        currentAnswer = ""
                        additionalNotes = ""
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Skip for now")
            }

            Button(
                onClick = {
                    scope.launch {
                        // Save answer
                        val answer = Answer(
                            id = DataUtils.generateId(),
                            questionId = currentQuestion.id,
                            questionVersion = currentQuestion.version,
                            answerText = currentAnswer,
                            additionalNotes = additionalNotes.takeIf { it.isNotBlank() },
                            timestamp = DataUtils.getCurrentInstant(),
                            wasSnooze = false
                        )
                        repository.insertAnswer(answer)

                        // Move to next question or finish
                        if (isLastQuestion) {
                            onNavigateBack()
                        } else {
                            currentQuestionIndex++
                            currentAnswer = ""
                            additionalNotes = ""
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = currentAnswer.isNotBlank()
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.submit_answer))
            }
        }
    }
}

@Composable
fun MultipleChoiceInput(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedOption == option,
                        onClick = { onOptionSelected(option) }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun YesNoInput(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilterChip(
            onClick = { onOptionSelected("Yes") },
            label = { Text(stringResource(R.string.yes)) },
            selected = selectedOption == "Yes",
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            onClick = { onOptionSelected("No") },
            label = { Text(stringResource(R.string.no)) },
            selected = selectedOption == "No",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun NumberInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                onValueChange(newValue)
            }
        },
        label = { Text(stringResource(R.string.enter_number)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun TextInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.enter_text)) },
        modifier = Modifier.fillMaxWidth(),
        maxLines = 4
    )
}