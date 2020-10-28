package com.boutiquesworld.ui.store

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boutiquesworld.ui.order.OrderFragment
import com.boutiquesworld.ui.profile.PaymentsFragment
import com.boutiquesworld.ui.requests.BaseRequestFragment
import com.boutiquesworld.ui.requests.BoutiqueRequestFragment

private const val NUM_PAGES_DEFAULT = 3
private const val NUM_PAGES_ORDERS = 4
private const val NUM_PAGES_PAYMENTS = 2

class FabricProductsStateAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int =
        if (fragment is StoreProductsFragment || fragment is BaseRequestFragment)
            NUM_PAGES_DEFAULT
        else if (fragment is PaymentsFragment)
            NUM_PAGES_PAYMENTS
        else
            NUM_PAGES_ORDERS

    override fun createFragment(position: Int): Fragment =
        when (fragment) {
            is StoreProductsFragment -> ProductStatusFragment()
                .apply { arguments = bundleOf("position" to position) }
            is PaymentsFragment -> OrderFragment().apply { arguments = bundleOf("position" to position + 3) }
            is BaseRequestFragment -> BoutiqueRequestFragment().apply { arguments = bundleOf("position" to position) }
            else -> OrderFragment().apply { arguments = bundleOf("position" to position) }
        }
}