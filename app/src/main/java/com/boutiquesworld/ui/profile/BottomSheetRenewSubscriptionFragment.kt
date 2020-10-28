package com.boutiquesworld.ui.profile

import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import com.boutiquesworld.R
import com.boutiquesworld.databinding.FragmentBottomSheetRenewSubscriptionBinding
import com.boutiquesworld.model.SubscriptionPlan
import com.boutiquesworld.ui.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.razorpay.Checkout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import javax.inject.Inject


@AndroidEntryPoint
class BottomSheetRenewSubscriptionFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetRenewSubscriptionBinding

    @Inject
    lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        binding = FragmentBottomSheetRenewSubscriptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Just to make sure plans are updated
        profileViewModel.updateSubscriptionPlans(forceRefresh = true)

        binding.run {
            subscriptionAction.setOnClickListener {
                profileViewModel.getSubscriptionPlans().value?.let { subscriptionPlans ->
                    if (subscriptionPlans.isNotEmpty())
                        if (subscriptionPlans[0].planAmount != 0) {
                            profileViewModel.subscriptionPlan = subscriptionPlans[0]
                            initiatePayment(subscriptionPlans[0])
                        } else {
                            profileViewModel.subscriptionPlan?.let {
                                profileViewModel.makeFreeSubscription(
                                    it.id,
                                    it.planAmount,
                                    (requireActivity() as MainActivity).getFormattedDate(needToday = true),
                                    (requireActivity() as MainActivity).getFormattedDate(
                                        needToday = false,
                                        period = it.planPeriod.toInt()
                                    ),
                                    profileViewModel.subscriptionId
                                )
                            }
                        }
                }
            }

            subscriptionSheetClose.setOnClickListener {
                dismiss()
            }

            profileViewModel.getRazorPayOrderId().observe(viewLifecycleOwner) {
                it?.let {razorPayOrderId ->
                    if (razorPayOrderId.isNotEmpty()) {
                        invokePayment(razorPayOrderId)
                    } else
                        Snackbar.make(binding.root, "Some error occurred", Snackbar.LENGTH_SHORT)
                            .apply {
                                anchorView = (requireActivity() as MainActivity).fab
                                show()
                            }
                }
            }

            profileViewModel.getSubscriptionPlans().observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    subscriptionPlan = it[0]
                    if (it[0].planAmount == 0)
                        strikeThroughAmount(
                            getString(
                                R.string.subscription_price_and_period,
                                "0 1500",
                                it[0].planPeriod
                            )
                        )
                    else
                        subscriptionPriceAndPeriod.text = getString(
                            R.string.subscription_price_and_period,
                            it[0].planAmount.toString(),
                            it[0].planPeriod
                        )
                }
            }

            profileViewModel.getIsSubscriptionSuccessful().observe(viewLifecycleOwner) {
                it?.let { isSubscriptionSuccessful ->
                    if (isSubscriptionSuccessful) {
                        profileViewModel.updateSubscriptionHistory(forceRefresh = true)
                        dismiss()
                    } else {
                        subscriptionAction.isEnabled = true
                        subscriptionSheetClose.isEnabled = true
                        Snackbar.make(binding.root, "Some error occurred", Snackbar.LENGTH_SHORT)
                            .apply {
                                anchorView = (requireActivity() as MainActivity).fab
                                show()
                            }
                    }
                    profileViewModel.resetIsSubscriptionSuccessful()
                }
            }
        }
    }

    private fun startPayment(razorPayOrderId: String, orderId: String, price: String) {
        val checkout = Checkout()
        try {
            val options = JSONObject()
            options.put("name", "Boutiques world")
            options.put("order_id", razorPayOrderId)
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency", "INR")
            options.put("amount", price.toInt() * 100)

            profileViewModel.getRetailer().value?.let {
                options.put("prefill.email", it.email)
                options.put("prefill.contact", it.mobile)
            }

            val notes = JSONObject()
            notes.put("merchant_order_id", orderId)
            options.put("notes", notes)

            checkout.open(requireActivity() as MainActivity, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initiatePayment(subscriptionPlan: SubscriptionPlan?) {
        subscriptionPlan?.let {
            binding.run {
                profileViewModel.getRetailer().value?.let { retailer ->
                    subscriptionAction.isEnabled = false
                    subscriptionSheetClose.isEnabled = false
                    profileViewModel.subscriptionId =
                        "SUBSCRIPTION_${retailer.businessCategory}${retailer.shopId}${System.currentTimeMillis()}"
                    profileViewModel.genRazorPayOrderId(
                        orderId = profileViewModel.subscriptionId,
                        price = subscriptionPlan.planAmount.toString()
                    )
                }
            }
        }
    }

    private fun invokePayment(razorPayOrderId: String) {
        profileViewModel.subscriptionPlan?.let { subscriptionPlan ->
            profileViewModel.getRetailer().value?.let { retailer ->
                profileViewModel.isSubscriptionPayment = true
                startPayment(
                    razorPayOrderId = razorPayOrderId,
                    orderId = profileViewModel.subscriptionId,
                    price = subscriptionPlan.planAmount.toString()
                )
                profileViewModel.resetRazorPayOrderId()
            }
        }
    }

    private fun strikeThroughAmount(content: String) {
        // Strike of 1500 in string @₹0 1500 • 3 months
        val strikePriceText = SpannableString(content)
        strikePriceText.apply {
            setSpan(StrikethroughSpan(), 5, 9, 0)
            setSpan(AbsoluteSizeSpan(24, true), 1, 4, 0);
        }
        binding.subscriptionPriceAndPeriod.text = strikePriceText
    }
}