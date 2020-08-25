package com.boutiquesworld.ui.fabrics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentFabricProductsBinding
import com.boutiquesworld.ui.product.ProductsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FabricProductsFragment : Fragment() {
    private lateinit var binding: FragmentFabricProductsBinding

    @Inject
    lateinit var productsViewModel: ProductsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFabricProductsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPagerWithTabs()
    }

    private fun setupViewPagerWithTabs() {
        binding.run {
            fabricProductsViewPager.adapter =
                FabricProductsStateAdapter(this@FabricProductsFragment)
            TabLayoutMediator(fabricProductsTabLayout, fabricProductsViewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.live)
                    1 -> getString(R.string.progress)
                    2 -> getString(R.string.cancelled)
                    else -> throw RuntimeException("${this@FabricProductsFragment.javaClass.simpleName}: Unknown position - $position when setting up TabLayout")
                }
            }.attach()
        }
    }
}