package com.example.dohit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dohit.R
import com.example.dohit.data.task.Task
import com.example.dohit.data.task.TaskCategory
import com.example.dohit.databinding.ItemTaskBinding

class TaskAdapter(
    private var tasks: List<Task>,
    private val onTaskClick: (Task) -> Unit,
    private val onTaskStatusChanged: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.taskTitle.text = task.title
            binding.taskDescription.text = task.description

            // Clear previous listener before setting a new state
            binding.checkboxStatus.setOnCheckedChangeListener(null)

            // Update checkbox state programmatically
            binding.checkboxStatus.isChecked = task.isCompleted

            // קביעת התמונה של הקטגוריה
            val categoryImageRes = when (task.category) {
                TaskCategory.Work -> R.drawable.boss
                TaskCategory.Sport -> R.drawable.cycling
                TaskCategory.Hobbies -> R.drawable.paint_palette
                TaskCategory.Education -> R.drawable.mortarboard
                TaskCategory.Urgent -> R.drawable.warning
                TaskCategory.Money -> R.drawable.money_bag
            }
            binding.categoryImage.setImageResource(categoryImageRes)

            // Handle checkbox click
            binding.checkboxStatus.setOnCheckedChangeListener { _, isChecked ->
                onTaskStatusChanged(task.id, isChecked)
            }

            val layoutParams = binding.root.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(16, 16, 16, 16) // מרווחים: שמאל, עליון, ימין, תחתון
            binding.root.layoutParams = layoutParams

            binding.root.setOnClickListener {
                onTaskClick(task)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding =
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }
    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    override fun getItemCount() = tasks.size
}
