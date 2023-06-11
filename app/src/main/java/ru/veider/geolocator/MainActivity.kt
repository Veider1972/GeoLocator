package ru.veider.geolocator

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.yandex.mapkit.MapKitFactory
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

//			when (destination.id) {
//			}
		}

//		val navView: BottomNavigationView = binding.navView
//
//		val navController = findNavController(R.id.nav_host_fragment_activity_main)
//		// Passing each menu ID as a set of Ids because each
//		// menu should be considered as top level destinations.
//		val appBarConfiguration = AppBarConfiguration(
//			setOf(
//				R.id.navigation_map, R.id.navigation_list
//			)
//		)
//		setupActionBarWithNavController(navController, appBarConfiguration)
//		navView.setupWithNavController(navController)
	}
}