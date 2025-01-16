package com.example.dohit.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dohit.R
import com.example.dohit.adapter.TaskAdapter
import com.example.dohit.data.task.TaskCategory
import com.example.dohit.databinding.FragmentTaskListBinding
import com.example.dohit.utils.setupButtonWithAnimation
import com.example.dohit.viewmodel.TaskViewModel
import java.util.Locale

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskAdapter: TaskAdapter
    private val taskViewModel: TaskViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // הגדרת כפתור חזור
        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        setupButtonWithAnimation(backButton) {
            requireActivity().onBackPressed() // חזרה למסך הקודם
        }

        // קבלת שם הקטגוריה מתוך ה-arguments
        val folderName = arguments?.let {
            TaskListFragmentArgs.fromBundle(it).folderName
        }

        // המרת שם הקטגוריה לשפה המקומית
        val category = TaskCategory.entries.find { it.displayName == folderName }
        val localizedCategoryName = category?.getLocalizedDisplayName(Locale.getDefault().language)
            ?: if (Locale.getDefault().language == "he") {
                getString(R.string.unknown_category_he)
            } else {
                getString(R.string.unknown_category)
            }

        // הצגת שם הקטגוריה בכותרת
        binding.taskCategoryTitle.text = localizedCategoryName



        // הגדרת RecyclerView
        taskAdapter = TaskAdapter(
            tasks = emptyList(),
            onTaskClick = { task ->
                val action = TaskListFragmentDirections.actionTaskListFragmentToTaskDetailsFragment(task.id)
                findNavController().navigate(action)
            },
            onTaskStatusChanged = { taskId, isCompleted ->
                taskViewModel.updateTaskStatus(taskId, isCompleted)
            }
        )
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }

        // טעינת משימות לפי הקטגוריה
        if (category != null) {
            taskViewModel.getTasksByCategory(category.displayName).observe(viewLifecycleOwner) { tasks ->
                taskAdapter.updateTasks(tasks)
            }
        } else {
            Log.e("TaskListFragment", "Category not found for folderName: $folderName")
        }
        

        // טעינת משימות לפי הקטגוריה
        taskViewModel.allTasks.observe(viewLifecycleOwner) { allTasks ->
            if (category != null) {
                val filteredTasks = allTasks.filter {  task ->
                    task.category.displayName == category?.displayName}
                taskAdapter.updateTasks(filteredTasks)
            } else {
                Log.e("TaskListFragment", "Category not found for folderName: $folderName")
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
