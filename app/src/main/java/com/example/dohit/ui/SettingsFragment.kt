package com.example.dohit.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.dohit.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // טען את מצב הלילה הנוכחי
        val isNightModeEnabled = loadNightModeState()
        binding.switch2.isChecked = isNightModeEnabled

        // הגדרת מצב הלילה לפי מתג
        binding.switch2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            saveNightModeState(isChecked)
        }
    }

    private fun saveNightModeState(isEnabled: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("night_mode", isEnabled).apply()
    }

    private fun loadNightModeState(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("night_mode", false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
