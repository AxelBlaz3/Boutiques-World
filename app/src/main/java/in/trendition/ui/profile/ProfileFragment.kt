package `in`.trendition.ui.profile

import `in`.trendition.R
import `in`.trendition.databinding.FragmentProfileBinding
import `in`.trendition.model.About
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
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
                    ArrayList<About>().apply {
                        add(
                            About(
                                icon = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_round_person
                                ), title = getString(R.string.name), detail = it.username
                            )
                        )
                        add(
                            About(
                                icon = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_round_phone
                                ), title = getString(R.string.mobile), detail = it.mobile
                            )
                        )
                        add(
                            About(
                                icon = ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_round_email
                                ), title = getString(R.string.email), detail = it.email
                            )
                        )
                        if (it.businessCategory == "B") {
                            add(
                                About(
                                    icon = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_round_access_time
                                    ), title = getString(R.string.timings), detail = it.timings
                                )
                            )
                            add(
                                About(
                                    icon = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_round_place
                                    ),
                                    title = getString(R.string.address),
                                    detail = it.businessAddress
                                )
                            )
                            add(
                                About(
                                    icon = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_round_pin_drop_24
                                    ), title = getString(R.string.zone), detail = it.zone
                                )
                            )
                        }
                        if (it.businessCategory == "D" || it.businessCategory == "S") {
                            if (it.businessCategory == "D")
                                add(
                                    About(
                                        icon = ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.ic_round_star_24
                                        ),
                                        title = getString(R.string.specialization),
                                        detail = it.specialization
                                    )
                                )
                            else
                                add(
                                    About(
                                        icon = ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.ic_round_category_24
                                        ),
                                        title = getString(R.string.category),
                                        detail = it.specialization
                                    )
                                )
                            add(
                                About(
                                    icon = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_round_flag_24
                                    ), title = getString(R.string.state), detail = it.state
                                )
                            )
                        }
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