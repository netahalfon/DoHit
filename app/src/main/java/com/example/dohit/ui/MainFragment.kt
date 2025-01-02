package com.example.dohit.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dohit.R
import com.example.dohit.data.Task
import com.example.dohit.databinding.FragmentMainBinding
import com.example.dohit.viewmodel.TaskViewModel

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
