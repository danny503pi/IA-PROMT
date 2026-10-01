package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizResult
import com.example.data.repository.QuizQuestion
import com.example.ui.QuizState
import com.example.ui.theme.ErrorCrimson
import com.example.ui.theme.GoldTertiary
import com.example.ui.theme.JadeSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TerracottaPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun QuizScreen(
    quizState: QuizState,
    quizResults: List<QuizResult>,
    onStartQuiz: () -> Unit,
    onSelectOption: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onResetQuiz: () -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("quiz_screen")
    ) {
        when (quizState) {
            is QuizState.Idle -> {
                QuizIdleView(
                    quizResults = quizResults,
                    onStartQuiz = onStartQuiz,
                    onClearHistory = onClearHistory
                )
            }
            is QuizState.InProgress -> {
                QuizInProgressView(
                    state = quizState,
                    onSelectOption = onSelectOption,
                    onSubmitAnswer = onSubmitAnswer,
                    onNextQuestion = onNextQuestion
                )
            }
            is QuizState.Finished -> {
                QuizFinishedView(
                    state = quizState,
                    onPlayAgain = onStartQuiz,
                    onReturnHome = onResetQuiz
                )
            }
        }
    }
}

@Composable
fun QuizIdleView(
    quizResults: List<QuizResult>,
    onStartQuiz: () -> Unit,
    onClearHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(TerracottaPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = TerracottaPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Quiz de Náhuat Vivo",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "5 preguntas al azar para entrenar tu memoria y comprensión del Náhuat-Pipil salvadoreño.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onStartQuiz,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_quiz_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Iniciar Quiz de 5 Preguntas",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Quiz History Section (M2 - Avance del quiz guardado en Room)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = JadeSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Historial de Avance (${quizResults.size} partidas)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (quizResults.isNotEmpty()) {
                IconButton(onClick = onClearHistory) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Limpiar historial",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        if (quizResults.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Aún no has completado ningún quiz",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tus resultados y porcentaje de aciertos se guardarán automáticamente en tu dispositivo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                quizResults.take(10).forEach { result ->
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val dateStr = dateFormat.format(Date(result.timestamp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = dateStr,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Quiz de ${result.totalQuestions} preguntas",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (result.score >= 4) SuccessGreen.copy(alpha = 0.18f)
                                else if (result.score >= 3) GoldTertiary.copy(alpha = 0.18f)
                                else ErrorCrimson.copy(alpha = 0.18f)
                            ) {
                                Text(
                                    text = "${result.score}/${result.totalQuestions} (${result.percentage}%)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (result.score >= 4) SuccessGreen
                                    else if (result.score >= 3) GoldTertiary
                                    else ErrorCrimson,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizInProgressView(
    state: QuizState.InProgress,
    onSelectOption: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit
) {
    val currentQuestion = state.questions[state.currentIndex]
    val progress = (state.currentIndex + 1).toFloat() / state.questions.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Progress & Score Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pregunta ${state.currentIndex + 1} de ${state.questions.size}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TerracottaPrimary
            )
            Text(
                text = "Puntaje: ${state.currentScore}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = JadeSecondary
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = TerracottaPrimary
        )

        // Question Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quiz_question_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = currentQuestion.targetWord.category,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = currentQuestion.questionText,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // 4 Options
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            currentQuestion.options.forEachIndexed { index, option ->
                val isSelected = state.selectedOption == index
                val isCorrect = index == currentQuestion.correctIndex

                val backgroundColor: Color
                val borderColor: Color
                val textColor: Color

                if (state.isAnswerSubmitted) {
                    if (isCorrect) {
                        backgroundColor = SuccessGreen.copy(alpha = 0.15f)
                        borderColor = SuccessGreen
                        textColor = SuccessGreen
                    } else if (isSelected) {
                        backgroundColor = ErrorCrimson.copy(alpha = 0.15f)
                        borderColor = ErrorCrimson
                        textColor = ErrorCrimson
                    } else {
                        backgroundColor = MaterialTheme.colorScheme.surface
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant
                    }
                } else {
                    if (isSelected) {
                        backgroundColor = TerracottaPrimary.copy(alpha = 0.15f)
                        borderColor = TerracottaPrimary
                        textColor = TerracottaPrimary
                    } else {
                        backgroundColor = MaterialTheme.colorScheme.surface
                        borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        textColor = MaterialTheme.colorScheme.onSurface
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = backgroundColor,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !state.isAnswerSubmitted) {
                            onSelectOption(index)
                        }
                        .testTag("quiz_option_$index")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(borderColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${('A'.code + index).toChar()}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = borderColor
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = textColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Feedback Explanation Box (Visible after submitting answer)
        AnimatedVisibility(visible = state.isAnswerSubmitted) {
            val isUserCorrect = state.selectedOption == currentQuestion.correctIndex
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isUserCorrect) SuccessGreen.copy(alpha = 0.12f) else ErrorCrimson.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isUserCorrect) SuccessGreen else ErrorCrimson
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isUserCorrect) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isUserCorrect) SuccessGreen else ErrorCrimson,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isUserCorrect) "¡Correcto! (¡Yek!)" else "Respuesta Incorrecta",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isUserCorrect) SuccessGreen else ErrorCrimson
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = currentQuestion.explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons
        if (!state.isAnswerSubmitted) {
            Button(
                onClick = onSubmitAnswer,
                enabled = state.selectedOption != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_quiz_answer_button"),
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Comprobar Respuesta",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        } else {
            Button(
                onClick = onNextQuestion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("next_quiz_question_button"),
                colors = ButtonDefaults.buttonColors(containerColor = JadeSecondary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (state.currentIndex + 1 < state.questions.size) "Siguiente Pregunta" else "Ver Resultados Finales",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun QuizFinishedView(
    state: QuizState.Finished,
    onPlayAgain: () -> Unit,
    onReturnHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Result Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quiz_finished_card"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            if (state.finalScore >= 4) SuccessGreen.copy(alpha = 0.15f)
                            else if (state.finalScore >= 3) GoldTertiary.copy(alpha = 0.15f)
                            else TerracottaPrimary.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = if (state.finalScore >= 4) SuccessGreen
                        else if (state.finalScore >= 3) GoldTertiary
                        else TerracottaPrimary,
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "¡Quiz Completado!",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${state.finalScore} / ${state.totalQuestions} respuestas correctas",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (state.finalScore >= 4) SuccessGreen
                        else if (state.finalScore >= 3) GoldTertiary
                        else TerracottaPrimary
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Cultural Message
                val culturalMessageNahuat: String
                val culturalMessageSpanish: String

                when (state.finalScore) {
                    5 -> {
                        culturalMessageNahuat = "¡Sujsul yek! ¡Titaketi yek!"
                        culturalMessageSpanish = "¡Excelente! Has demostrado gran respeto y memoria por la lengua de nuestros ancestros."
                    }
                    4, 3 -> {
                        culturalMessageNahuat = "¡Yek tajpala! ¡Shimachti sejse tunal!"
                        culturalMessageSpanish = "¡Buen esfuerzo! Continúa fortaleciendo tus conocimientos día con día."
                    }
                    else -> {
                        culturalMessageNahuat = "¡Te ma timukwalanikan! ¡Tikakis ne tajtuli!"
                        culturalMessageSpanish = "¡No te desanimes! Escucha las palabras y vuelve a repasar el diccionario."
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = culturalMessageNahuat,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = JadeSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = culturalMessageSpanish,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onPlayAgain,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("play_again_button"),
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Replay, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Repetir Quiz")
            }

            OutlinedButton(
                onClick = onReturnHome,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("return_home_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Volver al Inicio")
            }
        }

        // Review list of 5 questions
        Text(
            text = "Revisión de Preguntas",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Start)
        )

        state.reviewQuestions.forEachIndexed { i, q ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "${i + 1}. ${q.questionText}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Respuesta correcta: ${q.options[q.correctIndex]}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = SuccessGreen
                    )
                    Text(
                        text = q.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
