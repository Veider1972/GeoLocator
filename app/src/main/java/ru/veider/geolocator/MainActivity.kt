package ru.veider.geolocator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.veider.geolocator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding

	private lateinit var navController: NavController

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		val navHostFragment = supportFragmentManager.findFragmentById(
			R.id.nav_host_fragment
		) as NavHostFragment
		navController = navHostFragment.navController

		binding.bottomNav.setupWithNavController(navController)

		navController.addOnDestinationChangedListener { _, destination, _ ->

			when (destination.id) {
				R.id.fragmentMap,
				R.id.fragmentList,
				R.id.dialogAddMark -> {
				}
			}
		}
	}
}