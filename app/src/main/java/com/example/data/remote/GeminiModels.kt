package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig? = null,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val responseMimeType: String? = "application/json",
    val temperature: Float? = 0.3f
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null
)

/**
 * Esquema JSON estructurado devuelto por la IA para que el estudiante valide
 * contra la literatura documentada (IRIN, Alan R. King, testimonios de abuelos).
 */
@JsonClass(generateAdapter = true)
data class NahuatExampleSuggestion(
    @param:Json(name = "word") val word: String = "",
    @param:Json(name = "nahuatSentence") val nahuatSentence: String = "",
    @param:Json(name = "phoneticGuide") val phoneticGuide: String = "",
    @param:Json(name = "spanishTranslation") val spanishTranslation: String = "",
    @param:Json(name = "grammaticalBreakdown") val grammaticalBreakdown: String = "",
    @param:Json(name = "suggestedSourceOrRule") val suggestedSourceOrRule: String = "",
    @param:Json(name = "verificationTip") val verificationTip: String = ""
)
