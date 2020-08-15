package com.boutiquesworld.ui.fabrics

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boutiquesworld.ui.product.ProductsFragment

private const val NUM_PAGES = 4

class FabricsStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment = ProductsFragment(position)
}