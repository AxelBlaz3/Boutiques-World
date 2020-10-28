package com.boutiquesworld.ui.requests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentPendingBinding
import com.boutiquesworld.ui.store.FabricProductsStateAdapter
import com.boutiquesworld.ui.profile.ProfileViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BaseRequestFragment: Fragment() {
    private lateinit var binding: FragmentPendingBinding

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPendingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPagerWithTabs()
    }

    private fun setupViewPagerWithTabs() {
        binding.run {
            pendingViewPager.adapter = FabricProductsStateAdapter(this@BaseRequestFragment)
            TabLayoutMediator(pendingTabLayout, pendingViewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.pending)
                    1 -> getString(R.string.accepted)
                    2 -> getString(R.string.cancelled)
                    else -> throw RuntimeException("${this@BaseRequestFragment.javaClass.simpleName}: Unknown position - $position when setting up TabLayout")
                }
            }.attach()
        }
    }
}