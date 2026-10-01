package com.example

import com.example.data.local.InitialVocabulary
import com.example.data.remote.OfflineNahuatRuleGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NahuatUnitTest {

    @Test
    fun initialVocabulary_hasAtLeast50Words() {
        assertTrue(
            "El diccionario debe contar con al menos 50 palabras documentadas",
            InitialVocabulary.words.size >= 50
        )
    }

    @Test
    fun initialVocabulary_allWordsHaveRequiredFields() {
        for (word in InitialVocabulary.words) {
            assertTrue("La palabra en náhuat no debe estar vacía", word.nahuat.isNotBlank())
            assertTrue("La pronunciación fonética no debe estar vacía", word.phonetics.isNotBlank())
            assertTrue("El significado en español no debe estar vacío", word.spanish.isNotBlank())
            assertTrue("La fuente documentada no debe estar vacía (dato clave)", word.source.isNotBlank())
            assertTrue("La categoría no debe estar vacía", word.category.isNotBlank())
        }
    }

    @Test
    fun offlineRuleGenerator_generatesStructuredExample() {
        val word = InitialVocabulary.words.first { it.category == "Animales" }
        val suggestion = OfflineNahuatRuleGenerator.generateExample(word)

        assertNotNull(suggestion)
        assertEquals(word.nahuat, suggestion.word)
        assertTrue(suggestion.nahuatSentence.isNotBlank())
        assertTrue(suggestion.spanishTranslation.isNotBlank())
        assertTrue(suggestion.grammaticalBreakdown.isNotBlank())
        assertTrue(suggestion.suggestedSourceOrRule.isNotBlank())
    }
}
