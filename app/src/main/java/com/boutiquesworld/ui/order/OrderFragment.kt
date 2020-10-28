package com.boutiquesworld.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentOrderBinding
import com.boutiquesworld.model.Order
import com.boutiquesworld.model.Payment
import com.boutiquesworld.ui.decoration.CustomDividerItemDecoration
import com.boutiquesworld.ui.profile.PaymentsAdapter
import com.boutiquesworld.ui.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrderFragment : Fragment(), OrderAdapter.OrderAdapterListener {
    private lateinit var binding: FragmentOrderBinding

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    @Inject
    lateinit var orderViewModel: OrderViewModel

    private val orderAdapter by lazy {
        OrderAdapter(this)
    }

    private val paymentsAdapter by lazy {
        PaymentsAdapter()
    }

    private var position = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.get("position") as Int
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            orderSwipeRefresh.setOnRefreshListener {
                profileViewModel.getRetailer().value?.let {
                    orderViewModel.updateOrders(forceRefresh = true)
                }
                orderSwipeRefresh.isRefreshing = false
            }

            profileViewModel.getRetailer().observe(viewLifecycleOwner) {
                orderViewModel.updateOrders(forceRefresh = false)
            }

            orderRecyclerView.apply {
                // addItemDecoration(CustomDividerItemDecoration())
                adapter = if (position in 0..2)
                    orderAdapter
                else
                    paymentsAdapter
            }

            if (position in 0..2)
                orderViewModel.getOrders().observe(viewLifecycleOwner) {
                    getOrderListForPosition(position, it).apply {
                        (view as FrameLayout).layoutTransition.setAnimateParentHierarchy(false)
                        areProductsEmpty = isNullOrEmpty()
                        if (isNullOrEmpty())
                            when (position) {
                                4 -> {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_products
                                    )
                                    illustrationTitle = "Start submitting"
                                    illustrationSummary =
                                        "No products yet. Start by clicking the add button"
                                }
                                5 -> {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_pending_products
                                    )
                                    illustrationTitle = "Such empty"
                                    illustrationSummary =
                                        "Recently submitted products appear here"
                                }
                                6 -> {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_cancelled_products
                                    )
                                    illustrationTitle = "Yay!"
                                    illustrationSummary =
                                        "None of your products are cancelled"
                                }
                            }
                        orderAdapter.submitList(this)
                    }
                }
            else // Payments
                profileViewModel.getPayments().observe(viewLifecycleOwner) {
                    paymentsAdapter.submitList(getPaymentsListForPosition(position, it))
                }

        }
    }

    private fun getOrderListForPosition(position: Int, orders: ArrayList<Order>): List<Order> {
        profileViewModel.getRetailer().value?.let {
            return when (position) {
                0 -> orders.filter { order -> order.orderStatus == 0 && order.businessId == it.shopId }
                1 -> orders.filter { order -> order.orderStatus == 1 && order.businessId == it.shopId }
                2 -> orders.filter { order -> order.orderStatus == 2 && order.businessId == it.shopId }
                else -> orders.filter { order -> order.orderStatus == 3 && order.businessId == it.shopId }
            }
        }
        return ArrayList()
    }

    private fun getPaymentsListForPosition(position: Int, payments: ArrayList<Payment>): List<Payment> {
        profileViewModel.getRetailer().value?.let {
            return when (position) {
                3 -> payments.filter { payment -> payment.paymentStatus == 0}
                4 -> payments.filter { payment -> payment.paymentStatus == 1}
                else -> throw IllegalArgumentException("Unknown position $position while getting payments list")
            }
        }
        return ArrayList()
    }

    override fun onOrderItemClicked(order: Order) {

    }

    override fun onOrderActionClicked(order: Order) {
        findNavController().navigate(
            BaseOrderFragmentDirections.actionBaseOrderFragmentToOrderConfirmBottomSheetDialog(
                order
            )
        )
    }
}
