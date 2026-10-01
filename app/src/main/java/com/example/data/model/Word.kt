package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad principal del vocabulario Náhuat-Pipil de El Salvador.
 * Cumple con el dato clave de la práctica: Palabra + Significado + Fuente Documentada.
 */
@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nahuat: String,
    val phonetics: String,
    val spanish: String,
    val category: String,
    val source: String,
    val culturalNote: String = "",
    val exampleSentenceNahuat: String = "",
    val exampleSentenceSpanish: String = "",
    val isValidated: Boolean = true,
    val validatedSourceNote: String = "",
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false
)
