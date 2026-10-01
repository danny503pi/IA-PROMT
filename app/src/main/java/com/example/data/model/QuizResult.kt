package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Registro persistente del avance y desempeño en el Quiz de 5 preguntas.
 * Guarda la fecha, puntaje y porcentaje de aciertos del estudiante.
 */
@Entity(tableName = "quiz_results")
data class QuizResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val score: Int,
    val totalQuestions: Int = 5,
    val percentage: Int = (score * 100) / totalQuestions,
    val timestamp: Long = System.currentTimeMillis()
)
