package com.example.dohit.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.example.dohit.data.task.Task
import com.example.dohit.data.task.TaskCategory
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
import java.time.format.DateTimeFormatter
import java.util.Date

class AddEditTaskFragment : Fragment() {

    private var _binding: FragmentAddEditTaskBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: String? = null
    private var taskId: Int =-1
    private var startDate: String? = null
    private var startTime: String? = null
    private var endDate: String? = null
    private var endTime: String? = null

    private val taskViewModel: TaskViewModel by viewModels()

    //Allows the user to select an image from the device, and use it on the current screen
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

    //Opens a window to select a date
    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            onDateSelected(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    // Opens a window to  select Time
    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            onTimeSelected(formattedTime)
        }, hour, minute, true)

        timePickerDialog.show()
    }
// לא סגורה מה זה עושה
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

        //  הגדרת ה-RecyclerView של הקטגוריות
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

        binding.buttonStartDate.setOnClickListener {
            showDatePicker { selectedDate ->
                startDate = selectedDate
                binding.buttonStartDate.text = selectedDate
            }
        }

        binding.buttonStartTime.setOnClickListener {
            showTimePicker { selectedTime ->
                startTime = selectedTime
                binding.buttonStartTime.text = selectedTime
            }
        }

        binding.buttonEndDate.setOnClickListener {
            showDatePicker { selectedDate ->
                endDate = selectedDate
                binding.buttonEndDate.text = selectedDate
            }
        }

        binding.buttonEndTime.setOnClickListener {
            showTimePicker { selectedTime ->
                endTime = selectedTime
                binding.buttonEndTime.text = selectedTime
            }
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

                    // עדכון תאריכים ושעות
                    if (!it.startTime.isNullOrEmpty()) {
                        val startDateTimeParts = it.startTime.split(" ")
                        if (startDateTimeParts.size == 2) {
                            startDate = startDateTimeParts[0] // תאריך התחלה
                            startTime = startDateTimeParts[1] // שעה התחלה
                            binding.buttonStartDate.text = startDate
                            binding.buttonStartTime.text = startTime
                        }
                    }

                    if (!it.endTime.isNullOrEmpty()) {
                        val endDateTimeParts = it.endTime.split(" ")
                        if (endDateTimeParts.size == 2) {
                            endDate = endDateTimeParts[0] // תאריך סיום
                            endTime = endDateTimeParts[1] // שעה סיום
                            binding.buttonEndDate.text = endDate
                            binding.buttonEndTime.text = endTime
                        }
                    }


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
        binding.saveButton.setOnClickListener {
            val title = binding.editTaskName.text.toString().trim()
            val description = binding.editTaskDescription.text.toString().trim()
            val selectedCategory = categoryAdapter.getSelectedCategory() // קבלת הקטגוריה הנבחרת מה-Adapter
            val isCompleted = binding.checkboxStatus.isChecked
            val startDate = this@AddEditTaskFragment.startDate
            val startTime = this@AddEditTaskFragment.startTime
            val endDate = this@AddEditTaskFragment.endDate
            val endTime = this@AddEditTaskFragment.endTime


            val missingFields = mutableListOf<String>() // רשימה לשדות חסרים

                // איפוס הגבול של כל השדות (כדי להסיר אדום אם הכל תקין)
            binding.taskNameLabel.setTextColor(resources.getColor(R.color.dark_purple, null))
            binding.taskDescription.setTextColor(resources.getColor(R.color.dark_purple, null))
            binding.textViewCategoryTitle.setTextColor(resources.getColor(R.color.dark_purple, null))
            binding.startDateLabel.setTextColor(resources.getColor(R.color.dark_purple, null))
            binding.endDateLabel.setTextColor(resources.getColor(R.color.dark_purple, null))

            if (title.isEmpty()) {
                missingFields.add(getString(R.string.taskTitle))
               binding.taskNameLabel.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            }
            if (description.isEmpty()) {
                missingFields.add(getString(R.string.task_description))
                binding.taskDescription.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            }
            if (selectedCategory == null) {
                missingFields.add(getString(R.string.task_category))
                binding.textViewCategoryTitle.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            //TO DO: מראה לכך שחסר קטגוריה
            }

            if (startDate.isNullOrEmpty() || startTime.isNullOrEmpty()) {
                missingFields.add(getString(R.string.starting_date))
                binding.startDateLabel.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            }

            if (endDate.isNullOrEmpty() || endTime.isNullOrEmpty()) {
                missingFields.add(getString(R.string.end_date_label))
                binding.endDateLabel.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            }
            // אם רשימת השדות החסרים אינה ריקה, הצגת הודעת שגיאה ומניעת המשך פעולה
            if (missingFields.isNotEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "${getString(R.string.missing_fields)}: ${missingFields.joinToString(", ")}",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }else {
                // when all required fields are filled, need to check if start < end
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

                val startDateTime: Date = formatter.parse("$startDate $startTime")
                val endDateTime: Date = formatter.parse("$endDate $endTime")

                if(endDateTime.before(startDateTime)){
                    binding.startDateLabel.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
                    binding.endDateLabel.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.start_end_validation_error),
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
            }
                // המשך שמירת המשימה אם הכל תקין
            val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Calendar.getInstance().time)



            val task = if (taskId == -1) { // יצירת מטלה חדשה
                Task(
                    title = title,
                    description = description,
                    lastModifiedDate = currentDate,
                    category = selectedCategory!!,
                    isCompleted = isCompleted,
                    imageUri = selectedImageUri,
                    startTime = "$startDate $startTime",
                    endTime = "$endDate $endTime"
                )
            } else { // עריכת מטלה
                Task(
                    id = taskId,
                    title = title,
                    description = description,
                    lastModifiedDate = currentDate,
                    category = selectedCategory!!,
                    isCompleted = isCompleted,
                    imageUri = selectedImageUri,
                    startTime = "$startDate $startTime",
                    endTime = "$endDate $endTime"
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