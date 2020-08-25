package com.boutiquesworld.ui.fabrics.boutiques

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentFabricsBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FabricsFragment : Fragment() {
    private lateinit var binding: FragmentFabricsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFabricsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPagerWithTabs()
    }

    private fun setupViewPagerWithTabs() {
        binding.run {
            fabricsViewPager.adapter =
                FabricsStateAdapter(this@FabricsFragment)
            TabLayoutMediator(fabricsTabLayout, fabricsViewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.fabrics)
                    1 -> getString(R.string.dresses)
                    2 -> getString(R.string.sarees)
                    else -> getString(R.string.dupattas)
                }
            }.attach()
        }
    }
}