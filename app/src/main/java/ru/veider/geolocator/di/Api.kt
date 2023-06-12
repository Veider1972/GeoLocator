package ru.veider.geolocator.di

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.Koin
import org.koin.dsl.koinApplication

object Api {

	val GEOPREFS = "geolocatorPreferences"
	val DATA = "marksList"

	val gson = Gson()

	private lateinit var appContext: Context

	val koin: Koin by lazy {
		koinApplication {
			androidContext(appContext)
			androidLogger()
			modules(appModule)
		}.koin
	}

	@Synchronized
	fun init(context: Context) {
		check(!::appContext.isInitialized) { "Already initialized!" }

		appContext = context.applicationContext
	}
}