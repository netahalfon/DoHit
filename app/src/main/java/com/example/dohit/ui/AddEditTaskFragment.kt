package com.example.dohit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dohit.data.Task
import com.example.dohit.databinding.FragmentAddEditTaskBinding
import com.example.dohit.viewmodel.TaskViewModel

class AddEditTaskFragment : Fragment() {

    private var _binding: FragmentAddEditTaskBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // קבלת ה-taskId מה-arguments
        val taskId = arguments?.getInt("taskId") ?: -1

        if (taskId != -1) {
            // עריכת משימה קיימת
            taskViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
                val task = tasks.find { it.id == taskId }
                task?.let {
                    binding.taskTitle.setText(it.title)
                    binding.taskDescription.setText(it.description)
                }
            }
        }

        // לחיצה על כפתור שמירה
        binding.saveButton.setOnClickListener {
            val title = binding.taskTitle.text.toString()
            val description = binding.taskDescription.text.toString()

            // יצירת אובייקט המשימה
            val task = Task(
                id = if (taskId != -1) taskId else 0, // אם זה עריכה, שמור על ה-ID, אחרת משימה חדשה
                title = title,
                description = description,
                dueDate = "2024-12-31", // תאריך דוגמה
                priority = 1 // עדיפות דוגמה
            )

            // שמירה במסד הנתונים
            taskViewModel.insertTask(task)

            // חזרה למסך הקודם
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
