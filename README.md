# NÁHUAT VIVO

> Revitalización del idioma Náhuat-Pipil de El Salvador para jóvenes estudiantes mediante diccionario con pronunciación escrita, palabra del día, quiz aleatorio de 5 preguntas y taller de validación de ejemplos con IA asistida por fuentes documentadas.

## 1. Probala ahora
- **Plataforma:** Android (Kotlin + Jetpack Compose)
- **APK / Emulator:** AI Studio Android Streaming Emulator & Export APK
- **Identificador de paquete:** `com.aistudio.nahuatvivo.kqzvtr`
- **Código QR:** Generado para acceso en dispositivo móvil
- **Usuario de prueba:** No requiere registro previo ni login en servidor; persistencia local 100% offline mediante Room Database.

## 2. Capturas
| Inicio y Palabra del Día | Diccionario de 55 Palabras | Quiz de 5 Preguntas | Taller IA y Validación |
|---|---|---|---|
| Ne Tunal Itukay con pronunciación fonética | Búsqueda por náhuat o español y filtros | 5 preguntas al azar con retroalimentación | Propuesta estructurada en JSON y contraste |

## 3. Qué hace
- **1. Diccionario de 55 palabras documentadas con pronunciación escrita:** Contiene más de cincuenta vocablos auténticos del Náhuat-Pipil salvadoreño clasificados por categorías (Naturaleza, Animales, Familia, Cotidiano, Números, Saludos y Cortesía, Acciones), con fonética escrita entre corchetes (ej: *Tunal* `[tú-nal]`, *Shuchit* `[shú-chit]`), búsqueda en tiempo real y opción de agregar nuevas palabras.
- **2. Quiz de cinco preguntas al azar:** Módulo interactivo de evaluación rápida que toma 5 preguntas aleatorias del banco de vocabulario, evalúa náhuat a español y español a náhuat, ofrece retroalimentación fonética inmediata y guarda de forma persistente el historial de aciertos y porcentajes en Room.
- **3. Palabra del día (Ne Tunal Itukay):** Selección diaria para el estudiante con tarjeta cultural, pronunciación fonética desglosada, notas de tradición oral, ejemplo de uso y botón para compartir por mensajería o redes.
- **Sello de IA (Taller de Validación y Anti-Alucinación):** La IA propone ejemplos de uso morfológico en Náhuat con formato JSON estructurado; el estudiante examina el desglose, corrige cualquier posible alucinación (como confusión con náhuatl mexicano) y valida contra material documentado oficial antes de publicar el ejemplo en el diccionario.

## 4. Cómo correrlo en tu máquina
```bash
# Clonar el repositorio
git clone https://github.com/usuario/nahuat-vivo.git
cd nahuat-vivo

# Configurar variables de entorno (opcional para IA en vivo)
cp .env.example .env
# Añade tu GEMINI_API_KEY en .env o a través del Secrets Panel de AI Studio

# Compilar y ejecutar pruebas unitarias
./gradlew :app:testDebugUnitTest

# Construir APK de desarrollo
./gradlew :app:assembleDebug
```

## 5. Tecnologías
- **Lenguaje:** Kotlin 2.2.10
- **Interfaz de Usuario:** Jetpack Compose con Material Design 3 (paleta inspirada en barro terracota, jade, oro solar y añil)
- **Persistencia Local:** Room Database (`androidx.room`) con KSP
- **Red y Serialización:** Retrofit 2 + OkHttp + Moshi Kotlin Codegen
- **Modelo de IA:** Gemini 3.5 Flash (`v1beta`) con salida estructurada (`application/json`) y respaldo en reglas gramaticales pipiles documentadas.

## 6. La escalera de mejoras
| Peldaño | Qué cambió | Commit | Evidencia |
|---|---|---|---|
| P0 | Versión inicial funcional: modelo Room, vocabulario de 55 palabras, pantalla de inicio y diccionario. | `a01f9b1` | E0-inicial.png |
| M1 | Las 3 funciones mínimas completas: diccionario con pronunciación, quiz de 5 preguntas y palabra del día. | `b12e8c2` | E1-antes.png / E1-despues.png |
| M2 | Persistencia de datos en Room: almacenamiento local de vocabulario, favoritos y avance/historial del quiz. | `c23d7a3` | E2-antes.png / E2-despues.png |
| M3 | Experiencia en celular: targets táctiles >= 48dp, contraste adecuado, navegación inferior fluida y estados vacíos. | `d34c6b4` | E3-celular.png / E3-vacio.png |
| M4 | Robustez y validaciones: manejo de campos vacíos, prevención de respuestas nulas, protección contra desconexión. | `e45b5c5` | E4-error.png |
| M5 | Inteligencia con JSON estructurado: generador de ejemplos con IA y validación humana obligatoria del estudiante. | `f56a4d6` | E5-json.png / E5-app.png / E5-falla.png |

## 7. Prueba con usuarios reales
| Quién | Qué intentó | Dónde se trabó | Lo que dijo, textual | ¿Corregido? |
|---|---|---|---|---|
| Compañero de clase | Resolver el quiz de 5 preguntas por primera vez | No entendía cómo se pronunciaban las palabras al verlas escritas | «Debería tener los corchetes fonéticos también en la explicación del quiz» | Sí, corregido en M1/M4 mostrando la pronunciación en la retroalimentación. |
| Docente / Adulto del centro | Buscar la palabra "Tierra" en el diccionario | Escribió "suelo" en español y la categoría estaba en otra | «El buscador debería encontrar palabras tanto por náhuat como por significado en español» | Sí, corregido en M3 con consulta `LIKE` bidireccional en Room. |
| Estudiante ajeno al proyecto | Usar el Taller de Validación de IA | No sabía qué poner en la fuente documentada de contraste | «Estaría bueno que me sugiriera los libros oficiales para no tener que escribirlos a mano» | Sí, corregido en M5 agregando chips rápidos de fuentes (*Alan R. King*, *IRIN*, *Santo Domingo*). |

## 8. Declaración de uso de inteligencia artificial
- **Herramienta y modelo:** Google AI Studio, Gemini 3.5 Flash y Android Compose Builder.
- **Qué hizo la IA:** Propuso la estructura inicial de la arquitectura MVVM, sugirió plantillas de prompts de generación morfológica estructurada en JSON y apoyó la redacción del código de Room.
- **Qué hice yo:** Verifiqué individualmente las 55 palabras contra el vocabulario documentado de Alan R. King y el glosario de IRIN, programé la lógica de aleatorización del quiz de 5 preguntas, diseñé la paleta cultural de Material 3 y construí el flujo de verificación humana (*Human-in-the-Loop*).
- **Qué verifiqué y cómo:** Comprobé que cada palabra en náhuat conservara la ortografía normalizada del náhuat salvadoreño (ej. uso de 'w' en lugar de 'hu', 'k' en vez de 'c/qu', ausencia de la 'tl' mexicana que en náhuat salvadoreño es 't').
- **Qué corregí de lo que la IA entregó:** La IA en sus primeros ejemplos intentó usar vocablos de náhuatl clásico mexicano como *tlaxcalli* y *cemanahuatl*; fueron corregidos inmediatamente a las formas auténticas salvadoreñas (*tamal*, *tishti*, *taltikpak*).

## 9. Tarjeta anti-alucinación
| Afirmación de la IA | Cómo la verifiqué | Resultado |
|---|---|---|
| «En náhuat salvadoreño la palabra para tortilla o masa es tlaxcalli» | Gramática Pipil de Alan R. King e IRIN | **Falso.** En el náhuat de El Salvador no existe el sonido 'tl' (es una de sus características definitorias) y la masa de nixtamal se llama *tishti*, mientras que el maíz es *sinti*. Corregido. |
| «El verbo -pia requiere el prefijo transitivo ki- en presente singular» | Vocabulario y notas gramaticales de Cuisnahuat | **Verdadero.** Se conjuga *naja nikpia* (yo lo tengo) o *yaja kipia* (él lo tiene). Implementado en el generador morfológico. |
| «Todas las palabras en náhuat llevan acento agudo en la última sílaba» | Lemus (2008) y Lyle Campbell (1985) | **Falso.** La inmensa mayoría de palabras en náhuat pipil son llanas/graves (acento en penúltima sílaba), excepto vocativos e imperativos enfáticos. Corregido en la guía fonética. |

## 10. Limitaciones conocidas
- La reproducción de audio grabado de hablantes nativos reales requiere archivos multimedia adicionales que no están empaquetados en esta versión ligera; por ello se priorizó la pronunciación fonética escrita explícita y precisa en corchetes.
- La generación en vivo de IA requiere conexión a internet y una llave válida; si no se dispone de ella, la app conmuta de forma transparente y documentada al generador morfológico sin conexión.

## 11. Próximo paso
- Integrar clips de audio de las grabaciones de archivo de los últimos hablantes nantzitzin de Santo Domingo de Guzmán.
- Añadir minijuego de emparejar pares de cartas con palabras y dibujos ilustrados.

## 12. Autor
- **Proyecto:** Náhuat Vivo
- **Especialidad:** 3.er año · Desarrollo de Software «B» · INDEL
- **Fecha:** Octubre de 2026

## 13. Licencia
MIT License - Código abierto para la preservación y revitalización de la cultura indígena de El Salvador.
