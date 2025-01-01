package com.example.dohit.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dohit.R
import com.example.dohit.data.TaskCategory

class CategoryAdapter(
    private val categories: List<TaskCategory>,
    private val onCategorySelected: (TaskCategory) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedCategory: TaskCategory? = null

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryName.text = category.getLocalizedDisplayName()
        Log.d("CategoryAdapter", "Binding category: ${category.getLocalizedDisplayName()}")

        if (category == selectedCategory) {
            holder.itemView.setBackgroundResource(R.drawable.selected_category_background)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.category_background)
        }

        holder.itemView.setOnClickListener {
            selectedCategory = category
            notifyDataSetChanged()
            onCategorySelected(category)
        }
    }

    override fun getItemCount() = categories.size

    fun getSelectedCategory(): TaskCategory? {
        return selectedCategory
    }

    fun setSelectedCategory(category: TaskCategory?) {
        selectedCategory = category
        notifyDataSetChanged()
    }
}
