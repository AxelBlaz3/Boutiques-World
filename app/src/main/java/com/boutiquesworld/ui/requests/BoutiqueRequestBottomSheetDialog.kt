package com.boutiquesworld.ui.requests

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.boutiquesworld.databinding.FragmentBottomSheetBoutiqueRequestBinding
import com.boutiquesworld.ui.profile.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BoutiqueRequestBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetBoutiqueRequestBinding
    private val args: BoutiqueRequestBottomSheetDialogArgs by navArgs()
    private val boutiqueRequest by lazy {
        args.boutiqueRequest
    }

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBoutiqueRequestBinding.inflate(inflater)
        isCancelable = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            request = boutiqueRequest

            preparationTime.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.simple_spinner_dropdown_item,
                    resources.getStringArray(com.boutiquesworld.R.array.preparation_time_array)
                )
            )

            profileViewModel.getIsPriceEmpty().observe(viewLifecycleOwner) {
                it?.let { isEmpty ->
                    if (isEmpty) {
                        submitResponse.isEnabled = true
                        requestSheetClose.isEnabled = true
                        requestFocusForEditText(price)
                    }
                    profileViewModel.resetIsPriceEmpty()
                }
            }

            profileViewModel.getIsPreparationTimeEmpty().observe(viewLifecycleOwner) {
                it?.let { isEmpty ->
                    if (isEmpty) {
                        submitResponse.isEnabled = true
                        requestSheetClose.isEnabled = true
                        setEmptyDropDownError(preparationTime)
                    }
                    profileViewModel.resetIsPreparationTimeEmpty()
                }
            }

            profileViewModel.getIsBoutiqueResponsePosted().observe(viewLifecycleOwner) {
                it?.let { isPosted ->
                    if (isPosted)
                        dismiss()
                    else {
                        submitResponse.isEnabled = true
                        requestSheetClose.isEnabled = true
                    }
                    profileViewModel.resetIsBoutiqueResponsePosted()
                }
            }

            requestSheetClose.setOnClickListener {
                dismiss()
            }

            submitResponse.setOnClickListener {
                it.isEnabled = false
                requestSheetClose.isEnabled = false

                profileViewModel.submitBoutiqueResponse(
                    boutiqueRequest.id,
                    preparationTime.text.toString(),
                    price.text.toString(),
                    requestStatus = 1
                )
            }
        }
    }

    /**
     * Focus on the field that requires input from the user.
     * @param textInputEditText: EditText for being focused.
     */
    private fun requestFocusForEditText(textInputEditText: TextInputEditText) {
        textInputEditText.requestFocus()
    }

    /**
     * Focus on the field that requires input from the user.
     * @param materialAutoCompleteTextView: AutoCompleteTextView for being focused.
     */
    private fun setEmptyDropDownError(materialAutoCompleteTextView: MaterialAutoCompleteTextView) {
        materialAutoCompleteTextView.requestFocus()
    }
}