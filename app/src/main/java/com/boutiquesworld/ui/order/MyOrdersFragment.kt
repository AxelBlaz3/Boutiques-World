package com.boutiquesworld.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.boutiquesworld.databinding.FragmentMyOrdersBinding
import com.boutiquesworld.model.Order
import com.boutiquesworld.ui.address.AddressViewModel
import com.boutiquesworld.ui.profile.ProfileViewModel
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
                    myOrdersAdapter.submitList(getOrderList(it, retailer.shopId))
                }
            }
        }

    }

    private fun getOrderList(orders: ArrayList<Order>, shopId: Int): List<Order> =
        orders.filter { order -> order.userId == shopId }

    override fun onOrderItemClicked(order: Order) {
        order.orderId?.let { orderId ->
            val directions =
                MyOrdersFragmentDirections.actionMyOrdersFragmentToOrderTrackingFragment(
                    orderId = orderId,
                    productId = order.productId.toString()
                )
            findNavController().navigate(directions)
        }
    }

    override fun onOrderActionClicked(order: Order) {

    }
}