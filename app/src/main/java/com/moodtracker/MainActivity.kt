package com.moodtracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.moodtracker.data.database.MoodTrackerDatabase
import com.moodtracker.data.repository.MoodTrackerRepository
import com.moodtracker.services.NotificationService
import com.moodtracker.services.NotificationScheduler
import com.moodtracker.ui.answer.AnswerQuestionsScreen
import com.moodtracker.ui.config.ConfigScreen
import com.moodtracker.ui.logs.LogsScreen
import com.moodtracker.ui.main.MainScreen
import com.moodtracker.ui.notifications.NotificationScheduleScreen
import com.moodtracker.ui.theme.MoodTrackerTheme

class MainActivity : ComponentActivity() {
    private lateinit var repository: MoodTrackerRepository
    private var navigateToAnswerQuestions by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = MoodTrackerDatabase.getDatabase(this)
        repository = MoodTrackerRepository(
            database.questionDao(),
            database.answerDao(),
            database.notificationDao()
        )

        // Initialize notification channel
        NotificationService.createNotificationChannel(this)
        
        // Schedule notifications
        NotificationScheduler.scheduleAllNotifications(this)
        
        // Start WorkManager for periodic notification scheduling
        com.moodtracker.services.NotificationWorker.schedulePeriodicWork(this)

        // Check if app was opened from a notification
        val intentAction = intent?.getStringExtra("action")
        navigateToAnswerQuestions = intentAction == "answer_questions"
        
        setContent {
            MoodTrackerTheme {
                MoodTrackerApp(
                    repository = repository,
                    shouldNavigateToAnswerQuestions = navigateToAnswerQuestions
                )
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle notification when app is already running
        val intentAction = intent.getStringExtra("action")
        if (intentAction == "answer_questions") {
            navigateToAnswerQuestions = true
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Main : Screen("main", "Main", Icons.Filled.Home)
    object Logs : Screen("logs", "Logs", Icons.Filled.List)
    object Config : Screen("config", "Config", Icons.Filled.Settings)
    object AnswerQuestions : Screen("answer_questions", "Answer Questions", Icons.Filled.Check)
    object NotificationSchedule : Screen("notification_schedule", "Notifications", Icons.Filled.Notifications)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodTrackerApp(
    repository: MoodTrackerRepository,
    shouldNavigateToAnswerQuestions: Boolean = false
) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Main, Screen.Logs, Screen.NotificationSchedule, Screen.Config)
    
    // Initialize default data once when app starts
    LaunchedEffect(Unit) {
        repository.initializeDefaultDataIfNeeded()
    }
    
    // Navigate to Answer Questions if opened from notification
    var hasNavigated by remember { mutableStateOf(false) }
    LaunchedEffect(shouldNavigateToAnswerQuestions) {
        if (shouldNavigateToAnswerQuestions && !hasNavigated) {
            navController.navigate(Screen.AnswerQuestions.route) {
                // Clear the back stack to main screen first
                popUpTo(Screen.Main.route) {
                    inclusive = false
                }
            }
            hasNavigated = true
        }
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Main.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Main.route) {
                MainScreen(
                    repository = repository,
                    onNavigateToAnswerQuestions = {
                        navController.navigate(Screen.AnswerQuestions.route)
                    },
                    onNavigateToAnswerQuestion = { questionId ->
                        navController.navigate("${Screen.AnswerQuestions.route}/$questionId")
                    }
                )
            }
            composable(Screen.Logs.route) {
                LogsScreen(repository = repository)
            }
            composable(Screen.Config.route) {
                ConfigScreen(repository = repository)
            }
            composable(Screen.AnswerQuestions.route) {
                AnswerQuestionsScreen(
                    repository = repository,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable("${Screen.AnswerQuestions.route}/{questionId}") { backStackEntry ->
                val questionId = backStackEntry.arguments?.getString("questionId") ?: ""
                AnswerQuestionsScreen(
                    repository = repository,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    initialQuestionId = questionId
                )
            }
            composable(Screen.NotificationSchedule.route) {
                NotificationScheduleScreen(
                    repository = repository
                )
            }
        }
    }
}
