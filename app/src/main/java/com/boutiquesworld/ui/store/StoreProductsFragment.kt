package com.boutiquesworld.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentStoreProductsBinding
import com.boutiquesworld.ui.product.ProductsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StoreProductsFragment : Fragment() {
    private lateinit var binding: FragmentStoreProductsBinding

    @Inject
    lateinit var productsViewModel: ProductsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreProductsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPagerWithTabs()
    }

    private fun setupViewPagerWithTabs() {
        binding.run {
            storeProductsViewPager.adapter =
                FabricProductsStateAdapter(this@StoreProductsFragment)
            TabLayoutMediator(storeProductsTabLayout, storeProductsViewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.live)
                    1 -> getString(R.string.progress)
                    2 -> getString(R.string.cancelled)
                    else -> throw RuntimeException("${this@StoreProductsFragment.javaClass.simpleName}: Unknown position - $position when setting up TabLayout")
                }
            }.attach()
        }
    }
}