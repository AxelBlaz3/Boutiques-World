package `in`.trendition.ui.order

import `in`.trendition.databinding.FragmentOrderTrackingBinding
import `in`.trendition.ui.address.AddressViewModel
import `in`.trendition.util.TimeUtils
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderTrackingFragment : Fragment() {
    private lateinit var binding: FragmentOrderTrackingBinding
    private val args: OrderTrackingFragmentArgs by navArgs()
    private val orderItem by lazy {
        args.orderItem
    }

    @Inject
    lateinit var addressViewModel: AddressViewModel

    @Inject
    lateinit var orderViewModel: OrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderTrackingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            timeUtils = TimeUtils
            orderItem = this@OrderTrackingFragment.orderItem

            addressViewModel.getOrderAddressList().observe(viewLifecycleOwner) {
                if (it.isNotEmpty())
                    try {
                        address =
                            it.firstOrNull { address -> address.orderId == this@OrderTrackingFragment.orderItem.orderId }
                    } catch (e: Exception) {
                    }
            }
        }
    }
}