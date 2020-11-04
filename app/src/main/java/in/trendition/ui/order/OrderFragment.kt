package `in`.trendition.ui.order

import `in`.trendition.R
import `in`.trendition.databinding.FragmentOrderBinding
import `in`.trendition.model.Order
import `in`.trendition.model.Payment
import `in`.trendition.ui.profile.PaymentsAdapter
import `in`.trendition.ui.profile.ProfileViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
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
        (binding.root as FrameLayout).layoutTransition.setAnimateParentHierarchy(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            orderSwipeRefresh.setOnRefreshListener {
                profileViewModel.getRetailer().value?.let {
                    orderViewModel.updateOrders(forceRefresh = true)
                    profileViewModel.updatePayments(forceRefresh = true)
                }
                orderSwipeRefresh.isRefreshing = false
            }

            profileViewModel.getRetailer().observe(viewLifecycleOwner) {
                orderViewModel.updateOrders(forceRefresh = false)
            }

            orderRecyclerView.apply {
                // addItemDecoration(CustomDividerItemDecoration())
                adapter = if (position in 0..3)
                    orderAdapter
                else
                    paymentsAdapter
            }

            if (position > -1 && position < 4)
                orderViewModel.getOrders().observe(viewLifecycleOwner) {
                    getOrderListForPosition(position, it).run {
                        areProductsEmpty = isNullOrEmpty()
                        if (isNullOrEmpty())
                            when (position) {
                                0 -> {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_orders
                                    )
                                    illustrationTitle = "Such empty"
                                    illustrationSummary =
                                        "No worries! You'll receive orders soon"
                                }
                                1 -> {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_products
                                    )
                                    illustrationTitle = "Such empty"
                                    illustrationSummary =
                                        "Recently confirmed orders appear here"
                                }
                                2 -> {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_products
                                    )
                                    illustrationTitle = "Such empty!"
                                    illustrationSummary =
                                        "Dispatched orders appear here"
                                }
                                3 -> {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_pending_products
                                    )
                                    illustrationTitle = "Oops"
                                    illustrationSummary =
                                        "None of your orders are completed yet"
                                }
                            }
                        orderAdapter.submitList(this)
                    }
                }
            else // Payments
                profileViewModel.getPayments().observe(viewLifecycleOwner) {
                    getPaymentsListForPosition(position, it).run {
                        areProductsEmpty = isNullOrEmpty()
                        if (isNullOrEmpty())
                            when (position) {
                                4 -> {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_pending_products
                                    )
                                    illustrationTitle = "Such empty"
                                    illustrationSummary =
                                        "You don't have any payments pending"
                                }
                                5 -> {
                                    illustration = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_no_pending_products
                                    )
                                    illustrationTitle = "Oops"
                                    illustrationSummary =
                                        "None of your payments are completed yet"
                                }
                            }
                        paymentsAdapter.submitList(this)
                    }
                }

        }
    }

    private fun getOrderListForPosition(position: Int, orders: ArrayList<Order>): List<Order> {
        profileViewModel.getRetailer().value?.let {
            try {
                return when (position) {
                    0 -> orders.filter { order -> order.orderStatus == 0 && order.businessId == it.shopId }
                        .sortedByDescending { order -> order.id }
                    1 -> orders.filter { order -> order.orderStatus == 1 && order.businessId == it.shopId }
                        .sortedByDescending { order -> order.id }
                    2 -> orders.filter { order -> (order.orderStatus == 2 || order.orderStatus == 3 || order.orderStatus == 4) && order.businessId == it.shopId }
                        .sortedByDescending { order -> order.id }
                    3 -> orders.filter { order -> (order.orderStatus == 5 || order.orderStatus == 6) && order.businessId == it.shopId }
                        .sortedByDescending { order -> order.id }
                    else -> throw IllegalArgumentException("Unknown position - $position while getting orders list")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ArrayList()
    }

    private fun getPaymentsListForPosition(
        position: Int,
        payments: ArrayList<Payment>
    ): List<Payment> {
        profileViewModel.getRetailer().value?.let {
            try {
                return when (position) {
                    4 -> payments.filter { payment -> payment.paymentStatus == 0 }
                        .sortedByDescending { payment -> payment.id }
                    5 -> payments.filter { payment -> payment.paymentStatus == 1 }
                        .sortedByDescending { payment -> payment.id }
                    else -> throw IllegalArgumentException("Unknown position $position while getting payments list")
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
