package com.boutiquesworld.ui.profile

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boutiquesworld.ui.product.ProductsFragment

private const val NUM_PAGES = 2

class ProfileStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment =
        if (position == 0)
            ProductsFragment().apply { arguments = bundleOf("position" to -1) }
        else
            AboutFragment()
}