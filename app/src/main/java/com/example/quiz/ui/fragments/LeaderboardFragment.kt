package com.example.quiz.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.quiz.databinding.FragmentLeaderboardBinding
import com.example.quiz.ui.adapters.ResultsAdapter
import com.example.quiz.ui.viewmodels.QuizViewModel
import com.example.quiz.ui.viewmodels.AuthViewModel

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    
    // გამოყენებული უნდა იყოს activityViewModels, რათა მივწვდეთ ავტორიზებულ იუზერს
    private val viewModel: QuizViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val adapter = ResultsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // ადაპტერის დაკავშირება RecyclerView-სთან
        binding.recyclerViewResults.adapter = adapter
        
        // Swipe to Refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            // Room-ის Flow ავტომატურად ანახლებს მონაცემებს ცვლილებისას.

            binding.swipeRefreshLayout.isRefreshing = false
        }

        //ლიდერბორდის დაკვირვება
        viewModel.globalResults.observe(viewLifecycleOwner) { results ->
            android.util.Log.d("Leaderboard", "შედეგების რაოდენობა: ${results.size}")

            adapter.setResults(results)
            binding.swipeRefreshLayout.isRefreshing = false

            if (results.isEmpty()) {
                binding.textViewEmptyLeaderboard.visibility = View.VISIBLE
                binding.recyclerViewResults.visibility = View.GONE
            } else {
                binding.textViewEmptyLeaderboard.visibility = View.GONE
                binding.recyclerViewResults.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
