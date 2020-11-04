package `in`.trendition.ui.profile

import `in`.trendition.R
import `in`.trendition.databinding.FragmentPaymentsBinding
import `in`.trendition.ui.store.StoreProductsStateAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentsFragment : Fragment() {
    private lateinit var binding: FragmentPaymentsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            paymentsViewPager.adapter = StoreProductsStateAdapter(this@PaymentsFragment)
            TabLayoutMediator(paymentsTabLayout, paymentsViewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.pending)
                    1 -> getString(R.string.completed)
                    else -> throw RuntimeException("${this@PaymentsFragment.javaClass.simpleName}: Unknown position - $position when setting up TabLayout")
                }
            }.attach()
        }
    }
}