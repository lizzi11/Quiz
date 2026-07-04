package com.example.quiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.quiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val currentId = navController.currentDestination?.id

            if (item.itemId == R.id.categoriesFragment) {
                if (currentId == R.id.quizSummaryFragment || currentId == R.id.reviewAnswersFragment) {

                    return@setOnItemSelectedListener navController.popBackStack(R.id.categoriesFragment, false)
                }
            }
            item.onNavDestinationSelected(navController)
        }

        binding.bottomNavigation.setOnItemReselectedListener { item ->
            if (item.itemId == R.id.categoriesFragment) {
                val currentId = navController.currentDestination?.id
                if (currentId != R.id.categoriesFragment) {
                    navController.popBackStack(R.id.categoriesFragment, false)
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible = destination.id !in setOf(
                R.id.loginFragment,
                R.id.registerFragment,
                R.id.quizFragment
            )
        }
    }
}