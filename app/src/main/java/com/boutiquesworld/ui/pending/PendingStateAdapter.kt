package com.boutiquesworld.ui.pending

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boutiquesworld.ui.product.ProductsFragment

private const val NUM_PAGES = 3

class PendingStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment =
        ProductsFragment().apply {
            arguments = bundleOf("position" to position + 4)
        }
}