package com.boutiquesworld.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.boutiquesworld.databinding.FragmentOrderBinding
import com.boutiquesworld.model.Order
import com.boutiquesworld.ui.decoration.CustomDividerItemDecoration
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
                    orderViewModel.updateOrders(it.shopId, forceRefresh = true)
                }
                orderSwipeRefresh.isRefreshing = false
            }

            profileViewModel.getRetailer().observe(viewLifecycleOwner) {
                orderViewModel.updateOrders(it.shopId, forceRefresh = false)
            }

            orderRecyclerView.apply {
                addItemDecoration(CustomDividerItemDecoration())
                adapter = orderAdapter
            }

            orderViewModel.getOrders().observe(viewLifecycleOwner) {
                orderAdapter.submitList(getListForPosition(position, it))
            }
        }
    }

    private fun getListForPosition(position: Int, orders: ArrayList<Order>): List<Order> {
        return when (position) {
            0 -> orders.filter { order -> order.orderStatus == 0 }
            1 -> orders.filter { order -> order.orderStatus == 1 }
            2 -> orders.filter { order -> order.orderStatus == 2 }
            else -> orders.filter { order -> order.orderStatus == 3 }
        }
    }

    override fun onOrderItemClicked(order: Order) {

    }
}
