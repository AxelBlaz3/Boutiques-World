package com.boutiquesworld.ui.fabrics

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val NUM_PAGES = 3

class FabricProductsStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int =
        NUM_PAGES

    override fun createFragment(position: Int): Fragment =
        ProductStatusFragment()
            .apply { arguments = bundleOf("position" to position) }
}