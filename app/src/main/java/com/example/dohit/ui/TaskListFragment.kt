package com.example.dohit.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dohit.R
import com.example.dohit.data.TaskCategory
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // הגדרת כפתור חזור
        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // הצגת שם הקטגוריה בכותרת
        val categoryTitle = view.findViewById<TextView>(R.id.taskCategoryTitle)
        val folderName = arguments?.let {
            TaskListFragmentArgs.fromBundle(it).folderName
        }

// המרת שם הקטגוריה לשפה המקומית
        val localizedCategoryName = TaskCategory.entries
            .find { it.displayName == folderName }
            ?.getLocalizedDisplayName()
            ?: ""

// עדכון הכותרת
        categoryTitle.text = localizedCategoryName


        // הגדרת RecyclerView
        taskAdapter = TaskAdapter(emptyList()) { task ->
            val action = TaskListFragmentDirections.actionTaskListFragmentToTaskDetailsFragment(task.id)
            findNavController().navigate(action)
        }
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdapter
        }

        // טעינת משימות לפי הקטגוריה
        if (folderName != null) {
            viewModel.getTasksByCategory(folderName).observe(viewLifecycleOwner) { tasks ->
                taskAdapter.updateTasks(tasks)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
