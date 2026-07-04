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
import com.example.quiz.databinding.FragmentRegisterBinding
import com.example.quiz.ui.viewmodels.AuthViewModel
import com.example.quiz.ui.viewmodels.QuizViewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by activityViewModels()
    private val quizViewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // რეგისტრაციის ღილაკზე დაჭერის დამუშავება
        binding.buttonRegister.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            // ველების შემოწმება
            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (!email.contains("@")) {
                    Toast.makeText(context, "გთხოვთ შეიყვანოთ ვალიდური მეილი (@-ით)", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
               
                viewModel.register(username, email, password)
            } else {
                Toast.makeText(context, "გთხოვთ შეავსოთ ყველა ველი", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.registrationSuccess.observe(viewLifecycleOwner) { success ->
            if (success == null) return@observe

            when (success) {
                true -> {
                    quizViewModel.clearQuizState()
                    findNavController().navigate(R.id.action_registerFragment_to_categoriesFragment)
                }
                false -> {
                    Toast.makeText(context, "რეგისტრაცია ვერ მოხერხდა", Toast.LENGTH_SHORT).show()
                }
            }
            viewModel.resetStatus()
        }

        binding.textViewGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
