package com.example.dohit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dohit.R
import com.example.dohit.data.TaskCategory
import com.example.dohit.databinding.FragmentTaskBinding
import com.example.dohit.utils.setupButtonWithAnimation
import com.example.dohit.viewmodel.TaskViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskViewModel: TaskViewModel

    // משתנה לאחסון ה-ImageView הנוכחי שמנגן את ה-GIF
    private var currentActiveImageView: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

        // תאריך נוכחי
        val sdf = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        val currentDate = sdf.format(Date())
        binding.dateTextView.text = currentDate


        // רשימת ה-ImageView ורשימת התמונות (סטטיות ו-GIFים)
        val imageViews = listOf(
            binding.imageView1,
            binding.imageView2,
            binding.imageView3,
            binding.imageView4,
            binding.imageView5,
            binding.imageView6,
        )

        val gifResources = listOf(
            R.drawable.mortarboard,
            R.drawable.paint_palette,
            R.drawable.cycling,
            R.drawable.boss,
            R.drawable.money_bag,
            R.drawable.warning
        )


        // עדכון התקדמות ב-ProgressBar לכל קטגוריה
        val progressBars = listOf(
            binding.progressBarEducation,
            binding.progressBarHobbies,
            binding.progressBarSport,
            binding.progressBarWork,
            binding.progressBarMoney,
            binding.progressBarUrgent
        )

        val progressTextViews = listOf(
            binding.progressTextViewEducation,
            binding.progressTextViewHobbies,
            binding.progressTextViewSport,
            binding.progressTextViewWork,
            binding.progressTextViewMoney,
            binding.progressTextViewUrgent
        )

        TaskCategory.entries.forEachIndexed { index, category ->
            taskViewModel.getTaskStatisticsByCategory(category).observe(viewLifecycleOwner) { (completed, total) ->
                progressBars[index].max = if (total == 0) 1 else total
                progressBars[index].progress = completed
                progressTextViews[index].text = "$completed/$total"
            }
        }


        TaskCategory.entries.forEachIndexed { index, category ->
            taskViewModel.getCompletionPercentageByCategory(category).observe(viewLifecycleOwner) { percentage ->
                progressBars[index].progress = percentage
                progressTextViews[index].text = "$percentage/100"
            }
        }
        val frameLayouts = listOf(
            binding.frameLayout1,
            binding.frameLayout2,
            binding.frameLayout3,
            binding.frameLayout4,
            binding.frameLayout5,
            binding.frameLayout6
        )

        // הגדרת ברירת מחדל: טוען רק את הפריים הראשון של כל GIF
        imageViews.forEachIndexed { index, imageView ->
            Glide.with(this)
                .asBitmap()
                .load(gifResources[index])
                .apply(RequestOptions.frameOf(0)) // פריים ראשון בלבד
                .into(imageView)

            // טיפול בלחיצה על התמונה
            frameLayouts.forEachIndexed { index, frameLayout ->
                setupButtonWithAnimation(frameLayout) {
                    currentActiveImageView?.let { activeView ->
                        Glide.with(this)
                            .asBitmap()
                            .load(gifResources[imageViews.indexOf(activeView)])
                            .apply(RequestOptions.frameOf(0))
                            .into(activeView)
                    }

                    // טעינת ה-GIF הנוכחי
                    Glide.with(this)
                        .asGif()
                        .load(gifResources[index])
                        .into(imageViews[index])

                    currentActiveImageView = imageViews[index]

                    lifecycleScope.launch {
                        delay(1800) // השהיה של 2 שניות
                        val action = TaskFragmentDirections.actionTaskFragmentToTaskListFragment(TaskCategory.entries[index].displayName)
                        findNavController().navigate(action)
                    }
                }
            }

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        currentActiveImageView = null
    }
}
