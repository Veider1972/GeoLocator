package ru.veider.geolocator.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.veider.geolocator.domain.MarkData
import ru.veider.geolocator.repo.Repo

class ListViewModel(
	private val repo: Repo
) : ViewModel() {

	private var marksDataList = mutableListOf<MarkData>()
	private val _marks = MutableLiveData<List<MarkData>>()
	val marks: LiveData<List<MarkData>> get() = _marks

	init {
		loadMarks()
	}

	private val _mark = MutableLiveData<MarkData>()
	val mark: LiveData<MarkData> get() = _mark
	fun getMark(id: Long): LiveData<MarkData> {
		marksDataList.getIndex(id)?.run {
			_mark.value = marksDataList[this]
		}
		return mark
	}

	fun updateMark(mark: MarkData) {
		marksDataList.getIndex(mark)?.run {
			marksDataList[this] = mark
			_marks.value = marksDataList
			storeMarks()
		}
	}

	fun addMark(mark: MarkData) {
		marksDataList.add(mark)
		_marks.value = marksDataList
		storeMarks()
	}

	fun deleteMark(id: Long) {
		marksDataList.getIndex(id)?.run {
			marksDataList.removeAt(this)
			_marks.value = marksDataList
			storeMarks()
		}
	}

	private fun storeMarks() {
		viewModelScope.launch {
			repo.storeMarks(marksDataList)
		}
	}

	private fun loadMarks() {
		viewModelScope.launch {
			val dataList = repo.loadMarks().toMutableList()
			withContext(Dispatchers.Main) {
				marksDataList = dataList
				_marks.value = marksDataList
			}
		}
	}

	fun newId(): Long {
		var id = -1L
		marksDataList.forEach {
			if (it.id > id)
				id = it.id
		}
		return id + 1
	}

	private fun List<MarkData>.getIndex(mark: MarkData) = getIndex(mark.id)
	private fun List<MarkData>.getIndex(id: Long): Int? {
		for (i in indices) {
			if (this[i].id == id)
				return i
		}
		return null
	}
}