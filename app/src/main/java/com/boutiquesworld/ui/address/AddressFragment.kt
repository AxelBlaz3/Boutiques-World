package com.boutiquesworld.ui.address

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentAddressBinding
import com.boutiquesworld.ui.MainActivity
import com.boutiquesworld.ui.cart.CartViewModel
import com.boutiquesworld.ui.profile.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.razorpay.Checkout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.address_item.view.*
import kotlinx.coroutines.launch
import org.json.JSONObject
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Preload payment resources
        Checkout.preload(requireContext().applicationContext)
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
                    addNewAddress.visibility = View.GONE
                    addressForm.visibility = View.VISIBLE
                } else {
                    addressForm.visibility = View.GONE
                    addNewAddress.visibility = View.VISIBLE
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
                        addNewAddress.visibility = View.VISIBLE
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
            addNewAddress.setOnClickListener {
                it.visibility = View.GONE
                addressRecyclerView.visibility = View.GONE
                addressForm.visibility = View.VISIBLE
            }
            addressProceed.setOnClickListener {
                addressViewModel.genRazorPayOrderId(orderId, cartViewModel.cartTotal)
            }

            addressViewModel.getRazorPayOrderId().observe(viewLifecycleOwner) {
                Log.d(this@AddressFragment.javaClass.simpleName, "Got new order_id - $it")
                if (it.isNotEmpty()) {
                    startPayment(it)
                }
            }
        }
    }

    private fun startPayment(razorPayOrderId: String) {
        val checkout = Checkout()
        Log.d(this.javaClass.simpleName, "orderId - ${cartViewModel.orderId}")
        Log.d(this.javaClass.simpleName, "cartTotal - ${cartViewModel.cartTotal}")
        try {
            val options = JSONObject()
            options.put("name", "Boutiques world")
            options.put("order_id", razorPayOrderId)
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency", "INR")
            options.put("amount", cartViewModel.cartTotal * 100)

            options.put("prefill.email", "vinayagirichetty@gmail.com")
            options.put("prefill.contact", "9876543210")

            val notes = JSONObject()
            notes.put("merchant_order_id", cartViewModel.orderId)
            options.put("notes", notes)

            checkout.open(requireActivity() as MainActivity, options)
        } catch (e: Exception) {
            e.printStackTrace()
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
                    orderId
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