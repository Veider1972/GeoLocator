package ru.veider.geolocator.repo

import ru.veider.geolocator.domain.MarkData

interface Repo {
	suspend fun storeMarks(dataList: List<MarkData>)
	suspend fun loadMarks(): List<MarkData>
}