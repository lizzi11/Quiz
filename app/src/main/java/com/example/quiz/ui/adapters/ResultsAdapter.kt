package com.example.quiz.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.data.local.entities.QuizResultWithUser
import com.example.quiz.databinding.ItemResultBinding
import java.text.SimpleDateFormat
import java.util.*

class ResultsAdapter : RecyclerView.Adapter<ResultsAdapter.ResultViewHolder>() {

    private var results = emptyList<QuizResultWithUser>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    fun setResults(results: List<QuizResultWithUser>) {
        this.results = results
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(results[position], position)
    }

    override fun getItemCount() = results.size

    inner class ResultViewHolder(private val binding: ItemResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(result: QuizResultWithUser, position: Int) {
            val rank = position + 1
            // ლიდერბორდზე გამოვაჩინოთ: "ადგილი. მომხმარებელი (კატეგორია)"
            binding.textViewResultCategory.text = "$rank. ${result.username} (${result.categoryName})"
            binding.textViewScore.text = "${result.score}/${result.totalQuestions}"
            binding.textViewDate.text = dateFormat.format(Date(result.timestamp))
            
            // ტოპ 3-ისთვის გამოვიყენოთ ოქროსფერი/ბრაუნის აქცენტები
            if (rank <= 3) {
                binding.textViewScore.setTextColor(android.graphics.Color.parseColor("#795548")) // Brown
                binding.textViewScore.textSize = 22f
            } else {
                binding.textViewScore.setTextColor(android.graphics.Color.parseColor("#757575")) // Grey
                binding.textViewScore.textSize = 20f
            }
        }
    }
}
