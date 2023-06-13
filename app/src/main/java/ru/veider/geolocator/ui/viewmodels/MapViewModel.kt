package ru.veider.geolocator.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.geometry.Point

class MapViewModel : ViewModel() {

	private val _myPlace = MutableLiveData<Point>()
	val myPlace: LiveData<Point> = _myPlace

	fun setMyPlace(point: Point) {
		_myPlace.value = point
	}
}