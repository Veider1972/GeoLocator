package ru.veider.geolocator.domain

data class MarkData(
	val id: Long,
	var name: String,
	var desc: String?,
	val x: Double,
	val y: Double,
)
