package com.example.dohit.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dohit.data.Task
import com.example.dohit.databinding.ItemTaskBinding

class TaskAdapter(private var tasks: List<Task>, private val onTaskClick: (Task) -> Unit) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.taskTitle.text = task.title
            binding.taskDescription.text = task.description

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

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        Log.d("TaskAdapter", "Updating tasks: ${newTasks.size}")
        tasks = newTasks
        notifyDataSetChanged()
    }
}
