package com.example.quiz.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.data.local.entities.Category
import com.example.quiz.databinding.ItemCategoryBinding

class CategoriesAdapter(private val onCategoryClick: (Category) -> Unit) :
    RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    private var categories = emptyList<Category>()

    fun setCategories(categories: List<Category>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.textViewCategoryName.text = category.name
            binding.textViewCategoryDescription.text = category.description
            
            if (category.imageResId != 0) {
                binding.imageViewCategoryIcon.setImageResource(category.imageResId)
                binding.imageViewCategoryIcon.setPadding(0, 0, 0, 0)
                binding.imageViewCategoryIcon.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                // მოვაშოროთ ყავისფერი შეფერილობა (tint), რომ ნამდვილი სურათი გამოჩნდეს
                binding.imageViewCategoryIcon.imageTintList = null
            } else {
                binding.imageViewCategoryIcon.setImageResource(android.R.drawable.ic_menu_help)
                binding.imageViewCategoryIcon.setPadding(12, 12, 12, 12)
                binding.imageViewCategoryIcon.imageTintList = android.content.res.ColorStateList.valueOf(
                    binding.root.context.getColor(com.example.quiz.R.color.primary)
                )
            }

            binding.root.setOnClickListener { onCategoryClick(category) }
        }
    }
}
