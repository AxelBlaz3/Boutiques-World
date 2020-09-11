package com.boutiquesworld.ui.fabrics

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boutiquesworld.ui.order.OrderFragment

private const val NUM_PAGES_DEFAULT = 3
private const val NUM_PAGES_ORDERS = 4

class FabricProductsStateAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int =
        if (fragment is FabricProductsFragment)
            NUM_PAGES_DEFAULT
        else
            NUM_PAGES_ORDERS

    override fun createFragment(position: Int): Fragment =
        if (fragment is FabricProductsFragment)
            ProductStatusFragment()
                .apply { arguments = bundleOf("position" to position) }
        else
            OrderFragment().apply { arguments = bundleOf("position" to position) }
}