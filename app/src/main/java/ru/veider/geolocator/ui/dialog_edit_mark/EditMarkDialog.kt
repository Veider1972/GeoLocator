package ru.veider.geolocator.ui.dialog_edit_mark

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import ru.veider.geolocator.R
import ru.veider.geolocator.databinding.DialogEditMarkBinding
import ru.veider.geolocator.domain.MarkData
import ru.veider.geolocator.ui.viewmodels.ListViewModel

class EditMarkDialog : DialogFragment(R.layout.dialog_edit_mark) {

	private val listViewModel: ListViewModel by inject()

	private lateinit var binding: DialogEditMarkBinding
	private val id: Long? by lazy {
		arguments?.getLong(ID) }

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		binding = DialogEditMarkBinding.inflate(layoutInflater)

		workToAcceptButton()
		workToCancelButton()


		return AlertDialog.Builder(requireActivity())
			.setView(binding.root)
			.create()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		onGetData()

		dialog?.apply {
			window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
			setCanceledOnTouchOutside(false)
		}
	}

	private fun workToAcceptButton() {
		binding.acceptButton.setOnClickListener {
			if (binding.titleEditor.text.isNullOrEmpty()) {
				Toast.makeText(requireContext(), getString(R.string.mark_name_error), Toast.LENGTH_SHORT).show()
			} else {
				listViewModel.mark.value?.run {
					val title = binding.titleEditor.text.toString()
					val desc = if (!binding.descEditor.text.isNullOrEmpty())binding.descEditor.text.toString() else null
					listViewModel.updateMark(
						MarkData(
							id = id,
							name = title,
							desc = desc,
							x = x,
							y = y
						)
					)
					findNavController().navigateUp()
				}

			}
		}
	}

	private fun workToCancelButton() {
		binding.cancelButton.setOnClickListener {
			findNavController().navigateUp()
		}
	}

	private fun onGetData(){
		id?.run {
			listViewModel.getMark(this).observe(viewLifecycleOwner){
				binding.titleEditor.setText(it.name)
				binding.descEditor.setText(it.desc)
			}
		}
	}

	companion object {
		const val ID = "id"
	}
}