package ru.veider.geolocator.ui.list

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.veider.geolocator.R
import ru.veider.geolocator.databinding.ItemMarkBinding
import ru.veider.geolocator.domain.MarkData
import ru.veider.geolocator.utils.WorldSide
import ru.veider.geolocator.utils.toDegree

class ListAdapter(
	private val onClick: OnClick,
	private val resources: Resources
) : ListAdapter<MarkData, ru.veider.geolocator.ui.list.ListAdapter.ViewHolder>(DiffCallback) {

	interface OnClick {
		fun editMark(id: Long)
		fun deleteMark(id: Long)
	}


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ru.veider.geolocator.ui.list.ListAdapter.ViewHolder =
		ViewHolder(ItemMarkBinding.inflate(LayoutInflater.from(parent.context), parent, false))

	override fun onBindViewHolder(holder: ru.veider.geolocator.ui.list.ListAdapter.ViewHolder, position: Int) {
		holder.bind(getItem(position), onClick)
	}

	inner class ViewHolder(private val binding: ItemMarkBinding) : RecyclerView.ViewHolder(binding.root) {
		fun bind(markData: MarkData, onClick: OnClick) {
			with(binding) {
				name.text = markData.name
				if (markData.desc.isNullOrEmpty())
					desc.isVisible = false
				else {
					desc.isVisible = true
					desc.text = markData.desc
				}
				coordinates.text = resources.getString(
					R.string.coordinates_text,
					"${markData.x.toDegree(resources, WorldSide.LATITUDE)} ${markData.y.toDegree(resources, WorldSide.LONGITUDE)}"
				)
				closeButton.setOnClickListener {
					onClick.deleteMark(markData.id)
				}
				container.setOnClickListener {
					onClick.editMark(markData.id)
				}
			}
		}
	}

	companion object {
		private val DiffCallback = object : DiffUtil.ItemCallback<MarkData>() {
			override fun areItemsTheSame(oldItem: MarkData, newItem: MarkData): Boolean =
				oldItem.id == newItem.id

			override fun areContentsTheSame(oldItem: MarkData, newItem: MarkData): Boolean =
				oldItem.name == newItem.name &&
						oldItem.desc == newItem.desc &&
						oldItem.x == newItem.x &&
						oldItem.y == newItem.y
		}
	}
}