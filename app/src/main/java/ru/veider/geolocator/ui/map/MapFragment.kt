package ru.veider.geolocator.ui.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.TextStyle
import com.yandex.runtime.image.ImageProvider
import org.koin.android.ext.android.inject
import ru.veider.geolocator.R
import ru.veider.geolocator.databinding.FragmentMapBinding
import ru.veider.geolocator.domain.MarkData
import ru.veider.geolocator.ui.dialog_add_mark.AddMarkDialog
import ru.veider.geolocator.ui.viewmodels.ListViewModel
import ru.veider.geolocator.ui.viewmodels.MapViewModel

class MapFragment : Fragment(R.layout.fragment_map), EasyPermissions.PermissionCallbacks {

	private var _binding: FragmentMapBinding? = null
	private val binding get() = _binding!!

	private val listViewModel: ListViewModel by inject()
	private val mapViewModel: MapViewModel by inject()

	private val locationManager: LocationManager by lazy {
		requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
	}

	private var firstUse = true

	private val locationListener: LocationListener = LocationListener { location ->
		val newPoint = Point(location.latitude, location.longitude)
		if (firstUse) {
			binding.yandexMap.map.move(
				CameraPosition(newPoint, 14.0f, 0.0f, 0.0f)
			)
			firstUse = false
		}
		mapViewModel.setMyPlace(newPoint)
	}

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

		requestLocationPermission()

		workToMap()
		workToZoomInButton()
		workToZoomOutButton()
		workToPanButton()
		workToPinButton()
		workToMyPlaceButton()

		return root
	}

	override fun onPause() {
		binding.yandexMap.onStop()
		locationManager.removeUpdates(locationListener)
		MapKitFactory.getInstance().onStop()
		super.onPause()
	}

	@SuppressLint("MissingPermission")
	override fun onResume() {
		super.onResume()
		if (hasLocationPermission())
			with(locationManager) {
				requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 2, 5000f, locationListener)
				requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 1000 * 2, 5000f, locationListener
				)
			}
		getMarksList()
		getMyPlace()
		MapKitFactory.getInstance().onStart()
		binding.yandexMap.onStart()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		EasyPermissions.onRequestPermissionsResult(
			requestCode,
			permissions,
			grantResults,
			this
		)
	}

	override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {

		if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
			SettingsDialog.Builder(requireActivity()).build()
		} else {
			requestLocationPermission()
		}
	}

	override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

	}

	private fun setPoints(myPoint: Point? = null, marks: List<MarkData>? = null) {
		binding.yandexMap.map.mapObjects.clear()
		if (myPoint != null){
			setMyPoint(myPoint)
		} else {
				if (mapViewModel.myPlace.value != null){
					mapViewModel.myPlace.value?.run{
						setMyPoint(this)
					}
				} else {
					setMyPoint(startPoint)
				}
			}
		if (marks != null){
				setMarkers(marks)
		} else {
			listViewModel.marks.value?.run {
					setMarkers(this)
			}
		}
	}

	private fun setMyPoint(point: Point){
		binding.yandexMap.map.mapObjects.addPlacemark(
			point, ImageProvider.fromResource(
				requireContext(), R.drawable.my_place
			)
		)
	}

	private fun setMarkers(marks: List<MarkData>){
		val textStyle = TextStyle().apply {
			offsetFromIcon = true
			offset = -32f
			placement = TextStyle.Placement.BOTTOM_RIGHT
		}
		marks.forEach {
			binding.yandexMap.map.mapObjects.addPlacemark(
				Point(it.x, it.y), ImageProvider.fromResource(
					requireContext(), R.drawable.map_mark
				)
			).apply {
				setText(it.name, textStyle)
			}
		}

	}

	private fun workToMap() {
		binding.yandexMap.map.apply {
			addInputListener(inputListener)
			move(CameraPosition(mapViewModel.myPlace.value ?: startPoint, 14.0f, 0.0f, 0.0f))
			setPoints(myPoint = startPoint)
		}
	}

	private fun workToZoomInButton() {
		binding.zoomInButton.setOnClickListener {
			val camera = binding.yandexMap.map.cameraPosition
			binding.yandexMap.map.move(
				CameraPosition(camera.target, camera.zoom + 1, camera.azimuth, camera.tilt)
			)
		}
	}

	private fun workToZoomOutButton() {
		binding.zoomOutButton.setOnClickListener {
			val camera = binding.yandexMap.map.cameraPosition
			binding.yandexMap.map.move(
				CameraPosition(camera.target, camera.zoom - 1, camera.azimuth, camera.tilt)
			)
		}
	}

	private fun workToPanButton() {
		binding.panButton.apply {
			backgroundTintList = resources.getColorStateList(R.color.selected, null)
			setOnClickListener {
				mapMode = MapMode.PAN
				binding.panButton.backgroundTintList = resources.getColorStateList(R.color.selected, null)
				binding.pinButton.backgroundTintList = binding.zoomInButton.backgroundTintList
			}
		}
	}

	private fun workToPinButton() {
		binding.pinButton.setOnClickListener {
			mapMode = MapMode.PIN
			binding.pinButton.backgroundTintList = resources.getColorStateList(R.color.selected, null)
			binding.panButton.backgroundTintList = binding.zoomInButton.backgroundTintList
		}
	}

	private fun workToMyPlaceButton() {
		binding.myPlaceButton.setOnClickListener {
			mapViewModel.myPlace.value?.run {
				val zoom = binding.yandexMap.map.cameraPosition.zoom
				val azimuth = binding.yandexMap.map.cameraPosition.azimuth
				val tilt = binding.yandexMap.map.cameraPosition.tilt
				binding.yandexMap.map.move(CameraPosition(this, zoom, azimuth, tilt))
			}
		}
	}

	private fun getMarksList() {
		listViewModel.marks.observe(viewLifecycleOwner) {
			setPoints(myPoint = mapViewModel.myPlace.value, marks = it)
		}
	}

	private fun getMyPlace() {
		mapViewModel.myPlace.observe(viewLifecycleOwner) {
			setPoints(myPoint = it)
		}
	}

	private fun hasLocationPermission() =
		EasyPermissions.hasPermissions(
			requireContext(),
			ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
		)

	private fun requestLocationPermission() {
		EasyPermissions.requestPermissions(
			this,
			getString(R.string.permission_message),
			PERMISSION_REQUEST_CODE,
			ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
		)
	}

	companion object {
		private enum class MapMode {
			PIN, PAN
		}

		private val startPoint = Point(55.753922, 37.620243)

		private const val PERMISSION_REQUEST_CODE = 8674
	}
}