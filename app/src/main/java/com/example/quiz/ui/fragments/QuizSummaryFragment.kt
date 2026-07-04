package com.example.quiz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.quiz.R
import com.example.quiz.databinding.FragmentQuizSummaryBinding
import com.example.quiz.ui.viewmodels.QuizViewModel

class QuizSummaryFragment : Fragment() {

    private var _binding: FragmentQuizSummaryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val score = viewModel.score.value ?: 0
        val total = viewModel.questions.value?.size ?: 0
        
        // საბოლოო ქულის ჩვენება
        binding.textViewFinalScore.text = getString(R.string.score_format, score, total)

        // პასუხების რევიზიის ეკრანზე გადასვლა
        binding.buttonReviewAnswers.setOnClickListener {
            findNavController().navigate(
                QuizSummaryFragmentDirections.actionQuizSummaryFragmentToReviewAnswersFragment()
            )
        }

        binding.buttonBackToCategories.setOnClickListener {
            // ქვიზის სტეიტის გასუფთავება, რომ ახალი ქვიზი სუფთა ფურცლიდან დაიწყოს
            viewModel.clearQuizState()
            findNavController().navigate(
                QuizSummaryFragmentDirections.actionQuizSummaryFragmentToCategoriesFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}