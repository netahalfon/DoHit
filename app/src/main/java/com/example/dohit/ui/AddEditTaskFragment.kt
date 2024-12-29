package com.example.dohit.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
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
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import java.io.File

class AddEditTaskFragment : Fragment() {

    private var _binding: FragmentAddEditTaskBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: String? = null
    private var taskId: Int =-1

    private val taskViewModel: TaskViewModel by viewModels()

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(it)
                val file = File(requireContext().cacheDir, "task_image_${System.currentTimeMillis()}.jpg")
                file.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
                selectedImageUri = file.absolutePath
                binding.imageTask.setImageBitmap(BitmapFactory.decodeFile(selectedImageUri))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showAnimatedToast(message: String) {
        // יצירת Layout מותאם אישית
        val inflater = LayoutInflater.from(requireContext())
        val layout: View = inflater.inflate(R.layout.custom_toast_with_animation, null)

        // הגדרת הטקסט של ההודעה
        val toastText = layout.findViewById<TextView>(R.id.toast_text)
        toastText.text = message

        // יצירת Toast מותאם אישית
        val toast = Toast(requireContext())
        toast.view = layout
        toast.duration = Toast.LENGTH_LONG
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
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
        val categoryList: List<String> = TaskCategory.entries.map { it.getLocalizedDisplayName() }
        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            requireActivity().onBackPressed() // חזרה למסך הקודם
        }
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
                    binding.spinnerCategory.setSelection(categoryList.indexOf(it.category.getLocalizedDisplayName()))
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
            if (title.isEmpty() || description.isEmpty() || category.isEmpty()) {
                Toast.makeText(requireContext(), R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Calendar.getInstance().time)

            val task: Task
            if (taskId == -1) { // יצירת מטלה חדשה
                task = Task(
                    title = title,
                    description = description,
                    lastModifiedDate = currentDate,
                    category = TaskCategory.entries.find { it.getLocalizedDisplayName() == category } ?: TaskCategory.Work,
                    isCompleted = isCompleted,
                    imageUri = selectedImageUri
                )
            } else { // עריכת מטלה
                task = Task(
                    id = taskId,
                    title = title,
                    description = description,
                    lastModifiedDate = currentDate,
                    category = TaskCategory.entries.find { it.getLocalizedDisplayName() == category } ?: TaskCategory.Work,
                    isCompleted = isCompleted,
                    imageUri = selectedImageUri
                )
            }

            // הצגת GIF בעת לחיצה
            binding.saveButton.setImageResource(R.drawable.add) // החלף ב-GIF המתאים
            Glide.with(this)
                .asGif()
                .load(R.drawable.add)
                .into(binding.saveButton)

            // עיכוב עד לסיום ה-GIF
            Handler(Looper.getMainLooper()).postDelayed({
                // שמירה במסד הנתונים
                lifecycleScope.launch {
                    taskViewModel.insertTask(task)
                    findNavController().popBackStack()

                    // הצגת הטוסט המותאם אישית עם אנימציה
                    val message = if (Locale.getDefault().language == "he") {
                        "משימה נשמרה בהצלחה!"
                    } else {
                        "Task saved successfully!"
                    }
                    showAnimatedToast(message)

                    // החזרת הכפתור לתמונה המקורית
                    binding.saveButton.setImageResource(R.drawable.add) // תמונה סטטית
                }
            }, 3000) // משך זמן ה-GIF במילישניות
        }


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
