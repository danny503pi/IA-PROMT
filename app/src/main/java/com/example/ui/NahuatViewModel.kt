package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.QuizResult
import com.example.data.model.Word
import com.example.data.remote.NahuatExampleSuggestion
import com.example.data.repository.AiResult
import com.example.data.repository.NahuatRepository
import com.example.data.repository.QuizQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    DICTIONARY("Diccionario"),
    WORD_OF_DAY("Palabra del Día"),
    QUIZ("Quiz"),
    AI_VALIDATOR("Validación IA"),
    ABOUT("Cultura y Fuentes")
}

sealed class QuizState {
    object Idle : QuizState()
    data class InProgress(
        val questions: List<QuizQuestion>,
        val currentIndex: Int,
        val selectedOption: Int? = null,
        val isAnswerSubmitted: Boolean = false,
        val currentScore: Int = 0
    ) : QuizState()
    data class Finished(
        val finalScore: Int,
        val totalQuestions: Int,
        val reviewQuestions: List<QuizQuestion>
    ) : QuizState()
}

sealed class AiValidatorState {
    object Idle : AiValidatorState()
    object Loading : AiValidatorState()
    data class ReadyToVerify(
        val suggestion: NahuatExampleSuggestion,
        val isFromLiveAi: Boolean,
        val infoNotice: String? = null
    ) : AiValidatorState()
    data class ValidatedSaved(val savedWord: Word) : AiValidatorState()
}

class NahuatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NahuatRepository
    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = NahuatRepository(db.wordDao())
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
        }
    }

    // Tab Navigation
    private val _currentTab = MutableStateFlow(AppTab.DICTIONARY)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    // Search and Category Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todos")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories = listOf(
        "Todos",
        "Naturaleza",
        "Animales",
        "Familia",
        "Cotidiano",
        "Números",
        "Saludos y Cortesía",
        "Acciones",
        "Favoritos",
        "Validados con IA"
    )

    // Filtered words combined state
    val words: StateFlow<List<Word>> = combine(
        repository.allWords,
        _searchQuery,
        _selectedCategory
    ) { all, query, category ->
        var list = all
        if (category == "Favoritos") {
            list = list.filter { it.isFavorite }
        } else if (category == "Validados con IA") {
            list = list.filter { it.validatedSourceNote.isNotBlank() }
        } else if (category != "Todos") {
            list = list.filter { it.category.equals(category, ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.nahuat.lowercase().contains(q) ||
                it.spanish.lowercase().contains(q) ||
                it.phonetics.lowercase().contains(q)
            }
        }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Total words count in DB
    val allWordsList: StateFlow<List<Word>> = repository.allWords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Word of the Day
    val wordOfTheDay: StateFlow<Word?> = allWordsList.combine(_currentTab) { wordsList, _ ->
        repository.getWordOfTheDay(wordsList)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Quiz History
    val quizResults: StateFlow<List<QuizResult>> = repository.allQuizResults.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current Word for Detail Sheet
    private val _selectedWordForDetail = MutableStateFlow<Word?>(null)
    val selectedWordForDetail: StateFlow<Word?> = _selectedWordForDetail.asStateFlow()

    fun selectWordForDetail(word: Word?) {
        _selectedWordForDetail.value = word
    }

    // Word Add Modal
    private val _showAddWordDialog = MutableStateFlow(false)
    val showAddWordDialog: StateFlow<Boolean> = _showAddWordDialog.asStateFlow()

    fun setAddWordDialogVisible(visible: Boolean) {
        _showAddWordDialog.value = visible
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelect(category: String) {
        _selectedCategory.value = category
    }

    fun toggleFavorite(word: Word) {
        viewModelScope.launch {
            repository.toggleFavorite(word.id, !word.isFavorite)
        }
    }

    fun addNewWord(
        nahuat: String,
        phonetics: String,
        spanish: String,
        category: String,
        source: String,
        culturalNote: String
    ): Boolean {
        if (nahuat.isBlank() || spanish.isBlank() || source.isBlank()) {
            return false
        }

        val cleanedPhonetics = if (phonetics.isBlank()) "[${nahuat.lowercase()}]" else phonetics
        val word = Word(
            nahuat = nahuat.trim(),
            phonetics = cleanedPhonetics.trim(),
            spanish = spanish.trim(),
            category = category.ifBlank { "Cotidiano" },
            source = source.trim(),
            culturalNote = culturalNote.trim(),
            isCustom = true,
            isValidated = true
        )

        viewModelScope.launch {
            repository.insertWord(word)
        }
        return true
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch {
            repository.deleteWord(word)
        }
    }

    // ==========================================
    // QUIZ LOGIC (5 preguntas al azar)
    // ==========================================
    private val _quizState = MutableStateFlow<QuizState>(QuizState.Idle)
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    fun startQuiz() {
        viewModelScope.launch {
            val questions = repository.generateQuizQuestions(count = 5)
            if (questions.isNotEmpty()) {
                _quizState.value = QuizState.InProgress(
                    questions = questions,
                    currentIndex = 0,
                    selectedOption = null,
                    isAnswerSubmitted = false,
                    currentScore = 0
                )
            }
        }
    }

    fun selectQuizOption(optionIndex: Int) {
        val state = _quizState.value
        if (state is QuizState.InProgress && !state.isAnswerSubmitted) {
            _quizState.value = state.copy(selectedOption = optionIndex)
        }
    }

    fun submitQuizAnswer() {
        val state = _quizState.value
        if (state is QuizState.InProgress && state.selectedOption != null && !state.isAnswerSubmitted) {
            val currentQ = state.questions[state.currentIndex]
            val isCorrect = state.selectedOption == currentQ.correctIndex
            val newScore = if (isCorrect) state.currentScore + 1 else state.currentScore
            _quizState.value = state.copy(
                isAnswerSubmitted = true,
                currentScore = newScore
            )
        }
    }

    fun nextQuizQuestion() {
        val state = _quizState.value
        if (state is QuizState.InProgress && state.isAnswerSubmitted) {
            val nextIndex = state.currentIndex + 1
            if (nextIndex < state.questions.size) {
                _quizState.value = state.copy(
                    currentIndex = nextIndex,
                    selectedOption = null,
                    isAnswerSubmitted = false
                )
            } else {
                // Quiz completed! Save result to Room
                val finalScore = state.currentScore
                viewModelScope.launch {
                    repository.saveQuizResult(finalScore, state.questions.size)
                }
                _quizState.value = QuizState.Finished(
                    finalScore = finalScore,
                    totalQuestions = state.questions.size,
                    reviewQuestions = state.questions
                )
            }
        }
    }

    fun resetQuiz() {
        _quizState.value = QuizState.Idle
    }

    fun clearQuizHistory() {
        viewModelScope.launch {
            repository.clearQuizHistory()
        }
    }

    // ==========================================
    // AI VALIDATOR (Sello de IA)
    // ==========================================
    private val _aiValidatorState = MutableStateFlow<AiValidatorState>(AiValidatorState.Idle)
    val aiValidatorState: StateFlow<AiValidatorState> = _aiValidatorState.asStateFlow()

    private val _selectedWordForAi = MutableStateFlow<Word?>(null)
    val selectedWordForAi: StateFlow<Word?> = _selectedWordForAi.asStateFlow()

    fun selectWordForAiValidation(word: Word) {
        _selectedWordForAi.value = word
        _aiValidatorState.value = AiValidatorState.Idle
        _currentTab.value = AppTab.AI_VALIDATOR
    }

    fun requestAiExampleForWord(word: Word) {
        _selectedWordForAi.value = word
        _aiValidatorState.value = AiValidatorState.Loading

        viewModelScope.launch {
            when (val result = repository.requestAiExample(word)) {
                is AiResult.Success -> {
                    _aiValidatorState.value = AiValidatorState.ReadyToVerify(
                        suggestion = result.suggestion,
                        isFromLiveAi = result.isFromLiveAi,
                        infoNotice = null
                    )
                }
                is AiResult.Error -> {
                    val fallback = result.fallbackSuggestion
                    if (fallback != null) {
                        _aiValidatorState.value = AiValidatorState.ReadyToVerify(
                            suggestion = fallback,
                            isFromLiveAi = false,
                            infoNotice = result.message
                        )
                    } else {
                        _aiValidatorState.value = AiValidatorState.Idle
                    }
                }
            }
        }
    }

    /**
     * El estudiante valida la propuesta de la IA contra material documentado
     * (editando lo que considere necesario) y la publica en su vocabulario.
     */
    fun validateAndPublishAiExample(
        originalWord: Word,
        validatedSentence: String,
        validatedTranslation: String,
        documentedSource: String
    ): Boolean {
        if (validatedSentence.isBlank() || validatedTranslation.isBlank() || documentedSource.isBlank()) {
            return false
        }

        viewModelScope.launch {
            val updatedWord = originalWord.copy(
                exampleSentenceNahuat = validatedSentence.trim(),
                exampleSentenceSpanish = validatedTranslation.trim(),
                isValidated = true,
                validatedSourceNote = "Validado por estudiante contra: ${documentedSource.trim()}"
            )
            repository.updateWord(updatedWord)
            _aiValidatorState.value = AiValidatorState.ValidatedSaved(updatedWord)
        }
        return true
    }

    fun resetAiValidator() {
        _aiValidatorState.value = AiValidatorState.Idle
    }
}
