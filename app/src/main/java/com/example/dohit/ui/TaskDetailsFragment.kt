package com.example.dohit.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.dohit.R
import com.example.dohit.databinding.FragmentTaskDetailsBinding
import com.example.dohit.viewmodel.TaskViewModel
import java.io.File
import com.example.dohit.utils.setupButtonWithAnimation
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
        val backButton = view.findViewById<ImageButton>(R.id.backButton)

        setupButtonWithAnimation(backButton) {
            requireActivity().onBackPressed() // חזרה למסך הקודם
        }

        // Load task details using the task ID
        val taskId = args.taskId
        taskViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            val task = tasks.find { it.id == taskId }
            task?.let {
                binding.taskTitleDetails.text = it.title
                binding.taskDescriptionDetails.text = it.description
                binding.taskDueDateDetails.text = getString(R.string.due_date, it.lastModifiedDate)
                binding.taskCategoryDetails.text = getString(R.string.category, task.category.getLocalizedDisplayName())
                binding.taskStartTimeDetails.text = getString(R.string.start_time, task.startTime ?: "N/A")
                binding.taskEndTimeDetails.text = getString(R.string.end_time, task.endTime ?: "N/A")





                // הצגת תמונה במידת הצורך
                if (!it.imageUri.isNullOrEmpty()) {
                    val file = File(it.imageUri)
                    if (file.exists()) {
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        binding.taskImageDetails.setImageBitmap(bitmap)
                        binding.taskImageDetails.visibility = View.VISIBLE
                    } else {
                        binding.taskImageDetails.visibility = View.GONE
                        Toast.makeText(requireContext(), R.string.error_image_not_found, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.taskImageDetails.visibility = View.GONE
                }



            }
        }

        // Edit task button
        setupButtonWithAnimation(binding.editTaskButton) {
            val action = TaskDetailsFragmentDirections.actionTaskDetailsFragmentToAddEditTaskFragment(args.taskId)
            findNavController().navigate(action)
        }

        setupButtonWithAnimation(binding.deleteTaskButton) {
            val task = taskViewModel.allTasks.value?.find { it.id == args.taskId }
            task?.let {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.delete_task))
                    .setMessage(getString(R.string.delete_confirmation))
                    .setPositiveButton(getString(R.string.delete)) { _, _ ->
                        taskViewModel.deleteTask(it)
                        findNavController().popBackStack()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
