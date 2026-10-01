package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.TerracottaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        nahuat: String,
        phonetics: String,
        spanish: String,
        category: String,
        source: String,
        culturalNote: String
    ) -> Unit
) {
    var nahuat by remember { mutableStateOf("") }
    var phonetics by remember { mutableStateOf("") }
    var spanish by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Cotidiano") }
    var source by remember { mutableStateOf("") }
    var culturalNote by remember { mutableStateOf("") }

    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categoryOptions = listOf(
        "Naturaleza",
        "Animales",
        "Familia",
        "Cotidiano",
        "Números",
        "Saludos y Cortesía",
        "Acciones"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nueva Palabra en Náhuat",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Dato clave obligatorio: Palabra + Significado + Fuente documentada.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.testTag("add_word_error_message")
                    )
                }

                OutlinedTextField(
                    value = nahuat,
                    onValueChange = {
                        nahuat = it
                        errorMessage = null
                    },
                    label = { Text("Palabra en Náhuat *") },
                    placeholder = { Text("ej: Xuchit") },
                    leadingIcon = { Icon(Icons.Default.Translate, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_nahuat_word")
                )

                OutlinedTextField(
                    value = phonetics,
                    onValueChange = { phonetics = it },
                    label = { Text("Pronunciación escrita fonética") },
                    placeholder = { Text("ej: [shú-chit]") },
                    leadingIcon = { Icon(Icons.Default.RecordVoiceOver, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_phonetics")
                )

                OutlinedTextField(
                    value = spanish,
                    onValueChange = {
                        spanish = it
                        errorMessage = null
                    },
                    label = { Text("Significado en Español *") },
                    placeholder = { Text("ej: Flor / Brote") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_spanish_meaning")
                )

                // Category dropdown
                ExposedDropdownMenuBox(
                    expanded = isCategoryDropdownExpanded,
                    onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isCategoryDropdownExpanded,
                        onDismissRequest = { isCategoryDropdownExpanded = false }
                    ) {
                        categoryOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    category = opt
                                    isCategoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = source,
                    onValueChange = {
                        source = it
                        errorMessage = null
                    },
                    label = { Text("Fuente Documentada (Obligatoria) *") },
                    placeholder = { Text("ej: Alan R. King / Diccionario IRIN") },
                    leadingIcon = { Icon(Icons.Default.Book, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_source")
                )

                OutlinedTextField(
                    value = culturalNote,
                    onValueChange = { culturalNote = it },
                    label = { Text("Nota cultural o uso tradicional") },
                    placeholder = { Text("ej: Usada en oraciones campesinas de siembra") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_cultural_note")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nahuat.isBlank()) {
                        errorMessage = "La palabra en náhuat no puede estar vacía."
                    } else if (spanish.isBlank()) {
                        errorMessage = "Debes ingresar el significado en español."
                    } else if (source.isBlank()) {
                        errorMessage = "La fuente documentada es obligatoria para garantizar la autenticidad lingüística."
                    } else {
                        onConfirm(
                            nahuat.trim(),
                            phonetics.trim(),
                            spanish.trim(),
                            category,
                            source.trim(),
                            culturalNote.trim()
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                modifier = Modifier.testTag("confirm_add_word_button")
            ) {
                Text("Guardar en Diccionario")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_add_word_button")
            ) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}
