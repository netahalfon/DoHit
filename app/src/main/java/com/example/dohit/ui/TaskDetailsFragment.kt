package com.example.dohit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.dohit.R
import com.example.dohit.databinding.FragmentTaskDetailsBinding
import com.example.dohit.viewmodel.TaskViewModel

class TaskDetailsFragment : Fragment() {

    private var _binding: FragmentTaskDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: TaskDetailsFragmentArgs by navArgs()
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load task details using the task ID
        val taskId = args.taskId
        taskViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            val task = tasks.find { it.id == taskId }
            task?.let {
                binding.taskTitleDetails.text = it.title
                binding.taskDescriptionDetails.text = it.description
                binding.taskDueDateDetails.text = getString(R.string.due_date, it.lastModifiedDate)
            }
        }

        // Edit task button
        binding.editTaskButton.setOnClickListener {
            val action = TaskDetailsFragmentDirections.actionTaskDetailsFragmentToAddEditTaskFragment(args.taskId)
            findNavController().navigate(action)
        }

        // Delete task button
        binding.deleteTaskButton.setOnClickListener {
            val task = taskViewModel.allTasks.value?.find { it.id == args.taskId }
            task?.let {
                taskViewModel.deleteTask(it)
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
