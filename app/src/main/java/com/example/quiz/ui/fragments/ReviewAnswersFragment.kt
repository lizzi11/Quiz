package com.example.quiz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.databinding.FragmentReviewAnswersBinding
import com.example.quiz.databinding.ItemReviewAnswerBinding
import com.example.quiz.data.local.entities.Question
import com.example.quiz.ui.viewmodels.QuizViewModel

class ReviewAnswersFragment : Fragment() {

    private val viewModel: QuizViewModel by activityViewModels()
    private var _binding: FragmentReviewAnswersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewAnswersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewReview.layoutManager = LinearLayoutManager(context)
        
        val questions = viewModel.questions.value ?: emptyList()
        val userAnswers = viewModel.userAnswers.value ?: emptyMap()
        
        
        binding.recyclerViewReview.adapter = ReviewAdapter(questions, userAnswers)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ReviewAdapter(
        private val questions: List<Question>,
        private val userAnswers: Map<Int, Int>
    ) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

        class ViewHolder(val binding: ItemReviewAnswerBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemReviewAnswerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val question = questions[position]
            val userAnswerIndex = userAnswers[position] ?: -1
            val options = listOf(question.optionA, question.optionB, question.optionC, question.optionD)

            with(holder.binding) {
                val context = root.context
                
                textViewReviewQuestion.text = question.questionText
                
                textViewYourAnswer.text = if (userAnswerIndex != -1) {
                    context.getString(R.string.your_answer_format, options[userAnswerIndex])
                } else {
                    context.getString(R.string.no_answer)
                }
                
                textViewCorrectAnswer.text = context.getString(R.string.correct_answer_format, options[question.correctAnswer])

                val colorRes = if (userAnswerIndex == question.correctAnswer) R.color.correct_green else R.color.wrong_red
                textViewYourAnswer.setTextColor(context.getColor(colorRes))
            }
        }

        override fun getItemCount() = questions.size
    }
}
