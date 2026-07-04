package com.example.quiz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.quiz.R
import com.example.quiz.databinding.FragmentQuizBinding
import com.example.quiz.ui.viewmodels.QuizViewModel
import com.example.quiz.ui.viewmodels.AuthViewModel

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val args: QuizFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ვტვირთავთ კითხვებს მხოლოდ პირველად შესვლისას
        if (savedInstanceState == null) {
            authViewModel.currentUser.value?.let { user ->
                viewModel.loadQuestions(args.categoryId, args.categoryName, user.id)
            }
        }

        // კითხვების სიის ცვლილებაზე რეაგირება (ეკრანის პირველი განახლება)
        viewModel.questions.observe(viewLifecycleOwner) { questions ->
            if (questions.isNotEmpty()) {
                updateUI()
            }
        }

        // კითხვის ინდექსის ცვლილებაზე რეაგირება (შემდეგ კითხვაზე გადასვლა)
        viewModel.currentQuestionIndex.observe(viewLifecycleOwner) {
            updateUI()
        }

        viewModel.shuffledOptions.observe(viewLifecycleOwner) { options ->
            if (options.size >= 4) {
                binding.buttonOptionA.text = options[0]
                binding.buttonOptionB.text = options[1]
                binding.buttonOptionC.text = options[2]
                binding.buttonOptionD.text = options[3]
            }
        }

        //ტაიმერი
        viewModel.timeLeft.observe(viewLifecycleOwner) { time ->
            binding.textViewTimer.text = time.toString()
            binding.progressTimer.progress = time
            binding.progressTimer.max = 30 // მაქსიმალური დრო 30 წამი
        }

        viewModel.navigateToSummary.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                viewModel.consumeNavigation()
                val action = QuizFragmentDirections.actionQuizFragmentToQuizSummaryFragment()
                findNavController().navigate(action)
            }
        }

        // პასუხის ღილაკებზე დაჭერის დამუშავება (ვაწვდით მხოლოდ ინდექსს 0-3)
        binding.buttonOptionA.setOnClickListener { viewModel.submitAnswer(0) }
        binding.buttonOptionB.setOnClickListener { viewModel.submitAnswer(1) }
        binding.buttonOptionC.setOnClickListener { viewModel.submitAnswer(2) }
        binding.buttonOptionD.setOnClickListener { viewModel.submitAnswer(3) }
    }

    private fun updateUI() {
        val questions = viewModel.questions.value ?: return
        val currentIndex = viewModel.currentQuestionIndex.value ?: 0
        
        if (currentIndex < questions.size) {
            val question = questions[currentIndex]
            // კითხვების რაოდენობის და მიმდინარე ინდექსის ჩვენება
            binding.textViewQuestionCount.text = getString(R.string.question_count_format, currentIndex + 1, questions.size)
            binding.textViewQuestion.text = question.questionText
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
