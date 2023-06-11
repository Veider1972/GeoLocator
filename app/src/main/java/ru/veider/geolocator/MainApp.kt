package ru.veider.geolocator

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import java.util.*

class MainApp : Application() {

	override fun onCreate() {
		super.onCreate()
		MapKitFactory.setApiKey("697dfb65-2920-481d-b199-be06ccde7a36")
	}
}