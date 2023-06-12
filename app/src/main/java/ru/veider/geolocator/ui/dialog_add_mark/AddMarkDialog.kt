package ru.veider.geolocator.ui.dialog_add_mark

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import ru.veider.geolocator.R
import ru.veider.geolocator.databinding.DialogAddMarkBinding
import ru.veider.geolocator.domain.MarkData
import ru.veider.geolocator.ui.viewmodels.ListViewModel

class AddMarkDialog : DialogFragment(R.layout.dialog_add_mark) {

	private val listViewModel: ListViewModel by inject()

	private lateinit var binding: DialogAddMarkBinding
	private val cooX: Double? by lazy {
		arguments?.getDouble(COOX) }
	private val cooY: Double? by lazy {
		arguments?.getDouble(COOY) }

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		binding = DialogAddMarkBinding.inflate(layoutInflater)

		workToAcceptButton()
		workToCancelButton()

		return AlertDialog.Builder(requireActivity())
			.setView(binding.root)
			.create()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		dialog?.apply {
			window?.setBackgroundDrawableResource(R.drawable.shape_dialog)
			setCanceledOnTouchOutside(false)
		}
	}

	private fun workToAcceptButton() {
		binding.acceptButton.setOnClickListener {
			if (binding.textEditor.text.isNullOrEmpty()) {
				Toast.makeText(requireContext(), getString(R.string.mark_name_error), Toast.LENGTH_SHORT).show()
			} else if (cooX != null && cooY != null) {
				val id =  listViewModel.newId()
				val name = binding.textEditor.text.toString()
				listViewModel.addMark(
					MarkData(
						id = id,
						name = name,
						desc = null,
						x = cooX!!,
						y = cooY!!
					)
				)
				findNavController().navigateUp()
			} else {
				Toast.makeText(requireContext(), getString(R.string.mark_coo_error), Toast.LENGTH_SHORT).show()
				findNavController().navigateUp()
			}

		}
	}

	private fun workToCancelButton() {
		binding.cancelButton.setOnClickListener {
			findNavController().navigateUp()
		}
	}

	companion object {
		const val COOX = "cooX"
		const val COOY = "cooY"
	}
}