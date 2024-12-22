package com.example.dohit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

import androidx.navigation.ui.NavigationUI
import com.example.dohit.databinding.ActivityMainBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // הגדרת NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // pass to my task fragment
        binding.linearLayoutMyTasks.setOnClickListener {
            if (navController.currentDestination?.id != R.id.taskFragment) {
                navController.navigate(R.id.taskFragment)
            }
        }

        //pass to setting fragment
        binding.linearLayoutSettings.setOnClickListener {
            if (navController.currentDestination?.id != R.id.settingsFragment) {
                navController.navigate(R.id.settingsFragment)
            }
        }

        //pass to profile fragment
        binding.linearLayoutProfile.setOnClickListener {
            if (navController.currentDestination?.id != R.id.profileFragment) {
                navController.navigate(R.id.profileFragment)
            }
        }

        //מעבר לfragment main
        binding.linearLayoutHome.setOnClickListener {
            if (navController.currentDestination?.id != R.id.main) {
                navController.navigate(R.id.mainFragment)
            }
        }



        // Floating Action Button: Add Task
        binding.addTaskButton.setOnClickListener {
            navController.navigate(R.id.addEditTaskFragment)
        }



    }

}
