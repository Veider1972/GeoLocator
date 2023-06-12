package ru.veider.geolocator.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.veider.geolocator.repo.Repo
import ru.veider.geolocator.repo.RepoImpl
import ru.veider.geolocator.ui.viewmodels.MapViewModel
import ru.veider.geolocator.ui.viewmodels.ListViewModel

val appModule = module {

	single { Api.gson }
	single { androidContext().getSharedPreferences(Api.GEOPREFS, Context.MODE_PRIVATE) }

	singleOf(::RepoImpl){bind<Repo>()}

	singleOf(::MapViewModel)
	singleOf(::ListViewModel)
}