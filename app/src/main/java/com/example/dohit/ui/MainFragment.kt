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
import com.example.dohit.R
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
                Log.d("TaskAdapter", "Task clicked: ${task.title}")
                val bundle = Bundle().apply {
                    putInt("taskId",task.id)
                }
                findNavController().navigate(R.id.action_mainFragment_to_taskDetailsFragment,bundle)
            },
            onTaskStatusChanged = { taskId, isCompleted ->
                taskViewModel.updateTaskStatus(taskId, isCompleted) // עדכון סטטוס ב-ViewModel
            }
        )

        binding.taskRecyclerView.adapter = adapter
        binding.taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        // עדכון הרשימה כאשר יש שינויים
        taskViewModel.incompleteTasks.observe(viewLifecycleOwner) { tasks ->
            adapter.updateTasks(tasks)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
