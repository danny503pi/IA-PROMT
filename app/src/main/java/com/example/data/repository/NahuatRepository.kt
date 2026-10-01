package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.InitialVocabulary
import com.example.data.local.WordDao
import com.example.data.model.QuizResult
import com.example.data.model.Word
import com.example.data.remote.GeminiApiService
import com.example.data.remote.GeminiContent
import com.example.data.remote.GeminiGenerationConfig
import com.example.data.remote.GeminiPart
import com.example.data.remote.GeminiRequest
import com.example.data.remote.NahuatExampleSuggestion
import com.example.data.remote.OfflineNahuatRuleGenerator
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Calendar

data class QuizQuestion(
    val id: Int,
    val targetWord: Word,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

sealed class AiResult {
    data class Success(val suggestion: NahuatExampleSuggestion, val isFromLiveAi: Boolean) : AiResult()
    data class Error(val message: String, val fallbackSuggestion: NahuatExampleSuggestion?) : AiResult()
}

class NahuatRepository(
    private val wordDao: WordDao,
    private val geminiApiService: GeminiApiService = GeminiApiService.create()
) {
    val allWords: Flow<List<Word>> = wordDao.getAllWords()
    val favoriteWords: Flow<List<Word>> = wordDao.getFavoriteWords()
    val allQuizResults: Flow<List<QuizResult>> = wordDao.getAllQuizResults()

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val suggestionAdapter = moshi.adapter(NahuatExampleSuggestion::class.java)

    suspend fun initializeDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        if (wordDao.getWordCount() == 0) {
            wordDao.insertWords(InitialVocabulary.words)
        }
    }

    fun searchWords(query: String): Flow<List<Word>> = wordDao.searchWords(query)

    fun getWordsByCategory(category: String): Flow<List<Word>> =
        if (category == "Todos") wordDao.getAllWords() else wordDao.getWordsByCategory(category)

    suspend fun getWordById(id: Int): Word? = withContext(Dispatchers.IO) {
        wordDao.getWordById(id)
    }

    suspend fun insertWord(word: Word): Long = withContext(Dispatchers.IO) {
        wordDao.insertWord(word)
    }

    suspend fun updateWord(word: Word) = withContext(Dispatchers.IO) {
        wordDao.updateWord(word)
    }

    suspend fun deleteWord(word: Word) = withContext(Dispatchers.IO) {
        wordDao.deleteWord(word)
    }

    suspend fun toggleFavorite(id: Int, isFav: Boolean) = withContext(Dispatchers.IO) {
        wordDao.updateFavorite(id, isFav)
    }

    suspend fun getWordOfTheDay(words: List<Word>): Word? = withContext(Dispatchers.Default) {
        if (words.isEmpty()) return@withContext null
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val index = dayOfYear % words.size
        words[index]
    }

    suspend fun generateQuizQuestions(count: Int = 5): List<QuizQuestion> = withContext(Dispatchers.IO) {
        val pool = wordDao.getRandomWords(20)
        if (pool.size < 4) {
            // If DB is small, fallback to initial words
            val all = InitialVocabulary.words.shuffled()
            return@withContext createQuestionsFromList(all.take(count), all)
        }
        val targets = pool.shuffled().take(count)
        createQuestionsFromList(targets, pool)
    }

    private fun createQuestionsFromList(targets: List<Word>, pool: List<Word>): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()
        for ((idx, target) in targets.withIndex()) {
            val askNahuatToSpanish = (idx % 2 == 0)

            val questionText: String
            val correctAnswer: String
            val distractors: List<String>

            if (askNahuatToSpanish) {
                questionText = "¿Qué significa en español la palabra náhuat «${target.nahuat}»?"
                correctAnswer = target.spanish
                distractors = pool
                    .filter { it.id != target.id && it.spanish != target.spanish }
                    .map { it.spanish }
                    .distinct()
                    .shuffled()
                    .take(3)
            } else {
                questionText = "¿Cómo se dice «${target.spanish}» en náhuat pipil?"
                correctAnswer = target.nahuat
                distractors = pool
                    .filter { it.id != target.id && it.nahuat != target.nahuat }
                    .map { it.nahuat }
                    .distinct()
                    .shuffled()
                    .take(3)
            }

            val options = (distractors + correctAnswer).shuffled()
            val correctIndex = options.indexOf(correctAnswer)

            val explanation = "«${target.nahuat}» se pronuncia ${target.phonetics}. Significa: ${target.spanish}. Fuente documentada: ${target.source}."

            questions.add(
                QuizQuestion(
                    id = idx + 1,
                    targetWord = target,
                    questionText = questionText,
                    options = options,
                    correctIndex = correctIndex,
                    explanation = explanation
                )
            )
        }
        return questions
    }

    suspend fun saveQuizResult(score: Int, total: Int = 5): Long = withContext(Dispatchers.IO) {
        val result = QuizResult(
            score = score,
            totalQuestions = total
        )
        wordDao.insertQuizResult(result)
    }

    suspend fun clearQuizHistory() = withContext(Dispatchers.IO) {
        wordDao.clearQuizHistory()
    }

    /**
     * Sello de IA:
     * Genera una propuesta de ejemplo en Náhuat con formato JSON estructurado.
     * Si no hay API key configurada o falla la red, activa el generador
     * pedagógico documentado según la morfología del Náhuat salvadoreño.
     */
    suspend fun requestAiExample(word: Word): AiResult = withContext(Dispatchers.IO) {
        val fallback = OfflineNahuatRuleGenerator.generateExample(word)
        val apiKey = BuildConfig.GEMINI_API_KEY

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext AiResult.Error(
                message = "Modo de Regla Lingüística Documentada activo (no se detectó llave de Gemini en .env). Se aplicó la gramática pipil de Alan R. King para contrastar.",
                fallbackSuggestion = fallback
            )
        }

        try {
            val systemInstruction = GeminiContent(
                parts = listOf(
                    GeminiPart(
                        text = """
                            Eres un lingüista especialista en la lengua Náhuat (Pipil / Nawat) de El Salvador.
                            Tu misión es proponer a un estudiante de bachillerato un ejemplo auténtico de uso para la palabra solicitada.
                            Debes responder EXCLUSIVAMENTE con un objeto JSON válido con los siguientes campos:
                            {
                              "word": "palabra analizada",
                              "nahuatSentence": "oración corta y clara en náhuat pipil",
                              "phoneticGuide": "guía de pronunciación fonética con acentuación",
                              "spanishTranslation": "traducción fidedigna al español salvadoreño",
                              "grammaticalBreakdown": "desglose morfológico detallado (prefijos de sujeto, objetos, sufijos absolutivos)",
                              "suggestedSourceOrRule": "regla gramatical o fuente documental de El Salvador (ej: Alan R. King, IRIN, Santo Domingo)",
                              "verificationTip": "criterio específico que el estudiante debe comprobar en su diccionario antes de validar"
                            }
                            No agregues texto explicativo fuera del JSON.
                        """.trimIndent()
                    )
                )
            )

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(
                            GeminiPart(
                                text = "Genera un ejemplo de uso para la palabra náhuat: '${word.nahuat}' que significa '${word.spanish}' (categoría: ${word.category})."
                            )
                        )
                    )
                ),
                generationConfig = GeminiGenerationConfig(
                    responseMimeType = "application/json",
                    temperature = 0.2f
                ),
                systemInstruction = systemInstruction
            )

            val response = geminiApiService.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (jsonText.isNullOrBlank()) {
                return@withContext AiResult.Error(
                    message = "La IA devolvió una respuesta vacía. Se cargó el modelo de regla gramatical documentada.",
                    fallbackSuggestion = fallback
                )
            }

            // Clean json if wrapped in ```json ... ```
            val cleanedJson = jsonText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val suggestion = suggestionAdapter.fromJson(cleanedJson)
            if (suggestion != null && suggestion.nahuatSentence.isNotBlank()) {
                AiResult.Success(suggestion = suggestion, isFromLiveAi = true)
            } else {
                AiResult.Error(
                    message = "El formato devuelto no cumplió el esquema esperado. Se cargó la regla morfológica documentada.",
                    fallbackSuggestion = fallback
                )
            }
        } catch (e: Exception) {
            AiResult.Error(
                message = "Aviso de conexión: ${e.localizedMessage ?: "Fallo de red"}. Se activó la regla gramatical documentada para que continúes validando sin interrupción.",
                fallbackSuggestion = fallback
            )
        }
    }
}
