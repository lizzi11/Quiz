package com.example.quiz.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.quiz.data.QuizRepository
import com.example.quiz.data.local.AppDatabase
import com.example.quiz.data.local.entities.Category
import com.example.quiz.data.local.entities.Question
import com.example.quiz.data.local.entities.QuizResult
import com.example.quiz.data.local.entities.QuizResultWithUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuizRepository
    val allCategories: LiveData<List<Category>>
    val globalResults: LiveData<List<QuizResultWithUser>>
    
    private val _userResults = MutableLiveData<List<QuizResultWithUser>>()
    val userResults: LiveData<List<QuizResultWithUser>> = _userResults

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions

    private val _currentQuestionIndex = MutableLiveData(0)
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _timeLeft = MutableLiveData(30)
    val timeLeft: LiveData<Int> = _timeLeft

    private val _isQuizFinished = MutableLiveData(false)
    val isQuizFinished: LiveData<Boolean> = _isQuizFinished

    private val _navigateToSummary = MutableLiveData<Boolean>(false)
    val navigateToSummary: LiveData<Boolean> = _navigateToSummary

    fun consumeNavigation() {
        _navigateToSummary.value = false
        _isQuizFinished.value = false
    }

    private val _userAnswers = MutableLiveData<MutableMap<Int, Int>>(mutableMapOf())
    val userAnswers: LiveData<MutableMap<Int, Int>> = _userAnswers

    private var timerJob: kotlinx.coroutines.Job? = null
    private var currentUserId: Int = -1
    private var currentCategoryName: String = "Quiz"
    private var _currentCategoryId: Int = -1
    val currentCategoryId: Int get() = _currentCategoryId

    private var isConfigurationChanging = false

    init {
        val quizDao = AppDatabase.getDatabase(application).quizDao()
        repository = QuizRepository(quizDao)
        allCategories = repository.allCategories.asLiveData()
        globalResults = repository.getGlobalTopResults().asLiveData()
        
        viewModelScope.launch(Dispatchers.IO) {
            repository.prepopulateData()
        }
    }

    fun loadUserResults(userId: Int) {
        viewModelScope.launch {
            repository.getResultsForUser(userId).collect {
                _userResults.value = it
            }
        }
    }

    private val _shuffledOptions = MutableLiveData<List<String>>()
    val shuffledOptions: LiveData<List<String>> = _shuffledOptions

    private var correctOptionText: String = ""

    fun setCurrentUserId(userId: Int) {
        currentUserId = userId
        android.util.Log.d("QuizViewModel", "User ID updated to: $userId")
    }

    fun onNavigatedToSummary() {
        _isQuizFinished.value = false
    }

    fun consumeQuizFinished() {
        _isQuizFinished.value = false
    }

    // ქვიზის კითხვების ჩატვირთვა კატეგორიის მიხედვით
    fun loadQuestions(categoryId: Int, categoryName: String, userId: Int) {
        // თუ ეკრანი უბრალოდ შემოტრიალდა ქვიზის დროს, პროგრესს არ ვშლით
        if (_currentCategoryId == categoryId && _questions.value?.isNotEmpty() == true && _navigateToSummary.value == false && _isQuizFinished.value == false) {
            return
        }

        resetQuizState() // ძველი მონაცემების გასუფთავება
        
        _currentCategoryId = categoryId
        currentUserId = userId
        currentCategoryName = categoryName
        
        viewModelScope.launch {
            val fetchedQuestions = repository.getQuestionsByCategory(categoryId).shuffled()
            if (fetchedQuestions.isNotEmpty()) {
                _questions.value = fetchedQuestions
                _currentQuestionIndex.value = 0
                _score.value = 0
                _isQuizFinished.value = false
                _navigateToSummary.value = false
                
                prepareCurrentQuestion() // პირველი კითხვის მომზადება
                startTimer() // ტაიმერის ჩართვა
            }
        }
    }

    fun clearQuizState() {
        _isQuizFinished.value = false
        _navigateToSummary.value = false
        _score.value = 0
        _questions.value = emptyList()
        _currentQuestionIndex.value = 0
        _userAnswers.value = mutableMapOf()
        _currentCategoryId = -1
        timerJob?.cancel()
    }

    private fun resetQuizState() {
        _questions.value = emptyList()
        _currentQuestionIndex.value = 0
        _score.value = 0
        _timeLeft.value = 30
        _isQuizFinished.value = false
        _navigateToSummary.value = false
        _userAnswers.value = mutableMapOf()
        _shuffledOptions.value = emptyList()
        timerJob?.cancel()
    }

    private fun prepareCurrentQuestion() {
        val questions = _questions.value ?: return
        val index = _currentQuestionIndex.value ?: return
        if (index < questions.size) {
            val question = questions[index]
            val options = listOf(question.optionA, question.optionB, question.optionC, question.optionD)
            correctOptionText = when(question.correctAnswer) {
                0 -> question.optionA
                1 -> question.optionB
                2 -> question.optionC
                else -> question.optionD
            }
            _shuffledOptions.value = options.shuffled()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        _timeLeft.value = 30
        timerJob = viewModelScope.launch {
            while ((_timeLeft.value ?: 0) > 0) {
                delay(1000)
                _timeLeft.value = (_timeLeft.value ?: 0) - 1
            }
            goToNextQuestion()
        }
    }

    // მომხმარებლის მიერ პასუხის არჩევა
    fun submitAnswer(selectedOptionIndex: Int) {
        val options = _shuffledOptions.value ?: return
        if (selectedOptionIndex !in options.indices) return
        
        val selectedText = options[selectedOptionIndex]
        

        val currentQuestion = _questions.value?.get(_currentQuestionIndex.value ?: 0) ?: return
        val originalOptions = listOf(currentQuestion.optionA, currentQuestion.optionB, currentQuestion.optionC, currentQuestion.optionD)
        val originalIndex = originalOptions.indexOf(selectedText)
        
        // ქულის მომატება თუ პასუხი სწორია
        if (selectedText == correctOptionText) {
            _score.value = (_score.value ?: 0) + 1
        }
        
        // ვინახავთ პასუხს
        val currentAnswers = _userAnswers.value ?: mutableMapOf()
        currentAnswers[_currentQuestionIndex.value!!] = originalIndex
        _userAnswers.value = currentAnswers
        
        goToNextQuestion()
    }

    private fun goToNextQuestion() {
        timerJob?.cancel()
        
        // თუ ამ კითხვაზე პასუხი ჯერ არ არის შენახული დრო გავიდა ჩავწეროთ -1
        val currentIndex = _currentQuestionIndex.value ?: 0
        val currentAnswers = _userAnswers.value ?: mutableMapOf()
        if (!currentAnswers.containsKey(currentIndex)) {
            currentAnswers[currentIndex] = -1
            _userAnswers.value = currentAnswers
        }

        val nextIndex = currentIndex + 1
        if (nextIndex < (questions.value?.size ?: 0)) {
            _currentQuestionIndex.value = nextIndex
            prepareCurrentQuestion()
            startTimer()
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        timerJob?.cancel()
        _isQuizFinished.value = true
        _navigateToSummary.value = true
        
        val scoreToSave = _score.value ?: 0
        val totalQuestions = _questions.value?.size ?: 0
        val userId = currentUserId
        val category = currentCategoryName

        android.util.Log.d("QuizFlow", "Finishing Quiz: User=$userId, Score=$scoreToSave/$totalQuestions, Category=$category")
        
        if (userId == -1) {
            android.util.Log.e("QuizFlow", "Cannot save result: User ID is -1")
            return
        }
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = QuizResult(
                    userId = userId,
                    categoryName = category,
                    score = scoreToSave,
                    totalQuestions = totalQuestions
                )
                repository.insertResult(result)
                android.util.Log.d("QuizFlow", "Result saved successfully for User $userId")
            } catch (e: Exception) {
                android.util.Log.e("QuizFlow", "Error saving result: ${e.message}")
            }
        }
    }
}
