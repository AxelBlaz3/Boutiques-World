package com.boutiquesworld.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.boutiquesworld.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    private val aboutAdapter by lazy {
        AboutAdapter()
    }

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

            aboutRecyclerView.adapter = aboutAdapter

            profileViewModel.getRetailer().observe(viewLifecycleOwner) {
                retailer = it
                isBoutique = it.businessCategory == "B"
                val retailerInformation =
                    ArrayList<String>().apply {
                        add(it.username)
                        add(it.mobile)
                        add(it.email)
                        if (it.businessCategory == "B")
                            add(it.zone)
                    }
                aboutAdapter.submitList(retailerInformation)
            }
        }
    }

    private fun showOrHideReadMoreAndReadLess() {
        binding.apply {
            if (description.lineCount > 2) {
                totalLines = description.lineCount
                description.maxLines = 2
                showMoreVisibility = true
            }
        }
    }
}