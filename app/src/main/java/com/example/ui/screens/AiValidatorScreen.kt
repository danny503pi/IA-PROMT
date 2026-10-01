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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Word
import com.example.ui.AiValidatorState
import com.example.ui.theme.GoldTertiary
import com.example.ui.theme.JadeSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TerracottaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiValidatorScreen(
    words: List<Word>,
    selectedWord: Word?,
    aiValidatorState: AiValidatorState,
    onRequestAiExample: (Word) -> Unit,
    onValidateAndPublish: (
        originalWord: Word,
        validatedSentence: String,
        validatedTranslation: String,
        documentedSource: String
    ) -> Boolean,
    onResetValidator: () -> Unit,
    onNavigateToDictionary: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeWord by remember(selectedWord, words) {
        mutableStateOf(selectedWord ?: words.firstOrNull())
    }

    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Human-in-the-loop validation editable inputs
    var editableSentence by remember { mutableStateOf("") }
    var editableTranslation by remember { mutableStateOf("") }
    var documentedSource by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    // When new suggestion arrives, populate the editable fields
    LaunchedEffect(aiValidatorState) {
        if (aiValidatorState is AiValidatorState.ReadyToVerify) {
            editableSentence = aiValidatorState.suggestion.nahuatSentence
            editableTranslation = aiValidatorState.suggestion.spanishTranslation
            documentedSource = "Contrastado con: Gramática Pipil (Alan R. King)"
            validationError = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("ai_validator_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Header
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = GoldTertiary.copy(alpha = 0.15f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GoldTertiary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "SELLO DE IA · TALLER DE VALIDACIÓN",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = GoldTertiary
                    )
                    Text(
                        text = "La IA propone el ejemplo morfológico y tú lo contrastas contra libros oficiales antes de publicarlo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Word Selector Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "1. Selecciona el vocablo para generar ejemplo:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = activeWord?.let { "${it.nahuat} ${it.phonetics} - ${it.spanish}" } ?: "Selecciona una palabra",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Palabra del Diccionario") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                            .testTag("ai_word_selector_dropdown")
                    )

                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        words.forEach { w ->
                            DropdownMenuItem(
                                text = {
                                    Text(text = "${w.nahuat} ${w.phonetics} — ${w.spanish}")
                                },
                                onClick = {
                                    activeWord = w
                                    isDropdownExpanded = false
                                    onResetValidator()
                                }
                            )
                        }
                    }
                }

                if (activeWord != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fuente base actual: ${activeWord?.source}",
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        activeWord?.let { onRequestAiExample(it) }
                    },
                    enabled = activeWord != null && aiValidatorState !is AiValidatorState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("request_ai_example_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generar Propuesta con IA")
                }
            }
        }

        // Loading State
        if (aiValidatorState is AiValidatorState.Loading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = TerracottaPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Consultando modelo lingüístico de Náhuat...",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Analizando raíces, prefijos de concordancia y reglas fonéticas pipiles...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Ready to Verify State (Human-in-the-Loop)
        if (aiValidatorState is AiValidatorState.ReadyToVerify) {
            val suggestion = aiValidatorState.suggestion

            // Provenance Notice Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (aiValidatorState.isFromLiveAi) JadeSecondary.copy(alpha = 0.15f) else GoldTertiary.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (aiValidatorState.isFromLiveAi) Icons.Default.CloudDone else Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = if (aiValidatorState.isFromLiveAi) JadeSecondary else GoldTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (aiValidatorState.isFromLiveAi)
                            "Propuesta generada mediante Gemini 3.5 Flash (Salida estructurada JSON)."
                        else
                            (aiValidatorState.infoNotice ?: "Modo de Regla Lingüística Documentada activo (gramática Alan R. King)."),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // AI Proposal Inspection Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_suggestion_display_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldTertiary.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PROPUESTA DE LA IA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldTertiary,
                                letterSpacing = 1.sp
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldTertiary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "JSON estructurado",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldTertiary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = suggestion.nahuatSentence,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Text(
                        text = "Guía fonética: ${suggestion.phoneticGuide}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic,
                            color = JadeSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Traducción propuesta: «${suggestion.spanishTranslation}»",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Breakdown
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "DESGLOSE MORFOLÓGICO:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TerracottaPrimary
                                )
                            )
                            Text(
                                text = suggestion.grammaticalBreakdown,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "REGLA O FUENTE SUGERIDA:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = JadeSecondary
                                )
                            )
                            Text(
                                text = suggestion.suggestedSourceOrRule,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (suggestion.verificationTip.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = GoldTertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Para verificar: ${suggestion.verificationTip}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontStyle = FontStyle.Italic,
                                            color = GoldTertiary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Student Validation Editor Form (Human Verification)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_validation_editor_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, JadeSecondary)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = JadeSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2. Validación Humana por el Estudiante",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Revisa la oración. Si detectas alguna alucinación o discordancia, corrígela aquí antes de incorporarla al acervo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (validationError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = validationError ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.testTag("validation_error_text")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editableSentence,
                        onValueChange = {
                            editableSentence = it
                            validationError = null
                        },
                        label = { Text("Oración en Náhuat (revisada) *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_validated_sentence")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = editableTranslation,
                        onValueChange = {
                            editableTranslation = it
                            validationError = null
                        },
                        label = { Text("Traducción al Español *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_validated_translation")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = documentedSource,
                        onValueChange = {
                            documentedSource = it
                            validationError = null
                        },
                        label = { Text("Fuente Documentada de Contraste *") },
                        placeholder = { Text("ej: Alan R. King / Diccionario Nawat IRIN") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_documented_source")
                    )

                    // Quick buttons for authentic sources
                    Text(
                        text = "Fuentes de contraste recomendadas:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                documentedSource = "Gramática Pipil (Alan R. King, p. 112)"
                            }
                        ) {
                            Text(
                                text = "Alan R. King",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                documentedSource = "Diccionario Nawat-Castellano IRIN"
                            }
                        ) {
                            Text(
                                text = "Glosario IRIN",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                documentedSource = "Santo Domingo de Guzmán (Cuna Náhuat)"
                            }
                        ) {
                            Text(
                                text = "Santo Domingo",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val word = activeWord
                            if (word != null) {
                                val success = onValidateAndPublish(
                                    word,
                                    editableSentence,
                                    editableTranslation,
                                    documentedSource
                                )
                                if (!success) {
                                    validationError = "Todos los campos y la fuente documentada son obligatorios para garantizar rigor cultural."
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("publish_validated_example_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = JadeSecondary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Publish, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Validar y Publicar en Diccionario",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Successfully Validated and Saved State
        if (aiValidatorState is AiValidatorState.ValidatedSaved) {
            val savedWord = aiValidatorState.savedWord

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("validation_success_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(2.dp, SuccessGreen)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "¡Ejemplo Validado y Publicado!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "El vocablo «${savedWord.nahuat}» ha sido enriquecido y guardado permanentemente en la base de datos local con su fuente documentada.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SuccessGreen.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = savedWord.exampleSentenceNahuat,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "«${savedWord.exampleSentenceSpanish}»",
                                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = savedWord.validatedSourceNote,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = JadeSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateToDictionary,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("view_in_dictionary_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Ver en Diccionario")
                        }

                        OutlinedButton(
                            onClick = onResetValidator,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("validate_another_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Validar Otra")
                        }
                    }
                }
            }
        }
    }
}
