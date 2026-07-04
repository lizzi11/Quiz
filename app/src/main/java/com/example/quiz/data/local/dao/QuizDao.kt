package com.example.quiz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quiz.data.local.entities.Category
import com.example.quiz.data.local.entities.Question
import com.example.quiz.data.local.entities.QuizResult
import com.example.quiz.data.local.entities.QuizResultWithUser
import com.example.quiz.data.local.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories")
    suspend fun getAllCategoriesOnce(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    @Query("SELECT * FROM questions WHERE categoryId = :categoryId LIMIT 5")
    suspend fun getQuestionsByCategory(categoryId: Int): List<Question>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)

    @Query("""
        SELECT qr.id, u.username, qr.categoryName, qr.score, qr.totalQuestions, qr.timestamp 
        FROM quiz_results qr 
        JOIN users u ON qr.userId = u.id 
        WHERE qr.userId = :userId 
        ORDER BY qr.timestamp DESC
    """)
    fun getResultsForUser(userId: Int): Flow<List<QuizResultWithUser>>

    @Query("""
        SELECT qr.id, u.username, qr.categoryName, qr.score, qr.totalQuestions, qr.timestamp 
        FROM quiz_results qr
        JOIN users u ON qr.userId = u.id
        WHERE qr.id IN (
            SELECT id FROM quiz_results 
            GROUP BY userId, categoryName 
            HAVING score = MAX(score)
        )
        ORDER BY qr.score DESC, qr.timestamp DESC LIMIT 50
    """)
    fun getGlobalTopResults(): Flow<List<QuizResultWithUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: QuizResult)

    @Query("DELETE FROM quiz_results")
    suspend fun deleteAllResults()

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()

    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()

    @Query("SELECT COUNT(*) FROM quiz_results WHERE userId = :userId")
    fun getUserQuizzesCount(userId: Int): Flow<Int>

    @Query("SELECT AVG(CAST(score AS FLOAT) / totalQuestions * 100) FROM quiz_results WHERE userId = :userId")
    fun getAverageScoreForUser(userId: Int): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?
}
