package com.example.dohit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.dohit.databinding.FragmentProfileBinding
import com.example.dohit.viewmodel.TaskViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // תצפית על כמות המשימות שהושלמו
        taskViewModel.completedTasksCount.observe(viewLifecycleOwner) { count ->
            binding.totalTasksTxt.text = count.toString()
        }

        // תצפית על כמות המשימות הפעילות
        taskViewModel.activeTasksCount.observe(viewLifecycleOwner) { count ->
            binding.ongoingTasksAmount.text = count.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
