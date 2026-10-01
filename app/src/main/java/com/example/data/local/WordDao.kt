package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.QuizResult
import com.example.data.model.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words ORDER BY nahuat ASC")
    fun getAllWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE nahuat LIKE '%' || :query || '%' OR spanish LIKE '%' || :query || '%' ORDER BY nahuat ASC")
    fun searchWords(query: String): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE category = :category ORDER BY nahuat ASC")
    fun getWordsByCategory(category: String): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE isFavorite = 1 ORDER BY nahuat ASC")
    fun getFavoriteWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE id = :id LIMIT 1")
    suspend fun getWordById(id: Int): Word?

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomWords(limit: Int): List<Word>

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<Word>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word): Long

    @Update
    suspend fun updateWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)

    @Query("UPDATE words SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFav: Boolean)

    // Quiz Results
    @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
    fun getAllQuizResults(): Flow<List<QuizResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizResult(result: QuizResult): Long

    @Query("SELECT COUNT(*) FROM quiz_results")
    suspend fun getQuizCount(): Int

    @Query("DELETE FROM quiz_results")
    suspend fun clearQuizHistory()
}
