package `in`.trendition.ui.order

import `in`.trendition.databinding.FragmentMyOrdersBinding
import `in`.trendition.model.Order
import `in`.trendition.ui.address.AddressViewModel
import `in`.trendition.ui.profile.ProfileViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyOrdersFragment : Fragment(), OrderAdapter.OrderAdapterListener {
    private lateinit var binding: FragmentMyOrdersBinding
    private val myOrdersAdapter by lazy {
        MyOrdersAdapter(this)
    }

    @Inject
    lateinit var ordersViewModel: OrderViewModel

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    @Inject
    lateinit var addressViewModel: AddressViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyOrdersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            myOrdersRecyclerView.adapter = myOrdersAdapter

            // Force refresh address if not yet loaded or empty
            profileViewModel.getRetailer().value?.let {
                if (addressViewModel.getOrderAddressList().value.isNullOrEmpty())
                    addressViewModel.updateOrderAddressList(it.shopId, forceRefresh = true)
            }

            myOrdersSwipeRefresh.setOnRefreshListener {
                ordersViewModel.updateOrders(forceRefresh = true)
                profileViewModel.getRetailer().value?.let {
                    addressViewModel.updateOrderAddressList(it.shopId, forceRefresh = true)
                }
                myOrdersSwipeRefresh.isRefreshing = false
            }

            ordersViewModel.getOrders().observe(viewLifecycleOwner) {
                profileViewModel.getRetailer().value?.let { retailer ->
                    getOrderList(it, retailer.shopId).run {
                        areMyOrdersEmpty = isEmpty()
                        myOrdersAdapter.submitList(this)
                    }
                }
            }
        }

    }

    private fun getOrderList(orders: ArrayList<Order>, shopId: Int): List<Order> {
        try {
            profileViewModel.getRetailer().value?.let {
                return orders.filter { order -> order.userId == shopId && order.businessCategory == it.businessCategory }
                    .sortedByDescending { order -> order.id }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    override fun onOrderItemClicked(order: Order) {
        order.orderId?.let { orderId ->
            val directions =
                MyOrdersFragmentDirections.actionMyOrdersFragmentToOrderTrackingFragment(orderItem = order)
            findNavController().navigate(directions)
        }
    }

    override fun onOrderActionClicked(order: Order) {

    }
}