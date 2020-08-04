package com.boutiquesworld.ui.profile

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentProfileBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            // Show or hide button based on number of lines in description TextView
            description.post {
                showOrHideReadMoreAndReadLess()
            }

            // Set adapter for ViewPager
            pager.adapter = ProfileStateAdapter(this@ProfileFragment)
            TabLayoutMediator(tabLayout, pager) { tab, position ->
                if (position == 0)
                    tab.text = getString(R.string.products)
                else
                    tab.text = getString(R.string.about)
            }.attach()
        }

        profileViewModel.getRetailer().observe(viewLifecycleOwner) {
            binding.retailer = it
        }
    }

    private fun showOrHideReadMoreAndReadLess() {
        binding.apply {
            if (description.lineCount > 2) {
                description.maxLines = 2
                description.ellipsize = TextUtils.TruncateAt.END
                showMoreVisibility = true
            }
        }
    }
}