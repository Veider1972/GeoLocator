package ru.veider.geolocator.utils

import android.content.res.Resources
import ru.veider.geolocator.R
import kotlin.math.abs
import kotlin.math.roundToInt

fun Double.toDegree(resources: Resources, side: WorldSide): String {
	val degree = this.toInt()
	val rawMinute = abs(this % 1 * 60)
	val minute = rawMinute.toInt()
	val second = (rawMinute % 1 * 60).roundToInt()
	val worldSide = when (side) {
		WorldSide.LATITUDE ->
			if (this >= 0)
				resources.getString(R.string.latitude_north)
			else
				resources.getString(R.string.latitude_south)

		WorldSide.LONGITUDE ->
			if (this >= 0)
				resources.getString(R.string.longitude_east)
			else
				resources.getString(R.string.longitude_west)

	}
	return String.format("%d° %d′ %d %s″", degree, minute, second, worldSide)
}

enum class WorldSide {
	LATITUDE, LONGITUDE
}