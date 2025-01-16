package com.example.dohit.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.dohit.data.user.User
import com.example.dohit.databinding.FragmentProfileBinding
import com.example.dohit.viewmodel.TaskViewModel
import com.example.dohit.viewmodel.UserViewModel
import java.io.File

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()
    private val taskViewModel: TaskViewModel by viewModels()
    private var currentUser: User? = null

    //Allows the user to select an image from the device, and use it on the current screen
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(it)
                val file = File(requireContext().cacheDir, "user_profile_${System.currentTimeMillis()}.jpg")
                file.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
                // update ui image
                binding.profileImage.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))

                // update db
                currentUser?.let{
                    it.profileImageUri = file.absolutePath
                    userViewModel.updateUserProfile(it)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val keyboardService = ContextCompat.getSystemService(requireContext(),InputMethodManager::class.java)


        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            currentUser = user
            user?.let {
                binding.profileImage.setImageURI(Uri.parse(it.profileImageUri))
                binding.userNameTextView.setText(it.username)
                binding.emailTextView.setText(it.email)
            } ?: run{
                Toast.makeText(requireContext(),"No user found with default ID",Toast.LENGTH_SHORT)
            }
        }
        // תצפית על כמות המשימות שהושלמו
        taskViewModel.completedTasksCount.observe(viewLifecycleOwner){ count ->
            binding.totalTasksTxt.text = count.toString()
        }
        taskViewModel.activeTasksCount.observe(viewLifecycleOwner){ count ->
            binding.ongoingTasksAmount.text = count.toString()
        }

        // set profile image
        binding.profileImage.setOnClickListener{
            pickImage.launch("image/*")
        }

        // username view events
        binding.userNameTextView.setOnClickListener{
            if(binding.emailEditText.visibility == View.VISIBLE){
                binding.emailEditText.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            }
            binding.userNameTextView.visibility = View.GONE
            binding.userNameEditText.visibility = View.VISIBLE
            binding.userNameEditText.setText(binding.userNameTextView.text)
            binding.userNameEditText.requestFocus()

            // show keyboard
            keyboardService?.showSoftInput(binding.userNameEditText, InputMethodManager.SHOW_IMPLICIT)

        }

        binding.userNameEditText.setOnEditorActionListener { _, actionId, _ ->
            // update db
            currentUser?.let{
                it.username = binding.userNameEditText.text.toString()
                userViewModel.updateUserProfile(it)
            }

            // update ui
            binding.userNameTextView.text = binding.userNameEditText.text.toString()
            binding.userNameTextView.visibility = View.VISIBLE
            binding.userNameEditText.visibility = View.GONE

            // hide keyboard
            keyboardService?.hideSoftInputFromWindow(binding.userNameEditText.windowToken,0)
            true
        }

        // email view events
        binding.emailTextView.setOnClickListener{
            if(binding.userNameEditText.visibility == View.VISIBLE){
                binding.userNameEditText.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            }
            binding.emailTextView.visibility = View.GONE
            binding.emailEditText.visibility = View.VISIBLE
            binding.emailEditText.setText(binding.emailTextView.text)
            binding.emailEditText.requestFocus()

            // show keyboard
            keyboardService?.showSoftInput(binding.userNameEditText, InputMethodManager.SHOW_IMPLICIT)

        }

        binding.emailEditText.setOnEditorActionListener { _, actionId, _ ->
            // update db
            currentUser?.let{
                it.email = binding.emailEditText.text.toString()
                userViewModel.updateUserProfile(it)
            }

            // update ui
            binding.emailTextView.text = binding.emailEditText.text.toString()
            binding.emailTextView.visibility = View.VISIBLE
            binding.emailEditText.visibility = View.GONE

            // hide keyboard
            keyboardService?.hideSoftInputFromWindow(binding.emailEditText.windowToken,0)
            true
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
