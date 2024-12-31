package com.example.dohit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

import com.example.dohit.databinding.ActivityMainBinding
import com.example.dohit.utils.setupButtonWithAnimation
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
// keep the size off the + button
        binding.addTaskButton.size = FloatingActionButton.SIZE_NORMAL
        binding.addTaskButton.visibility = View.VISIBLE

        // הגדרת NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.addEditTaskFragment,
                R.id.taskDetailsFragment,
                R.id.taskListFragment -> { // מסכים נוספים להסתיר בהם את ה-Bar
                    binding.bottomAppBar.visibility = View.GONE
                    binding.addTaskButton.visibility = View.GONE
                }
                else -> { // מסכים שבהם ה-Bar צריך להופיע
                    binding.bottomAppBar.visibility = View.VISIBLE
                    binding.addTaskButton.visibility = View.VISIBLE
                }
            }
        }

// My Tasks Button
        setupButtonWithAnimation(binding.linearLayoutMyTasks) {
            if (navController.currentDestination?.id != R.id.taskFragment) {
                navController.navigate(R.id.taskFragment)
            }
        }

// Settings Button
        setupButtonWithAnimation(binding.linearLayoutSettings) {
            if (navController.currentDestination?.id != R.id.settingsFragment) {
                navController.navigate(R.id.settingsFragment)
            }
        }

// Profile Button
        setupButtonWithAnimation(binding.linearLayoutProfile) {
            if (navController.currentDestination?.id != R.id.profileFragment) {
                navController.navigate(R.id.profileFragment)
            }
        }

// Home Button
        setupButtonWithAnimation(binding.linearLayoutHome) {
            if (navController.currentDestination?.id != R.id.main) {
                navController.navigate(R.id.mainFragment)
            }
        }



        setupButtonWithAnimation(binding.addTaskButton) {
            navController.navigate(R.id.addEditTaskFragment)
        }


    }

}
