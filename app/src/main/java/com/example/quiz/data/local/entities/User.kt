package com.example.quiz.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

import androidx.room.Index

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val password: String
)
