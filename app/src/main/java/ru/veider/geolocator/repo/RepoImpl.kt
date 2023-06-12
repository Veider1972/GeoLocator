package ru.veider.geolocator.repo

import android.content.SharedPreferences
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.veider.geolocator.di.Api
import ru.veider.geolocator.domain.MarkData

class RepoImpl(
	private val prefs: SharedPreferences,
	private val gson: Gson
):Repo {
	override suspend fun storeMarks(dataList: List<MarkData>) {
		prefs.run {
			CoroutineScope(Dispatchers.IO).launch {
				val list = gson.toJson(dataList)
				edit().putString(Api.DATA, list).apply()
			}
		}
	}

	override suspend fun loadMarks(): List<MarkData> {
		prefs.run {
			val list = getString(Api.DATA, null)
			list?.run {
				return gson.fromJson(list, Array<MarkData>::class.java).toList()
			}
		}
		return emptyList()
	}
}