package ru.veider.geolocator.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.runtime.image.ImageProvider
import org.koin.android.ext.android.inject
import ru.veider.geolocator.R
import ru.veider.geolocator.databinding.FragmentMapBinding
import ru.veider.geolocator.ui.dialog_add_mark.AddMarkDialog
import ru.veider.geolocator.ui.viewmodels.ListViewModel
import ru.veider.geolocator.ui.viewmodels.MapViewModel

class MapFragment : Fragment(R.layout.fragment_map) {

	private var _binding: FragmentMapBinding? = null
	private val binding get() = _binding!!

	private val mapViewModel: MapViewModel by inject()
	private val listViewModel: ListViewModel by inject()

	private var mapMode = MapMode.PAN

	private var inputListener = object : InputListener {
		override fun onMapTap(map: Map, point: Point) {
			when (mapMode) {
				MapMode.PIN -> {
					findNavController().navigate(
						R.id.action_fragmentMap_to_dialogAddMark,
						bundleOf(
							AddMarkDialog.COOX to point.latitude,
							AddMarkDialog.COOY to point.longitude
						)
					)
				}

				else -> {}
			}
		}

		override fun onMapLongTap(map: Map, point: Point) {}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		MapKitFactory.initialize(requireContext())
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {

		_binding = FragmentMapBinding.inflate(inflater, container, false)
		val root: View = binding.root

		binding.yandexMap.map.apply {
			addInputListener(inputListener)
			move(CameraPosition(Point(59.945933, 30.320045), 14.0f, 0.0f, 0.0f))
		}
		binding.zoomInButton.setOnClickListener {
			val camera = binding.yandexMap.map.cameraPosition
			binding.yandexMap.map.move(
				CameraPosition(camera.target, camera.zoom + 1, camera.azimuth, camera.tilt)
			)
		}
		binding.zoomOutButton.setOnClickListener {
			val camera = binding.yandexMap.map.cameraPosition
			binding.yandexMap.map.move(
				CameraPosition(camera.target, camera.zoom - 1, camera.azimuth, camera.tilt)
			)
		}
		binding.panButton.apply {
			backgroundTintList = resources.getColorStateList(R.color.selected, null)
			setOnClickListener {
				mapMode = MapMode.PAN
				binding.panButton.backgroundTintList = resources.getColorStateList(R.color.selected, null)
				binding.pinButton.backgroundTintList = binding.zoomInButton.backgroundTintList
			}
		}
		binding.pinButton.setOnClickListener {
			mapMode = MapMode.PIN
			binding.pinButton.backgroundTintList = resources.getColorStateList(R.color.selected, null)
			binding.panButton.backgroundTintList = binding.zoomInButton.backgroundTintList
		}

		listViewModel.marks.observe(viewLifecycleOwner) {
			with(binding.yandexMap.map.mapObjects) {
				clear()
				it.forEach {
					addPlacemark(
						Point(it.x, it.y), ImageProvider.fromResource(
							requireContext(), R.drawable.map_mark
						)
					)
				}
			}
		}

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

	companion object {
		private enum class MapMode {
			PIN, PAN
		}
	}
}