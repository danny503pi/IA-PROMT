# Bitácora de Prompts · NÁHUAT VIVO

## P0 · Prompt Cero (Que exista)
**Prompt textual:**
```
ROL: Sos un desarrollador senior de aplicaciones Android con Kotlin y Jetpack Compose.

CONTEXTO: Estoy construyendo una app llamada NÁHUAT VIVO para estudiantes curiosos por la lengua indígena de El Salvador.
El problema que resuelve es: Quedan pocos hablantes de náhuat y casi ningún material hecho para jóvenes.

TAREA: Generá la primera versión funcional, con estas tres funciones y nada más:
1. Diccionario de cincuenta palabras con pronunciación escrita fonética y fuente documentada.
2. Quiz interactivo de cinco preguntas al azar con evaluación y opciones múltiples.
3. Palabra del día con tarjeta cultural, pronunciación y ejemplo de uso.

RESTRICCIONES: En español, sin librerías de pago, sin login, sin base de datos en servidor todavía. Que se vea bien en un celular, con colores cálidos e indígenas (terracota, jade, oro y añil). Código comentado en los puntos donde alguien vaya a equivocarse.

FORMATO DE SALIDA: Los archivos completos, cada uno con su nombre, y al final una lista de lo que NO hiciste y por qué.

CRITERIO DE ACEPTACIÓN: Abro la app, veo el vocabulario con pronunciación y puedo iniciar un quiz de 5 preguntas sin ningún error en la consola.
```
**Qué devolvió:** Estructura inicial del proyecto con Jetpack Compose, modelo de datos `Word`, datos base iniciales y pantalla de diccionario con navegación.
**Qué acepté:** La arquitectura MVVM limpia y los componentes declarativos de Jetpack Compose.
**Qué corregí a mano:** Amplié el banco a 55 palabras verificadas contra la literatura oficial de Alan R. King y normalicé la ortografía salvadoreña (eliminando formas mexicanas).
**Evidencia:** `evidencias/E0-inicial.png`
**Commit:** `a01f9b1`

---

## M1 · Función (Que sirva)
**Prompt textual:**
```
La app ya muestra el diccionario de palabras. Necesito completar las tres funciones mínimas exigidas por el ejercicio:
1. Asegurar las 50+ palabras con pronunciación fonética clara y categoría.
2. Quiz de cinco preguntas al azar con banco aleatorizado, puntaje dinámico y retroalimentación inmediata.
3. Palabra del día calculada de forma estable según el día del año.

No reescribas lo que ya funciona. Dame únicamente:
1. Los fragmentos nuevos o modificados, indicando en qué archivo y en qué parte va cada uno.
2. Una prueba manual de tres pasos para comprobar que quedó bien.
3. Qué podría romperse en el resto de la app por este cambio.
```
**Qué devolvió:** La lógica del `QuizScreen` con cálculo de 5 preguntas y distractores del mismo vocabulario, además del selector determinístico de la palabra del día en `NahuatRepository`.
**Qué acepté:** El algoritmo de selección de 3 distractores aleatorios sin repetir la respuesta correcta.
**Qué corregí a mano:** Añadí las notas culturales en la retroalimentación de cada pregunta del quiz para reforzar el aprendizaje.
**Evidencia:** `evidencias/E1-antes.png` y `evidencias/E1-despues.png`
**Commit:** `b12e8c2`

---

## M2 · Datos (Que recuerde)
**Prompt textual:**
```
Quiero que los datos de la app no se pierdan al cerrarla.

Usá Room Database con KSP y explicame:
1. Dónde queda guardada la información exactamente (SQLite local en el almacenamiento interno de la app).
2. Qué pasa si el usuario borra el caché o cambia de dispositivo.
3. Cómo hago para persistir tanto las palabras (con sus favoritos y palabras agregadas por el usuario) como el historial del quiz (fecha, aciertos de 5 preguntas y porcentaje).

Dame el código de Room con Entity, Dao y Repository, y el prepoblado de las 55 palabras documentadas.
```
**Qué devolvió:** Entidades `Word` y `QuizResult`, interfaz `WordDao`, base de datos `AppDatabase` con callback de prepoblado y métodos reactivos con `Flow`.
**Qué acepté:** La implementación de `Flow<List<Word>>` y `Flow<List<QuizResult>>` para actualizaciones reactivas en la UI de Compose.
**Qué corregí a mano:** Agregué la columna `validatedSourceNote` en `Word` para almacenar la verificación manual del estudiante del sello de IA.
**Evidencia:** `evidencias/E2-antes.png` y `evidencias/E2-despues.png`
**Commit:** `c23d7a3`

---

## M3 · Experiencia (Que se entienda)
**Prompt textual:**
```
Ajustá la interfaz de la app con estos requisitos, sin cambiar la lógica:

1. Se usa bien desde 320 px de ancho, con una sola mano y sin hacer zoom.
2. Contraste suficiente para leerse al sol; texto nunca menor a 16 px en explicaciones y 18 px en palabras principales.
3. Todos los campos con etiqueta visible, no solo con texto de ejemplo dentro.
4. Un solo botón principal por pantalla; los demás, secundarios o tonales.
5. Estado vacío: qué se muestra cuando no hay resultados de búsqueda o cuando no hay partidas en el quiz, con una frase que invite a la primera acción.
6. Mensajes de éxito y de error visibles, en español, sin palabras técnicas.

Dame los cambios y decime cuál de los seis puntos NO pudiste cumplir y por qué.
```
**Qué devolvió:** Ajustes de espaciado en la cuadrícula de 8dp, componentes `EmptyDictionaryState`, etiquetas en `AddWordDialog` y chips de categoría con contraste accesible.
**Qué acepté:** El diseño de tarjetas elevadas con borde sutil y estados vacíos con botón de acción ("Restablecer Filtros", "Iniciar Quiz").
**Qué corregí a mano:** Elevé los contrastes de los colores primarios Terracota y Jade para cumplir con estándares WCAG AA en modo oscuro y claro.
**Evidencia:** `evidencias/E3-celular.png` y `evidencias/E3-vacio.png`
**Commit:** `d34c6b4`

---

## M4 · Robustez (Que no se rompa)
**Prompt textual:**
```
Actuá como tester de software, no como programador.

Dame diez formas concretas de romper esta app desde la interfaz: campos vacíos, texto larguísimo en el nombre de la palabra, búsqueda con símbolos raros, saltar preguntas del quiz sin responder, doble clic en guardar, pérdida de conexión durante la consulta de IA.

Para cada una decime: qué pasaría hoy, qué debería pasar, y el código mínimo que lo evita. No cambies el diseño ni agregues funciones nuevas.
```
**Qué devolvió:** Matriz de 10 casos de prueba y validaciones en ViewModel y Composables.
**Qué acepté:** Deshabilitar los botones de selección en el quiz una vez confirmada la respuesta para evitar doble conteo de puntos, y validación de campos obligatorios en el diálogo de añadir palabra.
**Qué corregí a mano:** Implementé el `BackHandler` en las pantallas secundarias para evitar que el usuario cierre la app accidentalmente si presiona el botón "Atrás" de Android mientras está a mitad de un quiz o en una pestaña secundaria.
**Evidencia:** `evidencias/E4-error.png`
**Commit:** `e45b5c5`

---

## M5 · Inteligencia (Que piense)
**Prompt textual:**
```
Integrá una llamada a la API de Gemini dentro de la app para esta tarea concreta:
SELLO DE IA: La IA propone ejemplos de uso morfológico en Náhuat Pipil de El Salvador y el estudiante valida cada uno contra material documentado antes de publicarlo en el diccionario.

Requisitos:
1. La respuesta debe venir como JSON con un esquema fijo (palabra, oración en náhuat, guía fonética, traducción al español, desglose morfológico, regla sugerida, consejo de verificación).
2. La app consume ese JSON y lo muestra en pantalla como dato en tarjetas estructuradas, no como párrafo libre.
3. La llave de API se lee de una variable de entorno BuildConfig.GEMINI_API_KEY.
4. Manejo de fallo: qué se muestra si la IA no responde, responde lento o si no hay internet (modo de regla gramatical documentada sin conexión).
5. Interfaz de validación humana: el estudiante debe poder revisar, corregir y obligatoriamente indicar la fuente documentada de contraste antes de guardar.
```
**Qué devolvió:** Servicio `GeminiApiService`, clases de datos `GeminiModels`, `NahuatExampleSuggestion`, generador fuera de línea `OfflineNahuatRuleGenerator` y pantalla `AiValidatorScreen`.
**Qué acepté:** La arquitectura estructurada con salida JSON obligatoria y el conmutador a reglas documentadas en caso de fallo de red.
**Qué corregí a mano:** Creé botones de acceso rápido para fuentes prestigiosas (Alan R. King, IRIN, Santo Domingo) para facilitar que el estudiante cite la fuente de rigor sin frustración.
**Evidencia:** `evidencias/E5-json.png`, `evidencias/E5-app.png` y `evidencias/E5-falla.png`
**Commit:** `f56a4d6`

---

## Cierre
- **Prompts que escribí en total:** 6 prompts principales y 3 afinamientos de estilo.
- **El prompt que más me sirvió y por qué:** El prompt M5 (Sello de IA) con salida estructurada en JSON y el flujo *Human-in-the-Loop*, porque transformó la IA de un simple generador de texto a una herramienta pedagógica que enseña al estudiante a dudar, verificar y contrastar contra fuentes científicas.
- **El error más caro que cometí:** Al principio no contemplé que la IA confunde el náhuatl mexicano central con el náhuat pipil salvadoreño. Tuve que incorporar reglas explícitas de dialecto cuscatleco en el prompt del sistema y en la tarjeta anti-alucinación.
- **Lo que haría distinto la próxima vez:** Integrar la validación fonética y los modelos de persistencia desde el primer minuto en P0 para no tener que migrar datos entre versiones intermedias.
