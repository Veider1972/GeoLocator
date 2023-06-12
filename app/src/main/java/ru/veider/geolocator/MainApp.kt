package ru.veider.geolocator

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.veider.geolocator.di.appModule
import java.util.*

class MainApp : Application() {

	override fun onCreate() {
		super.onCreate()
		startKoin {
			androidLogger()
			androidContext(this@MainApp)
			modules(appModule)
		}
		MapKitFactory.setApiKey("697dfb65-2920-481d-b199-be06ccde7a36")
	}
}