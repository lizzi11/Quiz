package com.example.quiz.data

import com.example.quiz.data.local.dao.QuizDao
import com.example.quiz.data.local.entities.Category
import com.example.quiz.data.local.entities.Question
import com.example.quiz.data.local.entities.QuizResult
import com.example.quiz.data.local.entities.QuizResultWithUser
import kotlinx.coroutines.flow.Flow

class QuizRepository(private val quizDao: QuizDao) {

    val allCategories: Flow<List<Category>> = quizDao.getAllCategories()

    fun getResultsForUser(userId: Int): Flow<List<QuizResultWithUser>> {
        return quizDao.getResultsForUser(userId)
    }

    fun getGlobalTopResults(): Flow<List<QuizResultWithUser>> {
        return quizDao.getGlobalTopResults()
    }

    suspend fun getQuestionsByCategory(categoryId: Int): List<Question> {
        return quizDao.getQuestionsByCategory(categoryId)
    }

    suspend fun insertResult(result: QuizResult) {
        quizDao.insertResult(result)
    }

    suspend fun clearAllResults() {
        quizDao.deleteAllResults()
    }

    suspend fun prepopulateData() {
        // შევამოწმოთ კატეგორიები
        val existingCategories = quizDao.getAllCategoriesOnce()
        if (existingCategories.isEmpty()) {
            val categories = listOf(
                Category(id = 1, name = "Programming", description = "C#, Android & Architecture", imageResId = com.example.quiz.R.drawable.it),
                Category(id = 2, name = "History", description = "Georgian History & Key Events", imageResId = com.example.quiz.R.drawable.history),
                Category(id = 3, name = "Geography", description = "World & Georgian Geography", imageResId = com.example.quiz.R.drawable.geography)
            )
            quizDao.insertCategories(categories)
        }

        val questions = listOf(
            //Programming
            Question(id = 1, categoryId = 1, questionText = "რომელი საკვანძო სიტყვა გამოიყენება C#-ში კლასის მემკვიდრეობისთვის (Inheritance)?", 
                optionA = "extends", optionB = ": (ორწერტილი)", optionC = "implements", optionD = "inherit", correctAnswer = 1),
            Question(id = 2, categoryId = 1, questionText = "MVVM არქიტექტურაში, რომელი კომპონენტია პასუხისმგებელი ბიზნეს ლოგიკის მართვაზე?", 
                optionA = "View", optionB = "Model", optionC = "ViewModel", optionD = "Activity", correctAnswer = 2),
            Question(id = 3, categoryId = 1, questionText = "Android-ში, რომელი კომპონენტი გამოიყენება ეკრანებს შორის ნავიგაციისთვის Jetpack-ში?", 
                optionA = "Intent", optionB = "Navigation Component", optionC = "ViewPager2", optionD = "FragmentManager", correctAnswer = 1),
            Question(id = 4, categoryId = 1, questionText = "C#-ში, რა ეწოდება მექანიზმს, რომელიც საშუალებას გვაძლევს მეთოდი გადავცეთ როგორც პარამეტრი?", 
                optionA = "Delegate", optionB = "Interface", optionC = "Property", optionD = "Event", correctAnswer = 0),
            Question(id = 5, categoryId = 1, questionText = "Room მონაცემთა ბაზაში, რა ანოტაცია გამოიყენება თეიბლის (ცხრილის) აღსანიშნავად?", 
                optionA = "@Database", optionB = "@Dao", optionC = "@Table", optionD = "@Entity", correctAnswer = 3),

            //History
            Question(id = 6, categoryId = 2, questionText = "რომელ წელს მოხდა დიდგორის ბრძოლა?",
                optionA = "1121 წელს", optionB = "1125 წელს", optionC = "1221 წელს", optionD = "1089 წელს", correctAnswer = 0),
            Question(id = 7, categoryId = 2, questionText = "ვინ იყო საქართველოს პირველი დემოკრატიული რესპუბლიკის მთავრობის თავმჯდომარე (1918-1921)?", 
                optionA = "ილია ჭავჭავაძე", optionB = "ნოე ჟორდანია", optionC = "აკაკი ჩხენკელი", optionD = "ექვთიმე თაყაიშვილი", correctAnswer = 1),
            Question(id = 8, categoryId = 2, questionText = "რომელ საუკუნეში მოღვაწეობდა მეფე დავით IV აღმაშენებელი?", 
                optionA = "X საუკუნეში", optionB = "XI-XII საუკუნეებში", optionC = "XIII საუკუნეში", optionD = "IX საუკუნეში", correctAnswer = 1),
            Question(id = 9, categoryId = 2, questionText = "ვინ იყო ერთიანი საქართველოს პირველი მეფე?", 
                optionA = "ბაგრატ III", optionB = "ვახტანგ გორგასალი", optionC = "დავით კურაპალატი", optionD = "გიორგი ბრწყინვალე", correctAnswer = 0),
            Question(id = 10, categoryId = 2, questionText = "რომელ წელს მიიღო საქართველომ დამოუკიდებლობის აქტი პირველად მე-20 საუკუნეში?", 
                optionA = "1991 წლის 9 აპრილს", optionB = "1918 წლის 26 მაისს", optionC = "1921 წლის 25 თებერვალს", optionD = "1905 წლის 17 ოქტომბერს", correctAnswer = 1),

            //Geography
            Question(id = 11, categoryId = 3, questionText = "რომელია მსოფლიოში ყველაზე ღრმა ტბა?", 
                optionA = "კასპიის ზღვა", optionB = "ვიქტორია", optionC = "ბაიკალი", optionD = "მიჩიგანი", correctAnswer = 2),
            Question(id = 12, categoryId = 3, questionText = "რომელი მწვერვალია საქართველოსა და კავკასიის უმაღლესი წერტილი?", 
                optionA = "ყაზბეგი", optionB = "შხარა", optionC = "უშბა", optionD = "ჯანღა", correctAnswer = 1),
            Question(id = 13, categoryId = 3, questionText = "რომელი ოკეანეა ფართობით ყველაზე დიდი დედამიწაზე?", 
                optionA = "ატლანტის", optionB = "ინდოეთის", optionC = "ჩრდილოეთ ყინულოვანი", optionD = "წყნარი", correctAnswer = 3),
            Question(id = 14, categoryId = 3, questionText = "რომელი მდინარეა ყველაზე გრძელი საქართველოში?", 
                optionA = "მტკვარი", optionB = "რიონი", optionC = "ალაზანი", optionD = "ენგური", correctAnswer = 0),
            Question(id = 15, categoryId = 3, questionText = "რომელი ქვეყნის დედაქალაქია კიოტო წარსულში და ტოკიო ამჟამად?", 
                optionA = "ჩინეთის", optionB = "სამხრეთ კორეის", optionC = "იაპონიის", optionD = "ვიეტნამის", correctAnswer = 2)
        )
        // insertQuestions იყენებს OnConflictStrategy.REPLACE-ს, ამიტომ ID-ის თანხვედრისას განაახლებს ტექსტს
        quizDao.insertQuestions(questions)
    }
}
