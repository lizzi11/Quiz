package com.example.quiz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.quiz.databinding.FragmentCategoriesBinding
import com.example.quiz.ui.adapters.CategoriesAdapter
import com.example.quiz.ui.viewmodels.QuizViewModel

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    
    // ვიყენებთ activityViewModels-ს, რომ მონაცემები ფრაგმენტებს შორის გაზიარდეს
    private val viewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ბაინდინგის ინიციალიზაცია
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //კატეგორიაზე დაჭერის ლოგიკა
        val adapter = CategoriesAdapter { category ->
            // ქვიზის დაწყებამდე ვასუფთავებთ წინა სესიის სტეიტს
            viewModel.clearQuizState() 
            
            // გადავდივართ ქვიზის ეკრანზე და ვატანთ კატეგორიის მონაცემებს
            val action = CategoriesFragmentDirections.actionCategoriesFragmentToQuizFragment(
                category.id,
                category.name
            )
            findNavController().navigate(action)
        }

        binding.recyclerViewCategories.adapter = adapter

        viewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            adapter.setCategories(categories)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
