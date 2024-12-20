package com.example.dohit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dohit.R
import com.example.dohit.databinding.FragmentTaskBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    // משתנה לאחסון ה-ImageView הנוכחי שמנגן את ה-GIF
    private var currentActiveImageView: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)

        // תאריך נוכחי
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
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
            binding.imageView7
        )

        val gifResources = listOf(
            R.drawable.mortarboard, // קובצי GIF
            R.drawable.paint_palette,
            R.drawable.cycling,
            R.drawable.boss,
            R.drawable.money_bag,
            R.drawable.warning,
            R.drawable.fast_forward
            )

        val staticImages = listOf(
            R.drawable.mortarboard, // קובצי GIF
            R.drawable.paint_palette,
            R.drawable.cycling,
            R.drawable.boss,
            R.drawable.money_bag,
            R.drawable.warning,
            R.drawable.fast_forward
        )

        // הגדרת ברירת מחדל: טוען רק את הפריים הראשון של כל GIF
        imageViews.forEachIndexed { index, imageView ->
            Glide.with(this)
                .asBitmap() // טוען את הפריים הראשון של ה-GIF
                .load(gifResources[index])
                .apply(RequestOptions.frameOf(0)) // מבטיח טעינת הפריים הראשון
                .into(imageView)

            // טיפול בלחיצה על התמונה
            imageView.setOnClickListener {
                // החזרת התמונה הקודמת למצב סטטי
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
                    .into(imageView)

                // עדכון התמונה הנוכחית כפעילה
                currentActiveImageView = imageView
                lifecycleScope.launch {
                    delay(2000) // השהיה של 2 שניות
                    val folderNames = listOf("Education", "Hobbies", "Sport", "Work", "Money", "Urgent!")
                    val action = TaskFragmentDirections.actionTaskFragmentToTaskListFragment(folderNames[index])
                    findNavController().navigate(action)
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