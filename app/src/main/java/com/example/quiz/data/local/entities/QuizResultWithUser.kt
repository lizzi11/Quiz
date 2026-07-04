package com.example.quiz.data.local.entities

data class QuizResultWithUser(
    val id: Int,
    val username: String,
    val categoryName: String,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long
)
