package com.example.dohit.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dohit.R
import com.example.dohit.data.Task
import com.example.dohit.data.TaskCategory
import com.example.dohit.databinding.FragmentAddEditTaskBinding
import com.example.dohit.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddEditTaskFragment : Fragment() {

    private var _binding: FragmentAddEditTaskBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: String? = null
    private var taskId: Int =-1

    private val taskViewModel: TaskViewModel by viewModels()

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.imageTask.setImageURI(it)
            selectedImageUri = it.toString() // Save the selected image URI
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setting up spinner data
        val categoryList: List<String> = TaskCategory.entries.map { it.displayName }
        val spinnerAdapter = ArrayAdapter(requireContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, categoryList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = spinnerAdapter
        // קבלת ה-taskId מה-arguments
        taskId = arguments?.getInt("taskId") ?: -1

        if (taskId != -1) {
            // עדכון הטופס כאשר המשימה כבר קיימת
            taskViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
                val task = tasks.find { it.id == taskId }
                task?.let {
                    binding.editTaskName.setText(it.title)
                    binding.editTaskDescription.setText(it.description)
                    binding.spinnerCategory.setSelection(categoryList.indexOf(it.category.displayName))
                    binding.checkboxStatus.isChecked = it.isCompleted
                    binding.imageTask.setImageURI(Uri.parse(it.imageUri))
                    selectedImageUri = it.imageUri
                }
            }
        }

        //הוספת תמונה למשימה
        binding.buttonUploadImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        // לחיצה על כפתור שמירה
        binding.saveButton.setOnClickListener {
            val title = binding.editTaskName.text.toString()
            val description = binding.editTaskDescription.text.toString()
            val category = binding.spinnerCategory.selectedItem.toString()
            val isCompleted = binding.checkboxStatus.isChecked

            // Validate inputs
            if (title.isEmpty() || description.isEmpty() || category.isEmpty() || selectedImageUri == null) {
                Toast.makeText(requireContext(), R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Calendar.getInstance().time)

            val task:Task
            if (taskId == -1) {//יצירת מטלה חדשה (נשלח בלי ID כדי שיקבל אוטומטי)
                task = Task(
                    title = title,
                    description = description,
                    lastModifiedDate = currentDate,
                    category = TaskCategory.valueOf(category),
                    isCompleted = false,
                    imageUri = selectedImageUri
                )

            }else {//עריכת מטלה
                task = Task(
                    id = taskId,
                    title = title,
                    description = description,
                    lastModifiedDate = currentDate,
                    category = TaskCategory.valueOf(category),
                    isCompleted = false,
                    imageUri = selectedImageUri
                )
            }

            // שמירה במסד הנתונים
            lifecycleScope.launch {
                taskViewModel.insertTask(task)
                findNavController().popBackStack()
            }

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



