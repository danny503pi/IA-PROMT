package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppTab
import com.example.ui.NahuatViewModel
import com.example.ui.QuizState
import com.example.ui.components.AddWordDialog
import com.example.ui.components.AppTopBar
import com.example.ui.components.WordDetailDialog
import com.example.ui.screens.AboutCultureScreen
import com.example.ui.screens.AiValidatorScreen
import com.example.ui.screens.DictionaryScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.WordOfDayScreen
import com.example.ui.theme.GoldTertiary
import com.example.ui.theme.JadeSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TerracottaPrimary
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: NahuatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NahuatApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NahuatApp(viewModel: NahuatViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val words by viewModel.words.collectAsStateWithLifecycle()
    val allWordsList by viewModel.allWordsList.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val wordOfTheDay by viewModel.wordOfTheDay.collectAsStateWithLifecycle()
    val quizState by viewModel.quizState.collectAsStateWithLifecycle()
    val quizResults by viewModel.quizResults.collectAsStateWithLifecycle()
    val aiValidatorState by viewModel.aiValidatorState.collectAsStateWithLifecycle()
    val selectedWordForAi by viewModel.selectedWordForAi.collectAsStateWithLifecycle()

    val selectedWordForDetail by viewModel.selectedWordForDetail.collectAsStateWithLifecycle()
    val showAddWordDialog by viewModel.showAddWordDialog.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Handle Back Press to pop back to Dictionary tab if on secondary tab
    BackHandler(enabled = currentTab != AppTab.DICTIONARY || quizState !is QuizState.Idle) {
        if (quizState is QuizState.InProgress) {
            viewModel.resetQuiz()
        } else if (currentTab != AppTab.DICTIONARY) {
            viewModel.selectTab(AppTab.DICTIONARY)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppTopBar(
                totalWordsCount = allWordsList.size,
                onAddWordClick = { viewModel.setAddWordDialogVisible(true) }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("app_bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                val navItems = listOf(
                    NavItem(AppTab.DICTIONARY, "Diccionario", Icons.AutoMirrored.Filled.MenuBook),
                    NavItem(AppTab.WORD_OF_DAY, "Palabra del Día", Icons.Default.WbSunny),
                    NavItem(AppTab.QUIZ, "Quiz", Icons.Default.Quiz),
                    NavItem(AppTab.AI_VALIDATOR, "Taller IA", Icons.Default.AutoAwesome),
                    NavItem(AppTab.ABOUT, "Cultura", Icons.Default.HistoryEdu)
                )

                navItems.forEach { item ->
                    val isSelected = currentTab == item.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(item.tab) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TerracottaPrimary,
                            selectedTextColor = TerracottaPrimary,
                            indicatorColor = TerracottaPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_tab_${item.tab.name}")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        when (currentTab) {
            AppTab.DICTIONARY -> {
                DictionaryScreen(
                    words = words,
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    categories = viewModel.categories,
                    selectedCategory = selectedCategory,
                    onCategorySelect = viewModel::onCategorySelect,
                    onWordClick = viewModel::selectWordForDetail,
                    onToggleFavorite = viewModel::toggleFavorite,
                    modifier = modifier
                )
            }
            AppTab.WORD_OF_DAY -> {
                WordOfDayScreen(
                    word = wordOfTheDay,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onStartQuiz = {
                        viewModel.selectTab(AppTab.QUIZ)
                        viewModel.startQuiz()
                    },
                    onOpenAiValidator = { word ->
                        viewModel.selectWordForAiValidation(word)
                    },
                    modifier = modifier
                )
            }
            AppTab.QUIZ -> {
                QuizScreen(
                    quizState = quizState,
                    quizResults = quizResults,
                    onStartQuiz = viewModel::startQuiz,
                    onSelectOption = viewModel::selectQuizOption,
                    onSubmitAnswer = viewModel::submitQuizAnswer,
                    onNextQuestion = viewModel::nextQuizQuestion,
                    onResetQuiz = viewModel::resetQuiz,
                    onClearHistory = viewModel::clearQuizHistory,
                    modifier = modifier
                )
            }
            AppTab.AI_VALIDATOR -> {
                AiValidatorScreen(
                    words = allWordsList,
                    selectedWord = selectedWordForAi,
                    aiValidatorState = aiValidatorState,
                    onRequestAiExample = viewModel::requestAiExampleForWord,
                    onValidateAndPublish = viewModel::validateAndPublishAiExample,
                    onResetValidator = viewModel::resetAiValidator,
                    onNavigateToDictionary = {
                        viewModel.selectTab(AppTab.DICTIONARY)
                    },
                    modifier = modifier
                )
            }
            AppTab.ABOUT -> {
                AboutCultureScreen(modifier = modifier)
            }
        }

        // Detail Dialog
        selectedWordForDetail?.let { word ->
            WordDetailDialog(
                word = word,
                onDismiss = { viewModel.selectWordForDetail(null) },
                onToggleFavorite = { viewModel.toggleFavorite(word) },
                onOpenAiValidator = {
                    viewModel.selectWordForDetail(null)
                    viewModel.selectWordForAiValidation(word)
                }
            )
        }

        // Add Word Dialog
        if (showAddWordDialog) {
            AddWordDialog(
                onDismiss = { viewModel.setAddWordDialogVisible(false) },
                onConfirm = { nahuat, phonetics, spanish, category, source, culturalNote ->
                    val success = viewModel.addNewWord(
                        nahuat = nahuat,
                        phonetics = phonetics,
                        spanish = spanish,
                        category = category,
                        source = source,
                        culturalNote = culturalNote
                    )
                    if (success) {
                        viewModel.setAddWordDialogVisible(false)
                        scope.launch {
                            snackbarHostState.showSnackbar("«$nahuat» agregada al diccionario exitosamente.")
                        }
                    }
                }
            )
        }
    }
}

private data class NavItem(
    val tab: AppTab,
    val label: String,
    val icon: ImageVector
)
