package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldTertiary
import com.example.ui.theme.JadeSecondary
import com.example.ui.theme.TerracottaPrimary

@Composable
fun AboutCultureScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("about_culture_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(TerracottaPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HistoryEdu,
                            contentDescription = null,
                            tint = TerracottaPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "NÁHUAT VIVO",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TerracottaPrimary
                            )
                        )
                        Text(
                            text = "Revitalización del Idioma Pipil de El Salvador",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "El Náhuat (Pipil / Nawat) es la única lengua indígena viva de origen uto-azteca que sobrevive en El Salvador. Hablada principalmente en Santo Domingo de Guzmán (Witzapan), Cuisnahuat y Tacuba, enfrenta una seria amenaza de extinción que los jóvenes salvadoreños podemos revertir a través del estudio, la tecnología y el respeto por nuestros abuelos y abuelas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Guía Fonética Escrita
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = JadeSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Guía de Pronunciación Fonética",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val phoneticsRules = listOf(
                    Pair("TZ", "Suena como [ts], como en la palabra 'tsunami' o 'catsup'. Ej: 'Metzti' [méts-ti]."),
                    Pair("SH", "Suena como en el inglés 'show' o 'she'. Ej: 'Shuchit' [shú-chit]."),
                    Pair("J", "Es una aspiración suave (como la 'h' en inglés 'house'), no rasposa. Ej: 'Kojtan' [kóh-tan]."),
                    Pair("W", "Suena como 'u' semiconsonántica (como en 'hueso' o inglés 'water'). Ej: 'Kwawit' [kwá-wit]."),
                    Pair("K", "Suena como la 'k' o 'c' fuerte española, antes de cualquier vocal."),
                    Pair("Acento", "La mayoría de palabras en náhuat pipil son graves (se acentúan en la penúltima sílaba).")
                )

                phoneticsRules.forEach { (letter, explanation) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = JadeSecondary.copy(alpha = 0.15f),
                            modifier = Modifier.width(52.dp)
                        ) {
                            Text(
                                text = letter,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = JadeSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Fuentes Documentadas y Bibliografía (Dato Clave)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = TerracottaPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Fuentes Documentadas Oficiales",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Todo el vocabulario de esta app ha sido contrastado contra fuentes de rigurosa lingüística descriptiva:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                val sources = listOf(
                    "• King, Alan R. (2004). Gramática Pipil. Iniciativa para la Recuperación del Idioma Náhuat (IRIN).",
                    "• King, Alan R. (2004). Vocabulario con Claves de Identificación: Nawat-Castellano.",
                    "• Testimonios orales de las maestras y abuelas nantzitzin de Santo Domingo de Guzmán (Cuna Náhuat).",
                    "• Campbell, Lyle (1985). The Pipil Language of El Salvador. Mouton Grammar Library.",
                    "• Lemus, Jorge E. (2008). Propuesta de Alfabeto de la Lengua Náhuat de El Salvador."
                )

                sources.forEach { s ->
                    Text(
                        text = s,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }
            }
        }

        // Rigor y Ética de la IA
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = GoldTertiary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Compromiso Ético y Anti-Alucinación",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Las IA generativas a menudo confunden el Náhuat salvadoreño con variantes del náhuatl del centro de México. Por eso, en esta app es obligatorio el proceso humano donde el estudiante contrasta cada propuesta con las fuentes de Alan R. King e IRIN antes de publicarla.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
