package com.boutiquesworld.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentBaseOrderBinding
import com.boutiquesworld.ui.fabrics.FabricProductsStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BaseOrderFragment : Fragment() {
    private lateinit var binding: FragmentBaseOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseOrderBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPagerWithTabs()
    }

    private fun setupViewPagerWithTabs() {
        binding.run {
            baseOrderViewPager.adapter =
                FabricProductsStateAdapter(this@BaseOrderFragment)
            TabLayoutMediator(baseOrderTabLayout, baseOrderViewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.received)
                    1 -> getString(R.string.confirmed)
                    2 -> getString(R.string.dispatched)
                    3 -> getString(R.string.completed)
                    else -> throw RuntimeException("${this@BaseOrderFragment.javaClass.simpleName}: Unknown position - $position when setting up TabLayout")
                }
            }.attach()
        }
    }
}