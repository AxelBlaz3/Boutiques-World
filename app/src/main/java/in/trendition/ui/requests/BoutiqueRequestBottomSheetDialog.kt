package `in`.trendition.ui.requests

import `in`.trendition.R
import `in`.trendition.databinding.FragmentBottomSheetBoutiqueRequestBinding
import `in`.trendition.ui.profile.ProfileViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
    private val boutiqueResponse by lazy {
        args.boutiqueResponse
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
            dialog?.setOnShowListener {
                val dialog = it as BottomSheetDialog
                val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
                bottomSheet?.let { sheet ->
                    dialog.behavior.peekHeight = sheet.height
                    sheet.parent.parent.requestLayout()
                }
            }

            request = boutiqueRequest

            boutiqueResponse?.let {
                preparationTimeResponse =
                    Pair(getString(R.string.preparation_time), it.preparationTime)
                priceResponse = Pair(
                    getString(R.string.price_without_symbol),
                    getString(R.string.product_price, it.price)
                )
            }

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
                        requestFocusForEditText(preparationTime)
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
                        cancelResponse.isEnabled = true
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
                cancelResponse.isEnabled = false
                requestSheetClose.isEnabled = false

                profileViewModel.submitBoutiqueResponse(
                    boutiqueRequest.id,
                    preparationTime.text.toString(),
                    price.text.toString(),
                    requestStatus = 1
                )
            }

            cancelResponse.setOnClickListener {
                it.isEnabled = false
                submitResponse.isEnabled = false
                requestSheetClose.isEnabled = false

                profileViewModel.submitBoutiqueResponse(
                    boutiqueRequest.id,
                    preparationTime.text.toString(),
                    price.text.toString(),
                    requestStatus = 2
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
}