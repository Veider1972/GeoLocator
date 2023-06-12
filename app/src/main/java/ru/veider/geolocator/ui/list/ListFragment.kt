package ru.veider.geolocator.ui.list

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.veider.geolocator.R
import ru.veider.geolocator.databinding.DialogEditMarkBinding
import ru.veider.geolocator.databinding.FragmentListBinding
import ru.veider.geolocator.domain.MarkData
import ru.veider.geolocator.ui.dialog_edit_mark.EditMarkDialog
import ru.veider.geolocator.ui.viewmodels.ListViewModel

class ListFragment : Fragment(R.layout.fragment_list) {

	private val binding by viewBinding(FragmentListBinding::bind)
	private lateinit var adapter: ListAdapter

	private val listViewModel: ListViewModel by inject()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		workToAdapter()
		workToObjects()
	}

	private fun workToAdapter() {
		adapter=ListAdapter(
			object:ListAdapter.OnClick{
				override fun editMark(id: Long) {
					findNavController().navigate(R.id.action_fragmentList_to_dialogEditMark,
					bundleOf(EditMarkDialog.ID to id)
					)
				}

				override fun deleteMark(id: Long) {
					listViewModel.deleteMark(id)
				}

			}, resources
		)
		binding.marksList.adapter = adapter
	}

	private fun workToObjects(){
		listViewModel.marks.observe(viewLifecycleOwner){
			val list = mutableListOf<MarkData>()
			it.forEach {
				list.add(it.copy())
			}
			adapter.submitList(list)
		}
	}
}