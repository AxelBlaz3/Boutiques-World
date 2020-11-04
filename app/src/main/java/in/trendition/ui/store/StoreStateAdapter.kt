package `in`.trendition.ui.store

import `in`.trendition.ui.product.ProductsFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val NUM_PAGES = 4

class StoreStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int =
        NUM_PAGES

    override fun createFragment(position: Int): Fragment =
        ProductsFragment().apply { arguments = bundleOf("position" to position) }
}