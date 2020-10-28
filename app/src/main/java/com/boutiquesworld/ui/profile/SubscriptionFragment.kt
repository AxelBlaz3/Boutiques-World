package com.boutiquesworld.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.boutiquesworld.databinding.FragmentSubscriptionBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SubscriptionFragment : Fragment() {
    private lateinit var binding: FragmentSubscriptionBinding
    private val subscriptionAdapter by lazy {
        SubscriptionAdapter()
    }
    private val rotateAnimation by lazy {
        RotateAnimation(0F, 360F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F).apply {
            duration = 1000
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
        }
    }

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubscriptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            refreshSubscriptionStatus.startAnimation(rotateAnimation)
            refreshSubscriptionStatus.setOnClickListener {
                refreshSubscriptionStatus.startAnimation(rotateAnimation)
                profileViewModel.updateSubscriptionPlans(forceRefresh = true)
                profileViewModel.updateSubscriptionHistory(forceRefresh = true)
            }

            // Refresh subscription status
            profileViewModel.updateSubscriptionHistory(forceRefresh = false)

            subscriptionRecyclerView.adapter = subscriptionAdapter
            profileViewModel.getSubscriptionHistory().observe(viewLifecycleOwner) {
                rotateAnimation.cancel()
                subscriptionAdapter.submitList(it) {
                    if (it.isNotEmpty()) {
                        subscription = it[0]
                        val dateFormat = SimpleDateFormat("MMM d, y")
                        isActive = try {
                            Calendar.getInstance().time.before(dateFormat.parse(it[0].endDate))
                        } catch (e: Exception) {
                            e.printStackTrace()
                            false
                        }
                    } else
                        isActive = false

                    profileViewModel.getSubscriptionPlans().observe(viewLifecycleOwner) { plans ->
                        try {
                            subscriptionPlan =
                                plans.first { subscriptionPlan -> subscriptionPlan.id == it[0].planId }
                        } catch (e: Exception) {
                        }
                    }
                }
            }

            renewButton.setOnClickListener {
                findNavController().navigate(
                    SubscriptionFragmentDirections.actionSubscriptionFragmentToBottomSheetRenewSubscriptionFragment()
                )
            }
        }
    }
}