package com.boutiquesworld.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.boutiquesworld.databinding.FragmentAboutBinding
import com.boutiquesworld.model.Retailer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AboutFragment : Fragment() {
    private lateinit var binding: FragmentAboutBinding

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    private val retailer: Retailer by lazy {
        profileViewModel.getRetailer().value!!
    }

    private val aboutAdapter by lazy {
        AboutAdapter()
    }

    private val retailerInformation by lazy {
        ArrayList<String>().apply {
            add(retailer.username)
            add(retailer.mobile)
            add(retailer.email)
            add(retailer.zone)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.aboutRecyclerView.adapter = aboutAdapter
        aboutAdapter.submitList(retailerInformation)
    }
}