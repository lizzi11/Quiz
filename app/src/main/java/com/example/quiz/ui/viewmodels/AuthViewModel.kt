package com.example.quiz.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quiz.data.local.AppDatabase
import com.example.quiz.data.local.entities.User
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val quizDao = AppDatabase.getDatabase(application).quizDao()
    private val sharedPrefs = application.getSharedPreferences("eduquiz_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _loginStatus = MutableLiveData<Boolean?>()
    val loginStatus: LiveData<Boolean?> = _loginStatus

    private val _registrationSuccess = MutableLiveData<Boolean?>()
    val registrationSuccess: LiveData<Boolean?> = _registrationSuccess

    init {
        val userId = sharedPrefs.getInt("user_id", -1)
        if (userId != -1) {
            loadUser(userId)
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = User(username = username, email = email, password = password)
                val id = quizDao.insertUser(user)
                saveUserSession(id.toInt())
                loadUser(id.toInt())
                _registrationSuccess.value = true
            } catch (e: Exception) {
                _registrationSuccess.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = quizDao.login(email, password)
            if (user != null) {
                saveUserSession(user.id)
                _currentUser.value = user
                _loginStatus.value = true
            } else {
                _loginStatus.value = false
            }
        }
    }

    private fun loadUser(userId: Int) {
        viewModelScope.launch {
            _currentUser.value = quizDao.getUserById(userId)
        }
    }

    private fun saveUserSession(userId: Int) {
        sharedPrefs.edit().putInt("user_id", userId).apply()
    }

    fun logout() {
        sharedPrefs.edit().remove("user_id").apply()
        _currentUser.value = null
        _loginStatus.value = null
        _registrationSuccess.value = null
    }

    fun resetStatus() {
        _loginStatus.value = null
        _registrationSuccess.value = null
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPrefs.getInt("user_id", -1) != -1
    }
}
