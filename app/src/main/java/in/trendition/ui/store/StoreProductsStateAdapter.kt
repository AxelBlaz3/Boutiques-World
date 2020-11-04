package `in`.trendition.ui.store

import `in`.trendition.ui.order.OrderFragment
import `in`.trendition.ui.profile.PaymentsFragment
import `in`.trendition.ui.requests.BaseRequestFragment
import `in`.trendition.ui.requests.BoutiqueRequestFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val NUM_PAGES_DEFAULT = 3
private const val NUM_PAGES_ORDERS = 4
private const val NUM_PAGES_PAYMENTS = 2

class StoreProductsStateAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {

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
            is PaymentsFragment -> OrderFragment().apply {
                arguments = bundleOf("position" to position + 4)
            }
            is BaseRequestFragment -> BoutiqueRequestFragment().apply {
                arguments = bundleOf("position" to position)
            }
            else -> OrderFragment().apply { arguments = bundleOf("position" to position) }
        }
}