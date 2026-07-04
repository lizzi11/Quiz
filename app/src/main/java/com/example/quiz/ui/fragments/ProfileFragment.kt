package com.example.quiz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.quiz.R
import com.example.quiz.databinding.FragmentProfileBinding
import com.example.quiz.ui.viewmodels.AuthViewModel
import com.example.quiz.ui.viewmodels.QuizViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    //activityViewModels-სესიის შესანარჩუნებლად
    private val authViewModel: AuthViewModel by activityViewModels()
    private val quizViewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // მომხმარებლის ინფორმაციის გამოჩენა
        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.textViewUserName.text = user.username
                binding.textViewUserEmail.text = user.email
                // ვტვირთავთ ამ მომხმარებლის შედეგებს სტატისტიკისთვის
                quizViewModel.loadUserResults(user.id)
            }
        }

        // სტატისტიკის დათვლა და გამოჩენა მომხმარებლის შედეგების მიხედვით
        quizViewModel.userResults.observe(viewLifecycleOwner) { results ->
            val totalQuizzes = results.size
            val avgScore = if (results.isNotEmpty()) {
                // საშუალო პროცენტული ქულის გამოთვლა
                results.sumOf { (it.score.toDouble() / it.totalQuestions) * 100 } / results.size
            } else 0.0
            
            binding.textViewTotalQuizzes.text = totalQuizzes.toString()
            binding.textViewAverageScore.text = "${String.format("%.1f", avgScore)}%"
        }

        // გასვლის (Logout) ღილაკის ლოგიკა
        binding.buttonLogout.setOnClickListener {
            authViewModel.logout()
            // გადასვლა ავტორიზაციაზე
            findNavController().navigate(R.id.action_global_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
