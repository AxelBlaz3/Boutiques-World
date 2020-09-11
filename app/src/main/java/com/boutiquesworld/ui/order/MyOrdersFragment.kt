package com.boutiquesworld.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.boutiquesworld.databinding.FragmentMyOrdersBinding
import com.boutiquesworld.model.Order
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

            myOrdersSwipeRefresh.setOnRefreshListener {
                ordersViewModel.updateOrders(forceRefresh = true)
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

    }
}