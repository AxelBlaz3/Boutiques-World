package com.boutiquesworld.ui.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentAddressBinding
import com.boutiquesworld.ui.MainActivity
import com.boutiquesworld.ui.cart.CartViewModel
import com.boutiquesworld.ui.profile.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.address_item.view.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddressFragment : Fragment(), AddressAdapter.AddressAdapterListener {
    private lateinit var binding: FragmentAddressBinding

    @Inject
    lateinit var addressViewModel: AddressViewModel

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    @Inject
    lateinit var cartViewModel: CartViewModel

    // Used to track the currently and previously selected address indices
    // 0th index holds the currently selected item, 1st index holds previously selected item.
    private var addressSelectionIndices = mutableListOf(0, 0)
    private val addressAdapter by lazy {
        AddressAdapter(this, addressSelectionIndices)
    }

    private val args: AddressFragmentArgs by navArgs()
    private val userId by lazy {
        args.userId
    }
    private val orderId by lazy {
        args.orderId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            addressRecyclerView.adapter = addressAdapter

            profileViewModel.getRetailer().observe(viewLifecycleOwner) {
                addressViewModel.updateAddressList(it.shopId, forceRefresh = false)
            }

            addressViewModel.getAddressList().observe(viewLifecycleOwner) {
                addressAdapter.submitList(it)
                if (it.isEmpty()) {
                    addressRecyclerView.visibility = View.GONE
                    (requireActivity() as MainActivity).toolbar?.menu?.findItem(R.id.add_address)?.isVisible = false
                    addressForm.visibility = View.VISIBLE
                    addressProceed.visibility = View.GONE
                } else {
                    addressForm.visibility = View.GONE
                    addressProceed.visibility = View.VISIBLE
                    (requireActivity() as MainActivity).toolbar?.menu?.findItem(R.id.add_address)?.isVisible = true
                    addressRecyclerView.visibility = View.VISIBLE
                }
            }
            addressViewModel.getAddressName().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(userName)
            }
            addressViewModel.getAddressMobile().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(addressNumber)
            }
            addressViewModel.getAddressPincode().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(addressPincode)
            }
            addressViewModel.getAddressFlat().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(addressFlatNo)
            }
            addressViewModel.getAddressArea().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(addressArea)
            }
            addressViewModel.getAddressLandmark().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(addressLandmark)
            }
            addressViewModel.getAddressTown().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(addressTown)
            }
            addressViewModel.getAddressState().observe(viewLifecycleOwner) {
                if (it)
                    requestFocusForEditText(addressState)
            }
            addressViewModel.getIsAddressPosted().observe(viewLifecycleOwner) { isPosted ->
                submitAddress.isEnabled = true
                isPosted?.let {
                    if (it) {
                        addressForm.visibility = View.GONE
                        addressProceed.visibility = View.VISIBLE
                        (requireActivity() as MainActivity).toolbar?.menu?.findItem(R.id.add_address)?.isVisible = true
                        addressRecyclerView.visibility = View.VISIBLE
                        resetAddressFields()
                        addressViewModel.updateAddressList(
                            profileViewModel.getRetailer().value!!.shopId,
                            forceRefresh = true
                        )
                    } else
                        Snackbar.make(
                            view,
                            getString(R.string.some_error_occured),
                            Snackbar.LENGTH_SHORT
                        ).apply {
                            setAction(getString(R.string.retry)) { onSubmitButtonClick() }
                            show()
                        }
                    addressViewModel.resetIsPosted()
                }
            }
            submitAddress.setOnClickListener { onSubmitButtonClick() }

            (requireActivity() as MainActivity).toolbar?.menu?.findItem(R.id.add_address)?.setOnMenuItemClickListener {
                it.isVisible = false
                addressRecyclerView.visibility = View.GONE
                addressForm.visibility = View.VISIBLE
                addressProceed.visibility = View.GONE
                return@setOnMenuItemClickListener true
            }

            /*addNewAddress.setOnClickListener {
                (requireActivity() as MainActivity).toolbar?.menu?.findItem(R.id.add_address)?.isVisible = false
                it.visibility = View.GONE
                addressRecyclerView.visibility = View.GONE
                addressForm.visibility = View.VISIBLE
                addressProceed.visibility = View.GONE
            }*/
            addressProceed.setOnClickListener {
                addressViewModel.getAddressList().value?.let {
                    if (it.isNotEmpty())
                        addressViewModel.genRazorPayOrderId(
                            cartViewModel.orderId,
                            cartViewModel.cartTotal,
                            it[addressSelectionIndices[0]].copy(orderId = cartViewModel.orderId)
                        )
                }
            }

            addressViewModel.getRazorPayOrderId().observe(viewLifecycleOwner) {
                addressViewModel.getIsOrderAddressPosted().value?.let { isOrderAddressPosted ->
                    if (it.isNotEmpty() && isOrderAddressPosted) {
                        addressViewModel.resetRazorPayOrderId()
                        findNavController().navigate(
                            AddressFragmentDirections.actionAddressFragmentToOrderSummaryFragment(
                                razorPayOrderId = it,
                                selectedAddressIndex = addressSelectionIndices[0]
                            )
                        )
                        addressViewModel.resetIsOrderAddressPosted()
                    }
                }
            }
        }
    }

    private fun onSubmitButtonClick() {
        binding.run {
            submitAddress.isEnabled = false
            lifecycleScope.launch {
                addressViewModel.postAddress(
                    userName.text.toString(),
                    addressNumber.text.toString(),
                    addressPincode.text.toString(),
                    addressFlatNo.text.toString(),
                    addressArea.text.toString(),
                    addressLandmark.text.toString(),
                    addressTown.text.toString(),
                    addressState.text.toString(),
                    userId,
                    cartViewModel.orderId
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
        binding.submitAddress.isEnabled = true
    }

    private fun resetAddressFields() {
        binding.apply {
            userName.setText("")
            addressNumber.setText("")
            addressPincode.setText("")
            addressFlatNo.setText("")
            addressArea.setText("")
            addressLandmark.setText("")
            addressTown.setText("")
            addressState.setText("")
        }
    }

    override fun onAddressCardClick(position: Int) {
        if (position == addressSelectionIndices[0])
            return

        binding.addressRecyclerView.layoutManager?.let { layoutManager ->
            addressSelectionIndices[1] = addressSelectionIndices[0]
            addressSelectionIndices[0] = position
            val viewToCheck = layoutManager.findViewByPosition(addressSelectionIndices[0])
            val viewToUncheck = layoutManager.findViewByPosition(addressSelectionIndices[1])
            viewToCheck?.let { view ->
                view.address_radio_button.isChecked = !view.address_radio_button.isChecked
            }
            viewToUncheck?.let { view ->
                view.address_radio_button.isChecked = !view.address_radio_button.isChecked
            }
        }
    }
}