package com.example.dohit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dohit.R
import com.example.dohit.adapter.TaskAdapter
import com.example.dohit.data.Task
import com.example.dohit.databinding.FragmentMainBinding
import com.example.dohit.viewmodel.TaskViewModel
import java.util.Calendar


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by viewModels()

    private lateinit var taskAdapter: TaskAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //update the greeting massage  (nigh/morning...)
        updateGreeting()
        // RecyclerView
        val adapter = TaskAdapter(
            tasks = emptyList(),
            onTaskClick = { task ->
                val bundle = Bundle().apply {
                    putInt("taskId",task.id)
                }
                findNavController().navigate(R.id.action_mainFragment_to_taskDetailsFragment,bundle)
            },
            onTaskStatusChanged = { taskId, isCompleted ->
                taskViewModel.updateTaskStatus(taskId, isCompleted)
            }
        )

        binding.taskRecyclerView.adapter = adapter
        binding.taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        fun updateTasksWithSearchTerm() {
            val query = binding.editTextText.text.toString().trim()
            adapter.updateTasks(filterTasks(query))
        }

        // עדכון הרשימה כאשר יש שינויים
        taskViewModel.incompleteTasks.observe(viewLifecycleOwner) { tasks ->
            updateTasksWithSearchTerm()
        }
        //חיפוש בלחיצה על Enter
        binding.editTextText.setOnEditorActionListener { _, _, _ ->
            updateTasksWithSearchTerm() // קריאה לפונקציית סינון
            true // Indicates the event is handled
        }

    }

    //changing the greeting massage by the time
    private fun updateGreeting() {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            currentHour in 6..11 -> getString(R.string.good_morning) // בוקר טוב
            currentHour in 12..17 -> getString(R.string.good_afternoon) // צהריים טובים
            else -> getString(R.string.good_night) // לילה טוב
        }
        binding.textView2.text = greeting
    }


    //סינון רשימת המשימות בהתאם למחרוזת
    private fun filterTasks(query: String): List<Task> {
        val filteredTasks = taskViewModel.incompleteTasks.value?.filter { task ->
            task.title.contains(query, ignoreCase = true) || task.description.contains(query, ignoreCase = true) || task.category.toString().contains(query, ignoreCase = true)
        }
        return filteredTasks ?: emptyList()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
