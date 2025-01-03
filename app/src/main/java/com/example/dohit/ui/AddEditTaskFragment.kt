package com.example.dohit.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.dohit.adapter.CategoryAdapter
import com.example.dohit.utils.setupButtonWithAnimation
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

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddEditTaskBinding.inflate(inflater, container, false)
        val binding = _binding!!

        // רשימת הקטגוריות
        val categories = TaskCategory.entries
        Log.d("AddEditTaskFragment", "Categories: ${categories.joinToString { it.getLocalizedDisplayName() }}")

        // הגדרת ה-RecyclerView
        categoryAdapter = CategoryAdapter(categories) { selectedCategory ->
            // עדכון הקטגוריה הנבחרת ב-ViewModel
            taskViewModel.setSelectedCategory(selectedCategory)
        }

        binding.categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRecyclerView.adapter = categoryAdapter

        // טעינת הקטגוריה הנבחרת מראש
        taskViewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            categoryAdapter.setSelectedCategory(category)
        }

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setting up spinner data
        val categoryList: List<String> = TaskCategory.entries.map { it.getLocalizedDisplayName() }
        val backButton = view.findViewById<ImageButton>(R.id.backButton)

        setupButtonWithAnimation(backButton) {
            requireActivity().onBackPressed() // חזרה למסך הקודם
        }


        // קבלת ה-taskId מה-arguments
        taskId = arguments?.getInt("taskId") ?: -1

        if (taskId != -1) {
            taskViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
                val task = tasks.find { it.id == taskId }
                task?.let {
                    Log.d("AddEditTaskFragment", "Selected category: ${it.category.getLocalizedDisplayName()}") // הוספת הלוג כאן
                    binding.editTaskName.setText(it.title)
                    binding.editTaskDescription.setText(it.description)
                    binding.checkboxStatus.isChecked = it.isCompleted
                    categoryAdapter.setSelectedCategory(it.category)

                    if (!it.imageUri.isNullOrEmpty()) {
                        try {
                            val uri = Uri.parse(it.imageUri)
                            binding.imageTask.setImageURI(uri)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            binding.imageTask.setImageResource(R.drawable.add_image)
                        }
                    } else {
                        binding.imageTask.setImageResource(R.drawable.add_image)
                    }

                    selectedImageUri = it.imageUri
                }
            }
        }


        //הוספת תמונה למשימה
        binding.buttonUploadImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        // לחיצה על כפתור שמירה
// לחיצה על כפתור שמירה
        binding.saveButton.setOnClickListener {
            val title = binding.editTaskName.text.toString()
            val description = binding.editTaskDescription.text.toString()
            val selectedCategory = categoryAdapter.getSelectedCategory() // קבלת הקטגוריה הנבחרת מה-Adapter
            val isCompleted = binding.checkboxStatus.isChecked

            // Validate inputs
            if (title.isEmpty()) {
                Toast.makeText(requireContext(), R.string.error_empty_fields_title, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if ( description.isEmpty() ) {
                Toast.makeText(requireContext(), R.string.error_empty_fields_description, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if ( selectedCategory == null) {
                Toast.makeText(requireContext(), R.string.error_empty_fields_category, Toast.LENGTH_SHORT).show()
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
                    category = selectedCategory,
                    isCompleted = isCompleted,
                    imageUri = selectedImageUri
                )
            } else { // עריכת מטלה
                task = Task(
                    id = taskId,
                    title = title,
                    description = description,
                    lastModifiedDate = currentDate,
                    category = selectedCategory,
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
