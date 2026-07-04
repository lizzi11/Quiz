package com.example.quiz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.quiz.R
import com.example.quiz.databinding.FragmentLoginBinding
import com.example.quiz.ui.viewmodels.AuthViewModel
import com.example.quiz.ui.viewmodels.QuizViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()
    private val quizViewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ყოველთვის გამოვდივართ სისტემიდან, რომ აპლიკაციის ჩართვისას ლოგინი გამოჩნდეს
        viewModel.logout()
        quizViewModel.clearQuizState()

        // შესვლის ღილაკზე დაჭერა
        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(context, "გთხოვთ შეავსოთ ყველა ველი", Toast.LENGTH_SHORT).show()
            }
        }

        // რეგისტრაციაზე გადასვლა
        binding.textViewGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // ავტორიზაციის შედეგზე დაკვირვება
        viewModel.loginStatus.observe(viewLifecycleOwner) { success ->
            if (success == null) return@observe

            when (success) {
                true -> {
                    quizViewModel.clearQuizState()
                    findNavController().navigate(R.id.action_loginFragment_to_categoriesFragment)
                }
                false -> {
                    Toast.makeText(context, "არასწორი მონაცემები", Toast.LENGTH_SHORT).show()
                }
            }
            viewModel.resetStatus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
