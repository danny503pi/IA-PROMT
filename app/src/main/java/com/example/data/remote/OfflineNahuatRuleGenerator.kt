package com.example.data.remote

import com.example.data.model.Word

/**
 * Generador de reglas y ejemplos pedagógicos documentados para el modo sin conexión
 * o cuando no se dispone de llave de API.
 * Aplica reglas de concordancia morfológica del Náhuat-Pipil salvadoreño según Alan R. King.
 */
object OfflineNahuatRuleGenerator {

    fun generateExample(word: Word): NahuatExampleSuggestion {
        val nahuatClean = word.nahuat.trim()
        val lower = nahuatClean.lowercase()

        return when (word.category) {
            "Animales" -> {
                NahuatExampleSuggestion(
                    word = nahuatClean,
                    nahuatSentence = "Ne $lower nemi tik ne kojtan iwan ipilawan.",
                    phoneticGuide = "Ne ${word.phonetics} né-mi tik ne kóh-tan í-wan i-pí-la-wan",
                    spanishTranslation = "El/La ${word.spanish.lowercase()} está en el bosque con sus crías.",
                    grammaticalBreakdown = "Ne (artículo definido) + $nahuatClean + nemi (verbo de estado: estar/andar) + tik (en) + ne kojtan (el bosque) + iwan (con) + ipilawan (sus hijos/crías)",
                    suggestedSourceOrRule = "Regla de locación con verbo intransitivo 'nemi' y posposición 'tik', Gramática Pipil de Alan R. King, p. 78.",
                    verificationTip = "Verificar si el sustantivo toma sufijo absolutivo -t/-ti y contrastar el plural 'ipilawan' contra el vocabulario IRIN."
                )
            }
            "Naturaleza" -> {
                NahuatExampleSuggestion(
                    word = nahuatClean,
                    nahuatSentence = "Ne $lower kipia sujsul wey ipatiw tik tutal.",
                    phoneticGuide = "Ne ${word.phonetics} kí-pi-a súh-sul wey i-pá-tiw tik tu-tal",
                    spanishTranslation = "El/La ${word.spanish.lowercase()} tiene un valor muy grande en nuestra tierra.",
                    grammaticalBreakdown = "Ne (el/la) + $nahuatClean + ki-pia (lo-tiene: prefijo acusativo ki- + pia) + sujsul (adverbio: muy) + wey (adjetivo: grande) + ipatiw (su valor/precio) + tik tutal (en nuestra tierra)",
                    suggestedSourceOrRule = "Construcción con verbo transitivo 'pia' y prefijo de tercera persona singular 'ki-', Alan R. King p. 102.",
                    verificationTip = "Confirmar que 'tutal' lleva el prefijo posesivo de 1ra persona plural 'tu-' (nuestra)."
                )
            }
            "Acciones" -> {
                NahuatExampleSuggestion(
                    word = nahuatClean,
                    nahuatSentence = "Yaja kineki $lower tik ne techan.",
                    phoneticGuide = "Yá-ha ki-né-ki ${word.phonetics} tik ne té-chan",
                    spanishTranslation = "Él/Ella quiere ${word.spanish.lowercase()} en el pueblo.",
                    grammaticalBreakdown = "Yaja (pronombre 3ra persona) + ki-neki (lo quiere) + $lower (verbo en infinitivo/presente) + tik (en) + ne techan (el pueblo)",
                    suggestedSourceOrRule = "Verbos concatenados con 'neki' (querer), Gramática Náhuat de El Salvador.",
                    verificationTip = "Revisar si el verbo requiere prefijo transitivo 'ki-' cuando lleva objeto directo determinado."
                )
            }
            "Saludos y Cortesía" -> {
                NahuatExampleSuggestion(
                    word = nahuatClean,
                    nahuatSentence = "¡$nahuatClean, nutasujka ikniwan!",
                    phoneticGuide = "${word.phonetics}, nu-ta-súh-ka ik-ní-wan",
                    spanishTranslation = "¡${word.spanish}! ¡Mis queridos hermanos/amigos!",
                    grammaticalBreakdown = "$nahuatClean (fórmula tradicional) + nu-tasujka (mi querido/apreciado) + ikniwan (hermanos/compañeros en plural -wan)",
                    suggestedSourceOrRule = "Trato afectuoso comunitario registrado en Cuentos y Diálogos de Santo Domingo de Guzmán.",
                    verificationTip = "Comprobar que el sufijo plural de posesivo de parentesco sea '-wan'."
                )
            }
            "Números" -> {
                NahuatExampleSuggestion(
                    word = nahuatClean,
                    nahuatSentence = "Nipia $lower shuchit tik numawan.",
                    phoneticGuide = "Ni-pí-a ${word.phonetics} shú-chit tik nu-má-wan",
                    spanishTranslation = "Tengo ${word.spanish.lowercase()} flor(es) en mis manos.",
                    grammaticalBreakdown = "Ni-pia (1ra persona: yo tengo) + $lower (numeral) + shuchit (sustantivo flor) + tik numawan (en mis manos: nu- + ma- + -wan)",
                    suggestedSourceOrRule = "Los numerales náhuat preceden directamente al sustantivo sin sufijo clasificador, Alan R. King.",
                    verificationTip = "Verificar si el sustantivo contado mantiene la forma singular o si se pluraliza."
                )
            }
            else -> {
                NahuatExampleSuggestion(
                    word = nahuatClean,
                    nahuatSentence = "Ne $lower sujsul yek pal tejemet.",
                    phoneticGuide = "Ne ${word.phonetics} súh-sul yek pal te-hé-met",
                    spanishTranslation = "El/La ${word.spanish.lowercase()} es muy bueno/a para nosotros.",
                    grammaticalBreakdown = "Ne (artículo) + $nahuatClean + sujsul (muy) + yek (bueno/hermoso) + pal (preposición: para) + tejemet (nosotros)",
                    suggestedSourceOrRule = "Oración copulativa sin verbo 'ser' explícito (predicación nominal), característica del náhuat.",
                    verificationTip = "Confirmar que las oraciones de cualidad en náhuat no requieren cópula 'ser/estar'."
                )
            }
        }
    }
}
