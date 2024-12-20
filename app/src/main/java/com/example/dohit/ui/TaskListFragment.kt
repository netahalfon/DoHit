package com.example.dohit.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dohit.databinding.FragmentTaskListBinding
import com.example.dohit.viewmodel.TaskViewModel

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)

        // הגדרת RecyclerView
        taskAdapter = TaskAdapter(emptyList()) { task ->
            val action = TaskListFragmentDirections.actionTaskListFragmentToTaskDetailsFragment(task.id)
            findNavController().navigate(action)
        }
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }

        // קבלת שם התיקייה מהארגומנט
        val folderName = arguments?.let {
            TaskListFragmentArgs.fromBundle(it).folderName
        }
        Log.d("TaskListFragment", "FolderName received: $folderName")

        // טעינת משימות לפי התיקייה
        folderName?.let {
            Log.d("TaskListFragment", "Loading tasks for folder: $it")
            viewModel.getTasksByCategory(it).observe(viewLifecycleOwner) { tasks ->
                Log.d("TaskListFragment", "Tasks loaded for $it: ${tasks.size}")
                tasks.forEach { task ->
                    Log.d("TaskListFragment", "Task: ${task.title}, Folder: ${task.category}")
                }
                taskAdapter.updateTasks(tasks)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
