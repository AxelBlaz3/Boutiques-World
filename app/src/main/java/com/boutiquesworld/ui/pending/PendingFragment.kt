package com.boutiquesworld.ui.pending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentPendingBinding
import com.boutiquesworld.ui.profile.ProfileViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PendingFragment : Fragment() {
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
            profileViewModel.getRetailer().value?.let {
                pendingViewPager.adapter = PendingStateAdapter(
                    this@PendingFragment,
                    it.businessCategory
                )
            }
            TabLayoutMediator(pendingTabLayout, pendingViewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.live)
                    1 -> getString(R.string.progress)
                    2 -> getString(R.string.cancelled)
                    else -> throw RuntimeException("${this@PendingFragment.javaClass.simpleName}: Unknown position - $position when setting up TabLayout")
                }
            }.attach()
        }
    }
}