package com.boutiquesworld.ui.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.boutiquesworld.databinding.FragmentOrderTrackingBinding
import com.boutiquesworld.ui.address.AddressViewModel
import com.boutiquesworld.util.TimeUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderTrackingFragment : Fragment() {
    private lateinit var binding: FragmentOrderTrackingBinding
    private val args: OrderTrackingFragmentArgs by navArgs()
    private val orderId by lazy {
        args.orderId
    }
    private val productId by lazy {
        args.productId
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

            orderViewModel.getOrders().observe(viewLifecycleOwner) {
                if (it.isNotEmpty())
                    orderItem =
                        it.first { order -> order.orderId == orderId && order.productId.toString() == productId }
            }

            addressViewModel.getOrderAddressList().observe(viewLifecycleOwner) {
                if (it.isNotEmpty())
                    address =
                        it.first { address -> address.orderId == orderId}
            }
        }
    }
}