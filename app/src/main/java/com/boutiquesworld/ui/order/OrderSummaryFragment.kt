package com.boutiquesworld.ui.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentOrderSummaryBinding
import com.boutiquesworld.ui.MainActivity
import com.boutiquesworld.ui.address.AddressViewModel
import com.boutiquesworld.ui.cart.CartViewModel
import com.razorpay.Checkout
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class OrderSummaryFragment : Fragment() {
    private lateinit var binding: FragmentOrderSummaryBinding
    private val summaryAdapter by lazy {
        OrderSummaryAdapter()
    }
    private val args: OrderSummaryFragmentArgs by navArgs()
    private val razorPayOrderId by lazy {
        args.razorPayOrderId
    }
    private val selectedAddressIndex by lazy {
        args.selectedAddressIndex
    }

    @Inject
    lateinit var cartViewModel: CartViewModel

    @Inject
    lateinit var addressViewModel: AddressViewModel

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
        binding = FragmentOrderSummaryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            if (cartViewModel.cartTotal > 1499) {
                summaryDeliveryCharges.visibility = View.GONE
                summaryDeliveryChargesText.visibility = View.GONE
            }
            summaryProductRecyclerView.adapter = summaryAdapter
            summaryOrderTotal.text =
                getString(R.string.product_price, cartViewModel.cartTotal.toString())
            cartViewModel.getCart().observe(viewLifecycleOwner) {
                summaryCartItemCount.text = getString(R.string.cart_items_count, it.size)
                summaryAdapter.submitList(it)
            }

            addressViewModel.getAddressList().observe(viewLifecycleOwner) {
                address = it[selectedAddressIndex]
            }

            placeOrderButton.setOnClickListener {
                startPayment()
            }
        }
    }

    private fun startPayment() {
        val checkout = Checkout()
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
}