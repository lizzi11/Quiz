package com.example.quiz.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.quiz.data.local.dao.QuizDao
import com.example.quiz.data.local.entities.Category
import com.example.quiz.data.local.entities.Question
import com.example.quiz.data.local.entities.QuizResult
import com.example.quiz.data.local.entities.User

@Database(entities = [Category::class, Question::class, QuizResult::class, User::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quiz_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
