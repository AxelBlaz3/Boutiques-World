package `in`.trendition.ui.order

import `in`.trendition.BuildConfig
import `in`.trendition.R
import `in`.trendition.databinding.FragmentOrderSummaryBinding
import `in`.trendition.ui.MainActivity
import `in`.trendition.ui.address.AddressViewModel
import `in`.trendition.ui.cart.CartViewModel
import `in`.trendition.ui.profile.ProfileViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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

    @Inject
    lateinit var profileViewModel: ProfileViewModel

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
                findNavController().navigate(OrderSummaryFragmentDirections.actionOrderSummaryFragmentToPaymentWaitingBottomSheetDialog())
            }
        }
    }

    private fun startPayment() {
        val checkout = Checkout()
        checkout.setKeyID(BuildConfig.RAZORPAY_API_KEY)
        try {
            val options = JSONObject()
            options.put("name", getString(R.string.app_name))
            options.put("order_id", razorPayOrderId)
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://trendition.in/assets/images/icons/logo_gradient.png")
            options.put("currency", "INR")
            options.put("amount", cartViewModel.cartTotal * 100)

            profileViewModel.getRetailer().value?.let {
                options.put("prefill.email", it.email)
                options.put("prefill.contact", it.mobile)
            }

            val notes = JSONObject()
            notes.put("merchant_order_id", cartViewModel.orderId)
            options.put("notes", notes)

            checkout.open(requireActivity() as MainActivity, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}