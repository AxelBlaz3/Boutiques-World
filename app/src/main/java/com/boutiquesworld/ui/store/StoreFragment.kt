package com.boutiquesworld.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentStoreBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreFragment : Fragment() {
    private lateinit var binding: FragmentStoreBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPagerWithTabs()
    }

    private fun setupViewPagerWithTabs() {
        binding.run {
            storeViewPager.adapter =
                StoreStateAdapter(this@StoreFragment)
            TabLayoutMediator(storeTabLayout, storeViewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.fabrics)
                    1 -> getString(R.string.clothes)
                    2 -> getString(R.string.dress_materials)
                    else -> getString(R.string.jewellery)
                }
            }.attach()
        }
    }
}