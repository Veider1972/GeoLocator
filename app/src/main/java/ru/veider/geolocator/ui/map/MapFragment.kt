package ru.veider.geolocator.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import ru.veider.geolocator.databinding.FragmentMapBinding

class MapFragment : Fragment() {

	private var _binding: FragmentMapBinding? = null

	// This property is only valid between onCreateView and
	// onDestroyView.
	private val binding get() = _binding!!

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		MapKitFactory.initialize(requireContext())
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {



		val mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)


		_binding = FragmentMapBinding.inflate(inflater, container, false)
		val root: View = binding.root

		binding.yandexMap.map.move(
			CameraPosition(Point(59.945933, 30.320045), 14.0f, 0.0f, 0.0f),
			Animation(Animation.Type.SMOOTH, 5f),
			null
		)

		return root
	}

	override fun onPause() {
		binding.yandexMap.onStop()
		MapKitFactory.getInstance().onStop()
		super.onPause()
	}

	override fun onResume() {
		super.onResume()
		MapKitFactory.getInstance().onStart()
		binding.yandexMap.onStart()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}